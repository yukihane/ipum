/* $Id$ */
package yukihane.ipum;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yukihane.ipum.gui.Status;
import yukihane.ipum.nicovideo.NicoVideoInfo;
import yukihane.ipum.nicovideo.NicoVideoInfoManager;
import yukihane.swf.Cws2Fws;

public class Converter implements Callable<File> {

    private static Logger log = LoggerFactory.getLogger(Converter.class);
    private final LinkedBlockingQueue<Event> queue;
    private final Config config;
    private final File file;

    public Converter(LinkedBlockingQueue<Event> statusQueue, Config config, File file) {
        this.queue = statusQueue;
        this.config = config;
        this.file = file;
    }

    public File call() throws Exception {
        Status status = new Status();
        status.setState(Status.State.CONVERTING);
        try {
            queue.put(new Event(this.file, status));
        } catch (InterruptedException ex) {
            log.error("キューイング失敗", ex);
        }

        int res = -1;
        final SrcFileType type = SrcFileType.fromFileExt(file);
        if (type == null) {
            status.setState(Status.State.FAIL);
            Event event = new Event(file, status);
            try {
                queue.put(event);
            } catch (InterruptedException ex) {
                log.error("キューイング失敗", ex);
            }
            return null;
        }

        CommandLine commandLine = CommandLine.parse(config.getFfmpegPath().toString());
        String[] defArgs = {"-y", "-acodec", "${acodec}", "-i", "${infile}", "${outfile}"};
        commandLine.addArguments(defArgs);

        File tmpInFile = null;
        File tmpOutFile = null;
        try {
            final DstFileType dstType = getDstType(file);
            if (dstType == DstFileType.AAC) {
                tmpOutFile = File.createTempFile("tmp", "." + DstFileType.AAC.getExtension(), config.getTempDir());
                tmpOutFile.deleteOnExit();
            } else {
                tmpOutFile = new File(config.getOutputDir(), FilenameUtils.getBaseName(file.toString()) + "." + dstType.
                        getExtension());
            }

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("acodec", "copy");
            tmpInFile = File.createTempFile("tmp", "." + FilenameUtils.getExtension(file.toString()), config.getTempDir());
            if (type == SrcFileType.SWF && Cws2Fws.isCws(file)) {
                Cws2Fws.createFws(file, tmpInFile);
            } else {
                FileUtils.copyFile(file, tmpInFile);
            }
            params.put("infile", tmpInFile.toString());

            params.put("outfile", tmpOutFile.toString());
            commandLine.setSubstitutionMap(params);
            log.info("COMMAND: " + commandLine);

            Executor executor = new DefaultExecutor();
            executor.execute(commandLine);

            if (dstType == DstFileType.AAC) {
                File realOutFile = new File(config.getOutputDir(), FilenameUtils.getBaseName(file.toString()) + ".m4a");
                CommandLine mp4box = CommandLine.parse(config.getMp4boxPath().toString());
                mp4box.addArguments(new String[]{"-new", "-add", tmpOutFile.toString(), realOutFile.toString()});
                log.info("MP4BOX: " + mp4box);
                res = new DefaultExecutor().execute(mp4box);
                tmpOutFile.delete();
                tmpOutFile = realOutFile;
            }
        } catch (Exception ex) {
            log.error("変換エラー", ex);
        } finally {
            if (tmpInFile != null && tmpInFile.exists()) {
                tmpInFile.delete();
            }
        }

        status = new Status();
        if (res != 0) {
            status.setState(Status.State.FAIL);
            Event event = new Event(file, status);
            try {
                queue.put(event);
            } catch (InterruptedException ex) {
                log.error("キューイング失敗", ex);
            }
            return null;
        }

        if (config.isUseID3()) {
            createID3(config, tmpOutFile);
        }

        status.setState(Status.State.DONE);
        Event event = new Event(file, status);
        try {
            queue.put(event);
        } catch (InterruptedException ex) {
            log.error("キューイング失敗", ex);
        }

        return tmpOutFile;
    }

    private void createID3(Config config, File file) throws IOException {
        File artWorkFile = null;
        try {
            NicoVideoInfoManager manager = NicoVideoInfoManager.getInstance();
            NicoVideoInfo cont = manager.findNicoContent(file.getName());
            AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();

            artWorkFile = cont.getArtWork(config.getTempDir());
            Artwork artWork = Artwork.createArtworkFromFile(artWorkFile);
            artWork.setImageUrl(cont.getThumbnailUrl().toString());
            tag.addField(artWork);
            tag.addField(FieldKey.TITLE, cont.getTitle());
            tag.addField(FieldKey.COMMENT, cont.getVideoId() + "; " + cont.getDescription());
            Calendar cal = Calendar.getInstance();
            cal.setTime(cont.getFirstRetrieve());
            tag.addField(FieldKey.YEAR, Integer.toString(cal.get(Calendar.YEAR)));
            tag.addField(FieldKey.GENRE, "ニコニコ動画");
            if (cont.getAuthor() != null) {
                tag.addField(FieldKey.ARTIST, cont.getAuthor());
            }
            if (config.useTitleAsAlbum()) {
                tag.addField(FieldKey.ALBUM, cont.getTitle());
            }
            f.commit();
        } catch (Exception e) {
            log.error("ID3タグ作成に失敗: " + FilenameUtils.getName(file.toString()), e);
            throw new IOException(e);
        } finally {
            if (artWorkFile != null) {
                artWorkFile.delete();
            }
        }
    }

    private DstFileType getDstType(File file) throws IOException {

        final String args[] = {config.getFfmpegPath().toString(), "-i", file.toString()};
        final Process process = Runtime.getRuntime().exec(args);
        final InputStream es = process.getErrorStream();
        final BufferedReader isr = new BufferedReader(new InputStreamReader(es));

        final Pattern pattern = Pattern.compile("Stream #.*?: Audio: (.*?),");
        String line;
        while ((line = isr.readLine()) != null) {
            final Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                final String type = matcher.group(1);
                return DstFileType.from(type);
            }
        }

        log.warn("出力タイプ不明のためMP3と推定します: " + file);
        return DstFileType.MP3;
    }
}
