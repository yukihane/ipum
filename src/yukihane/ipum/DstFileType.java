/** $Id$ */
package yukihane.ipum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DstFileType {

    MP3 {

        @Override
        public String getExtension() {
            return "mp3";
        }
    }, AAC {

        @Override
        public String getExtension() {
            return "aac";
        }
    };

    private static Logger log = LoggerFactory.getLogger(DstFileType.class);
    public abstract String getExtension();

    @Override
    public String toString() {
        return getExtension();
    }

    static DstFileType from(String str) {
        final String ext = str.toLowerCase();
        if ("aac".equals(ext)) {
            return AAC;
        } else if ("mp3".equals(ext)) {
            return MP3;
        }
        log.error("unknown type: " + str);
        return null;
    }
}
