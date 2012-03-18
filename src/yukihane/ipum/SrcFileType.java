/** $Id$ */
package yukihane.ipum;

import java.io.File;
import org.apache.commons.io.FilenameUtils;

public enum SrcFileType {

    FLV, MP4, SWF;

    static SrcFileType fromFileExt(File file) {
        String ext = FilenameUtils.getExtension(file.toString()).toLowerCase();
        if ("flv".equals(ext)) {
            return FLV;
        } else if ("mp4".equals(ext)) {
            return MP4;
        } else if ("swf".equals(ext)) {
            return SWF;
        }
        return null;
    }
}
