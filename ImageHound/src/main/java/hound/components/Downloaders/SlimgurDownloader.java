package hound.components.Downloaders;

import hound.components.AbstractDownloader;
import hound.components.ViewableMutablePair;
import hound.ui.Controller;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Joshua on 24/09/2016.
 */
public class SlimgurDownloader  extends AbstractDownloader{
    public SlimgurDownloader(Controller controller) {
        super("Slimgur", controller);
    }

    @Override
    public ArrayList<MutablePair<String, String>> getSmallImages(String searchURL, int pageToStart, int minWidth, int minHeight, String search) {
        return enumerateImages(true, minHeight, -1, -1, minWidth, search);
    }

    @Override
    public boolean saveLargeImages(String searchURL, String filePath, int minWidth, int minHeight, String downloadMode, int limit, String search) {
        ArrayList<String> urls = new ArrayList<>();
        enumerateImages(true, minHeight, -1, -1, minWidth, search).forEach(e -> urls.add(e.getRight()));
        saveUrlArray(filePath, urls);
        return true;
    }



    public ArrayList<MutablePair<String, String>> enumerateImages(boolean small, int minHeight, int limit, int pageToStart, int minWidth, String search) {
        ArrayList<MutablePair<String, String>> properties = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(webRequest(search));
            doc.getElementsByTag("img").stream().filter(e -> e.attributes().get("src").contains("i.sli.mg")).forEach(e -> {
                if (properties.size() <= limit || limit == -1) {
                    properties.add(new MutablePair<>(e.attributes().get("src"), e.attributes().get("src")));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties;
    }

    @Override
    public List getDownloadModes() {
        return Collections.singletonList(new ViewableMutablePair<>("Album URL", ""));
    }

}
