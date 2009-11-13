/** $Id$ */
package yukihane.ipum.nicovideo;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import nicobrowser.Config;
import nicobrowser.entity.NicoContent;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FilenameUtils;

public final class NicoVideoInfoManager {

    private static final String MOVIE_THUMBNAIL_PAGE_HEADER = "http://www.nicovideo.jp/api/getthumbinfo/";
    private static final NicoVideoInfoManager instance = new NicoVideoInfoManager();
    private final EntityManagerFactory factory;

    private NicoVideoInfoManager() {

        Config config = Config.getInstance();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("toplink.jdbc.url", "jdbc:h2:" + config.getDbFile());
        factory = Persistence.createEntityManagerFactory("NicoBrowserPU", map);
    }

    public static NicoVideoInfoManager getInstance() {
        return instance;
    }

    /**
     * ファイル名からDBを検索する.
     * @param fileName ファイル名.
     * @return コンテンツ情報. 一致するものが無ければnull.
     */
    public NicoVideoInfo findNicoContent(String fileName) {
        EntityManager manager = factory.createEntityManager();

        String baseName = FilenameUtils.getBaseName(fileName);
        Query query = manager.createQuery("SELECT cont FROM NicoContent AS cont " + "WHERE ?1 = cont.fileName").
                setParameter(1, baseName);
        List<NicoContent> results = query.getResultList();
        if (results.size() > 1) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "同一ファイル名のファイルが複数ありました");
        }

        if (results.isEmpty()) {
            return null;
        }
        final NicoContent content = results.get(results.size() - 1);

        URL url = null;
        XMLConfiguration config = null;
        try {
            url = new URL(MOVIE_THUMBNAIL_PAGE_HEADER + content.getNicoId());
            config = new XMLConfiguration(url);
            return new NicoVideoInfo(
                    config.getString("thumb.video_id"),
                    config.getString("thumb.title"),
                    config.getString("thumb.description"),
                    config.getString("thumb.thumbnail_url"),
                    config.getString("thumb.first_retrieve"),
                    config.getString("thumb.length"),
                    config.getString("thumb.view_counter"),
                    config.getString("thumb.comment_num"),
                    config.getString("thumb.mylist_counter"),
                    config.getString("thumb.last_res_body"),
                    config.getString("thumb.watch_url"),
                    config.getString("thumb.thumb_type"),
                    config.getStringArray("thumb.tags.tag"),
                    content.getAuthor());
        } catch (Exception ex) {
            Logger.getLogger(NicoVideoInfoManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
