/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yukihane.ipum;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yuki
 */
public class ConverterTest {

    @BeforeClass
    public static void setUpBeforeClass() {
        new File("output").mkdir();
        new File("temp").mkdir();
    }

    /**
     * 存在しないファイル指定.
     */
    @Test
    public void testRunWithNotExistFile() throws InterruptedException, ExecutionException {
        Config config = Config.getInstance();
        File file = new File("not_exist_file.mp4");
        Converter conv = new Converter(config, file);
        FutureTask<File> task = new FutureTask<File>(conv);

        task.run();
        File res = task.get();
        assertNull(res);
    }

    /**
     * 存在するファイル指定.
     */
    @Test
    public void testRunWithExistFile() throws InterruptedException, ExecutionException {
        Config config = Config.getInstance();
        File file = new File("testdata", "testdata.mp4");
        Converter conv = new Converter(config, file);
        FutureTask<File> task = new FutureTask<File>(conv);

        task.run();
        File res = task.get();
        assertEquals(new File("output/testdata.aac"), res);
    }

    /**
     * 圧縮SWFファイル指定.
     */
    @Test
    public void testRunWithCwfFile() throws InterruptedException, ExecutionException {
        Config config = Config.getInstance();
        File file = new File("testdata", "testdata_cwf.swf");
        Converter conv = new Converter(config, file);
        FutureTask<File> task = new FutureTask<File>(conv);

        task.run();
        File res = task.get();
        assertEquals(new File("output/testdata_cwf.mp3"), res);
    }
}
