/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yukihane.ipum;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yuki
 */
public class ConverterTest {

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
}
