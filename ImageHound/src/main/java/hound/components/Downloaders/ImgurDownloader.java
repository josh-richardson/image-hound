package hound.components.Downloaders;

import hound.components.AbstractDownloader;
import hound.components.ViewableMutablePair;
import hound.ui.Controller;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by joshuarichardson on 06/05/2016.
 */
public class ImgurDownloader extends AbstractDownloader {
    public ImgurDownloader(Controller controller) {
        super("Imgur", controller);
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
            Document doc = Jsoup.connect(search).get();
            doc.getElementsByTag("a").stream().filter(e -> e.attributes().hasKey("class") && e.attributes().hasKey("href") && e.attributes().get("class").equals("zoom") && e.attributes().get("href").startsWith("//i.imgur.com/")).forEach(e -> {
                String url = "http:" + e.attributes().get("href");
                String smallUrl = (FilenameUtils.removeExtension(url) + "g." + FilenameUtils.getExtension(url));
                if (properties.size() <= limit || limit == -1) {
                    properties.add(new MutablePair<>((small ? smallUrl : url), url));
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
