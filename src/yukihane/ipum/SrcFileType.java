/** $Id$ */
package yukihane.ipum;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import static yukihane.ipum.DstFileType.*;

public enum SrcFileType {

    FLV {

        @Override
        public DstFileType getDstFileType() {
            return MP3;
        }
    }, MP4 {

        @Override
        public DstFileType getDstFileType() {
            return AAC;
        }
    }, SWF {

        @Override
        public DstFileType getDstFileType() {
            return MP3;
        }
    };

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

    public abstract DstFileType getDstFileType();
}
