/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yukihane.ipum;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yuki
 */
public class ConfigTest {

    private static File config;
    private static File configOrig;
    private boolean isMove;

    @Before
    public void setUp() {
        final String APPLICATION_NAME = "ipum";
        final String CONFIG_NAME = APPLICATION_NAME + ".xml";
        final File appDir = new File(System.getProperty("user.home", "."), "." + APPLICATION_NAME).getAbsoluteFile();
        config = new File(appDir, CONFIG_NAME);
        configOrig = new File(appDir, CONFIG_NAME + ".orig");

        if (config.exists()) {
            isMove = config.renameTo(configOrig);
            if (!isMove) {
                System.err.println("コンフィグファイルの削除(退避)に失敗. テスト結果が不正確な可能性があります. ");
                System.err.println("次のファイルを確認のうえ削除してみてください. : " + configOrig.getAbsolutePath());
            }
        }
    }

    @After
    public void tearDown() {
        if (isMove) {
            if (config.exists()) {
                config.delete();
            }
            boolean res = configOrig.renameTo(config);
            if (!res) {
                System.err.println("退避したコンフィグファイルの置換に失敗. ");
            }
        }
        isMove = false;
    }

    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        Config result = Config.getInstance();
        assertNotNull(result);
    }

    @Test
    public void testGetFfmpegPath() {
        Config conf = Config.getInstance();
        File res = conf.getFfmpegPath();
        assertNotNull(res);
        assertEquals(new File("bin/ffmpeg.exe"), res);
    }
}
