/* $Id$ */
package yukihane.ipum;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import yukihane.ipum.gui.Status;
import yukihane.ipum.nicovideo.NicoVideoInfo;
import yukihane.ipum.nicovideo.NicoVideoInfoManager;
import yukihane.swf.Cws2Fws;

public class Converter implements Callable<File> {

    private final LinkedBlockingQueue<Event> queue;
    private final Config config;
    private final File file;

    public Converter(LinkedBlockingQueue<Event> statusQueue, Config config, File file) {
        this.queue = statusQueue;
        this.config = config;
        this.file = file;
    }

    public File call() {
        Status status = new Status();
        status.setState(Status.State.CONVERTING);
        try {
            queue.put(new Event(this.file, status));
        } catch (InterruptedException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }

        int res = -1;
        File tmpFile = null;
        final SrcFileType type = SrcFileType.fromFileExt(file);
        if (type == null) {
            status.setState(Status.State.FAIL);
            Event event = new Event(file, status);
            try {
                queue.put(event);
            } catch (InterruptedException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        CommandLine commandLine = CommandLine.parse(config.getFfmpegPath().toString());
        String[] defArgs = new String[]{"-y", "-acodec", "${acodec}", "-i", "${infile}", "${outfile}"};
        commandLine.addArguments(defArgs);

        File outfile = null;
        try {
            if (type.getDstFileType() == DstFileType.AAC) {
                outfile = File.createTempFile("tmp", "." + DstFileType.AAC.getExtension(), config.getTempDir());
                outfile.deleteOnExit();
            } else {
                outfile = new File(config.getOutputDir(), FilenameUtils.getBaseName(file.toString()) + "." + type.
                        getDstFileType().getExtension());
            }

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("acodec", "copy");
            if (type == SrcFileType.SWF && Cws2Fws.isCws(file)) {
                tmpFile = File.createTempFile("tmp", "." + FilenameUtils.getExtension(file.toString()), config.
                        getTempDir());
                tmpFile.deleteOnExit();
                Cws2Fws.createFws(file, tmpFile);
                params.put("infile", tmpFile.toString());
            } else {
                params.put("infile", file.toString());
            }
            params.put("outfile", outfile.toString());
            commandLine.setSubstitutionMap(params);
            System.out.println(commandLine);

            Executor executor = new DefaultExecutor();
            res = executor.execute(commandLine);

            if (res == 0 && type.getDstFileType() == DstFileType.AAC) {
                File realOutFile = new File(config.getOutputDir(), FilenameUtils.getBaseName(file.toString()) + ".m4a");
                CommandLine mp4box = CommandLine.parse(config.getMp4boxPath().toString());
                mp4box.addArguments(new String[]{"-add", outfile.toString(), realOutFile.toString()});
                System.out.println(mp4box);
                res = new DefaultExecutor().execute(mp4box);
                outfile.delete();
                outfile = realOutFile;
            }
        } catch (Exception ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (tmpFile != null && tmpFile.exists()) {
                tmpFile.delete();
            }
        }

        status = new Status();
        if (res != 0) {
            status.setState(Status.State.FAIL);
            Event event = new Event(file, status);
            try {
                queue.put(event);
            } catch (InterruptedException ex) {
                Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        if (config.isUseID3()) {
            createID3(config, outfile);
        }

        status.setState(Status.State.DONE);
        Event event = new Event(file, status);
        try {
            queue.put(event);
        } catch (InterruptedException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return outfile;
    }

    private void createID3(Config config, File file) {
        File artWorkFile = null;
        try {
            NicoVideoInfoManager manager = NicoVideoInfoManager.getInstance();
            NicoVideoInfo cont = manager.findNicoContent(file.getName());
            AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();

            artWorkFile = cont.getArtWork(config.getTempDir());
            Artwork artWork = Artwork.createArtworkFromFile(artWorkFile);
            artWork.setImageUrl(cont.getThumbnailUrl().toString());
            tag.createAndSetArtworkField(artWork);
            tag.setTitle(cont.getTitle());
            tag.setComment(cont.getVideoId() + "; " + cont.getDescription());
            Calendar cal = Calendar.getInstance();
            cal.setTime(cont.getFirstRetrieve());
            tag.addYear(Integer.toString(cal.get(Calendar.YEAR)));
            tag.setGenre("ニコニコ動画");
            if (cont.getAuthor() != null) {
                tag.setArtist(cont.getAuthor());
            }
            f.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "ID3タグ作成に失敗: " + FilenameUtils.getName(file.
                    toString()), e);
        } finally {
            if (artWorkFile != null) {
                artWorkFile.delete();
            }
        }
    }
}
