/* $Id$ */
package yukihane.ipum;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yuki
 */
public class Config {

    private final static String APPLICATION_NAME = "ipum";
    private final static String CONFIG_NAME = APPLICATION_NAME + ".xml";
    private static Logger log = LoggerFactory.getLogger(Config.class);
    private static Config instance = new Config();
    private static XMLConfiguration properties;

    public static Config getInstance() {
        return instance;
    }

    /** FFMPEGのパス. */
    public File getFfmpegPath() {
        return new File(properties.getString("path.ffmpeg"));
    }

    /** MP4Boxのパス. */
    public File getMp4boxPath() {
        return new File(properties.getString("path.mp4box"));
    }

    /** 変換後ファイルの出力先. */
    /** FFMPEGを何個同時起動するか. */
    public File getOutputDir() {
        return new File(properties.getString("path.output"));
    }

    /** 作業用の一時ファイルを置くディレクトリ. */
    public File getTempDir() {
        return new File(properties.getString("path.temp"));
    }

    /** FFMPEGをいくつ同時起動するか. */
    public int getThreadNum() {
        return properties.getInt("ffmpeg.threadnum");
    }

    /** ID3タグに埋め込みを行うか. */
    public boolean isUseID3() {
        return properties.getBoolean("convertopt.useid3");
    }

    /**
     * タイトルをアルバム名として設定するか.
     */
    boolean useTitleAsAlbum() {
        return properties.getBoolean("convertopt.use_title_as_album", false);
    }

    {
        final File appDir = new File(System.getProperty("user.home", "."), "." + APPLICATION_NAME).getAbsoluteFile();
        final File config = new File(appDir, CONFIG_NAME);

        properties = new XMLConfiguration();
        try {
            properties.setFile(config);
            if (!config.exists()) {
                initializeProperties();
            }
            properties.load();
        } catch (ConfigurationException ex) {
            log.error("コンフィグ読み込み失敗", ex);
        }
    }

    private Config() {
    }

//    static synchronized void saveConfig(Config config) {
//        try {
//            instance = (Config) config.clone();
//            properties.save();
//        } catch (ConfigurationException ex) {
//            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (CloneNotSupportedException ex) {
//            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException(ex);
//        }
//    }
    private void initializeProperties() throws ConfigurationException {
        if (SystemUtils.IS_OS_WINDOWS) {
            properties.addProperty("path.ffmpeg", new File("bin/win", "ffmpeg.exe").toString());
            properties.addProperty("path.mp4box", new File("bin/win", "MP4Box.exe").toString());
        } else if (SystemUtils.IS_OS_MAC) {
            properties.addProperty("path.ffmpeg", new File("bin/mac", "ffmpeg").toString());
            properties.addProperty("path.mp4box", new File("bin/mac", "MP4Box").toString());
        } else {
            properties.addProperty("path.ffmpeg", new File("bin/linux", "ffmpeg").toString());
            properties.addProperty("path.mp4box", new File("bin/linux", "MP4Box").toString());
        }
        properties.addProperty("path.output", new File("output").toString());
        properties.addProperty("path.temp", new File("temp").toString());
        properties.addProperty("ffmpeg.threadnum", 1);
        properties.addProperty("convertopt.useid3", false);
        properties.addProperty("convertopt.use_title_as_album", false);
        properties.save();
    }
}
