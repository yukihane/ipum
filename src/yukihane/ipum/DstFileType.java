package yukihane.ipum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DstFileType {

    MP3 {

        @Override
        public String getExtension() {
            return "mp3";
        }

        @Override
        public boolean isTaggable() {
            return true;
        }
    }, AAC {

        @Override
        public String getExtension() {
            return "aac";
        }

        @Override
        public boolean isTaggable() {
            return true;
        }
    }, WAV {

        @Override
        public String getExtension() {
            return "wav";
        }

        @Override
        public boolean isTaggable() {
            return false;
        }
    };
    private static Logger log = LoggerFactory.getLogger(DstFileType.class);

    public abstract String getExtension();

    public abstract boolean isTaggable();

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
        } else if ("pcm_s16le".equals(ext)) {
            return WAV;
        }
        log.error("unknown type: " + str);
        return null;
    }
}
