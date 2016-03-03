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
 * Created by Joshua on 06/02/2016.
 */
public class FourChanDownloader extends AbstractDownloader {

    public FourChanDownloader(Controller controller) {
        super("4chan", controller);
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
            doc.getElementsByTag("a").stream().filter(e -> e.attributes().get("href").startsWith("//i.4cdn.org/") && !e.attributes().get("href").endsWith("s") && e.attributes().hasKey("class")).forEach(e -> {
                String url = "http:" + e.attributes().get("href");
                String smallUrl = (FilenameUtils.removeExtension(url) + "s." + FilenameUtils.getExtension(url)).replace(".gif", ".jpg").replace(".webm", ".jpg");
                System.out.println(smallUrl);
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
        return Collections.singletonList(new ViewableMutablePair<>("Thread URL", ""));
    }


}
