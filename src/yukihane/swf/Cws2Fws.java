/** $Id$ */
package yukihane.swf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

public class Cws2Fws {

    private static final String CWS = "CWS";
    private static final String FWS = "FWS";

    /**
     * 圧縮SWFかどうか判定する.
     * @param file 判定対象.
     * @return 圧縮SWFであればtrue.
     */
    public static boolean isCws(File file) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte header[] = new byte[CWS.length()];
            bis.read(header, 0, header.length);
            if (CWS.equals(new String(header))) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    /**
     * 圧縮SWFを展開する.
     * @param in 展開対象.
     * @return 展開後のファイル. 対象が圧縮SWFでなければnull.
     */
    public static File createFws(File in, File out) {
        if (!isCws(in)) {
            return null;
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            byte buffer[] = new byte[1024];
            bis = new BufferedInputStream(new FileInputStream(in));
            bis.read(buffer, 0, CWS.length()); // CWS
            bis.read(buffer, 0, 5); // その他ヘッダ

            bos = new BufferedOutputStream(new FileOutputStream(out));
            bos.write(FWS.getBytes());
            bos.write(buffer, 0, 5);

            InflaterInputStream iis = new InflaterInputStream(bis);
            while (true) {
                int res = iis.read(buffer);
                if (res < 0) {
                    break;
                }
                bos.write(buffer, 0, res);
            }
            return out;
        } catch (IOException ex) {
            Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ex) {
                    Logger.getLogger(Cws2Fws.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
}
