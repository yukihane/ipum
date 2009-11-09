/** $Id$ */
package yukihane.ipum.nicovideo;

import nicobrowser.entity.NicoContent;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class NicoVideoInfoManagerTest {

    @Test
    public void testFindNicoContent() {
        System.out.println("findNicoContent");
        String fileName = "【ＭＭＤ】たこルカで、カチカチボールを作ってみた。【物理演算】.mp4";
        NicoVideoInfoManager instance = NicoVideoInfoManager.getInstance();
        NicoVideoInfo result = instance.findNicoContent(fileName);
//        assertEquals(FilenameUtils.getBaseName(fileName), result.getFileName());
        System.out.println(result.toString());
        fail();
        System.out.println(result.toString());
    }
}
