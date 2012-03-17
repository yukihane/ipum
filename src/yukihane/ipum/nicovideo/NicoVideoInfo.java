/** $Id$ */
package yukihane.ipum.nicovideo;

import com.sun.syndication.io.impl.DateParser;
import java.io.*;
import java.net.URL;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NicoVideoInfo {

    private static Logger log = LoggerFactory.getLogger(NicoVideoInfo.class);
    private final String videoId;
    private final String title;
    private final String description;
    private final URL thumbnailUrl;
    private final Date firstRetrieve;
    private final int length;
    private final int viewCounter;
    private final int commentNum;
    private final int mylistCounter;
    private final String lastResBody;
    private final URL watchUrl;
    private final String thumbType;
    private final String[] tags;
    private final String author;

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbType() {
        return thumbType;
    }

    public Date getFirstRetrieve() {
        return firstRetrieve;
    }

    public int getLength() {
        return length;
    }

    public int getViewCounter() {
        return viewCounter;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public int getMylistCounter() {
        return mylistCounter;
    }

    public String getLastResBody() {
        return lastResBody;
    }

    public URL getWatchUrl() {
        return watchUrl;
    }

    public String[] getTags() {
        return tags;
    }

    public URL getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getAuthor() {
        return author;
    }

    NicoVideoInfo(
            String videoId,
            String title,
            String description,
            String thumbnailUrl,
            String firstRetrieve,
            String length,
            String viewCounter,
            String commentNum,
            String mylistCounter,
            String lastResBody,
            String watchUrl,
            String thumbType,
            String[] tags,
            String author) {
        try {
            this.videoId = videoId;
            this.title = title;
            this.description = description;
            this.thumbnailUrl = new URL(thumbnailUrl);
            this.firstRetrieve = DateParser.parseW3CDateTime(firstRetrieve);
            String[] times = length.split(":");
            int calc = 0;
            for (String time : times) {
                calc *= 60;
                calc += Integer.parseInt(time);
            }
            this.length = calc;
            this.viewCounter = Integer.parseInt(viewCounter);
            this.commentNum = Integer.parseInt(commentNum);
            this.mylistCounter = Integer.parseInt(mylistCounter);
            this.lastResBody = lastResBody;
            this.watchUrl = new URL(watchUrl);
            this.thumbType = thumbType;
            this.tags = tags;
            this.author = author;
        } catch (Exception e) {
            throw new IllegalArgumentException("ビデオ情報取得に失敗", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public File getArtWork(File tmpDir) {
        InputStream is = null;
        OutputStream os = null;
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("tmp", ".jpg", tmpDir);
            tmpFile.deleteOnExit();
            os = new FileOutputStream(tmpFile);
            is = thumbnailUrl.openStream();

            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
        } catch (IOException ex) {
            log.error("", ex);
            tmpFile = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                }
            }
        }
        return tmpFile;
    }
}
