/** $Id$ */
package yukihane.ipum.nicovideo;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import nicobrowser.Config;
import nicobrowser.NicoHttpClient;
import nicobrowser.entity.NicoContent;
import org.apache.commons.io.FilenameUtils;

public final class NicoVideoInfoManager {

    private static final NicoVideoInfoManager instance = new NicoVideoInfoManager();
    private final NicoHttpClient client;
    private final EntityManagerFactory factory;

    private NicoVideoInfoManager() {
        client = NicoHttpClient.getInstance();

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
    public NicoContent findNicoContent(String fileName) {
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
        return results.get(results.size() - 1);


    }
}
