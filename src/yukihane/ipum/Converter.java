/* $Id$ */
package yukihane.ipum;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FilenameUtils;

class Converter implements Callable<File> {

    private final Config config;
    private final File file;

    Converter(Config config, File file) {
        this.config = config;
        this.file = file;
    }

    public File call() {
        SrcFileType type = SrcFileType.fromFileExt(file);
        if (type == null) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        CommandLine commandLine = CommandLine.parse(config.getFfmpegPath().toString());
        final String[] defArgs = new String[]{"-y", "-acodec", "${acodec}", "-i", "${infile}", "${outfile}"};
        commandLine.addArguments(defArgs);

        File outfile = new File(config.getOutputDir(), FilenameUtils.getBaseName(file.toString()) + "." + type.
                getDstFileType().getExtension());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("acodec", "copy");
        params.put("infile", file.toString());
        params.put("outfile", outfile.toString());

        commandLine.setSubstitutionMap(params);

        System.out.println(commandLine);

        Executor executor = new DefaultExecutor();
        int res = -1;
        try {
            res = executor.execute(commandLine);
        } catch (Exception ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (res != 0) {
            return null;
        }
        return outfile;
    }
}
