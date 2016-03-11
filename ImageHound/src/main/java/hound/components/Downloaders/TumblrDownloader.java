package hound.components.Downloaders;

import hound.components.AbstractDownloader;
import hound.components.ViewableMutablePair;
import hound.ui.Controller;
import org.apache.commons.lang3.tuple.MutablePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Joshua on 06/02/2016.
 */
public class TumblrDownloader extends AbstractDownloader {

    public TumblrDownloader(Controller controller) {
        super("Tumblr", controller);
    }

    @Override
    public ArrayList<MutablePair<String, String>> getSmallImages(String searchURL, int pageToStart, int minWidth, int minHeight, String search) {
        search = fixUrl(search);
        return enumerateImages(minHeight, 50, pageToStart, minWidth, search);
    }


    public ArrayList<MutablePair<String, String>> enumerateImages(int minHeight, int limit, int pageToStart, int minWidth, String search) {
        search = fixUrl(search);
        ArrayList<MutablePair<String, String>> properties = new ArrayList<>();
        try {
            int numImages = getRequestDetails(search);
            for(int i = pageToStart * 50; i < numImages; i += 50) {
                String page = webRequest("http://" + search + "/api/read?type=photo&num=50&start=" + i);
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(page.getBytes("UTF-8")));
                getImageDetails(doc.getElementsByTagName("post"), minWidth, minHeight).forEach(e -> {
                    if (properties.size() <= limit || limit == -1) {
                        properties.add(e);
                    }
                });
                getImageDetails(doc.getElementsByTagName("photo"), minWidth, minHeight).forEach(e -> {
                    if (properties.size() <= limit || limit == -1) {
                        properties.add(e);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties;
    }

    private ArrayList<MutablePair<String, String>> getImageDetails(NodeList list, int minWidth, int minHeight) {
        ArrayList<MutablePair<String, String>> properties = new ArrayList<>();
        for (int j = 0; j < list.getLength(); j ++) {
            Node curPost = list.item(j);
            if (Integer.valueOf(curPost.getAttributes().getNamedItem("width").getNodeValue()) >= minWidth && Integer.valueOf(curPost.getAttributes().getNamedItem("height").getNodeValue()) >= minHeight) {
                NodeList imageChildren = curPost.getChildNodes();
                String smallImage = "", largeImage = "";
                for (int k = 0; k < imageChildren.getLength(); k++) {
                    Node l = imageChildren.item(k);
                    if (l.hasAttributes() && l.getAttributes().getNamedItem("max-width").getTextContent() != null) {
                        if (Integer.valueOf(l.getAttributes().getNamedItem("max-width").getTextContent()) == 1280) {
                            largeImage = l.getTextContent();
                        } else if (Integer.valueOf(l.getAttributes().getNamedItem("max-width").getTextContent()) == 75) {
                            smallImage = l.getTextContent();
                        }
                    }
                }
                if (!smallImage.equals("") && !largeImage.equals("")) {
                    properties.add(new ViewableMutablePair<>(smallImage, largeImage));
                }
            }

        }
        return properties;
    }



    @Override
    public boolean saveLargeImages(String searchURL, String filePath, int minWidth, int minHeight, String downloadMode, int limit, String search) {
        ArrayList<String> urls = new ArrayList<>();
        enumerateImages(minHeight, limit, 0, minWidth, search).forEach(e -> urls.add(e.getRight()));
        saveUrlArray(filePath, urls);
        return true;
    }


    private Integer getRequestDetails(String searchURL) throws Exception {
        String results = webRequest("http://" + searchURL + "/api/read?type=photo&num=50&start=0");
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(results.getBytes("UTF-8")));
        return Integer.valueOf(doc.getElementsByTagName("posts").item(0).getAttributes().getNamedItem("total").getNodeValue());
    }

    @Override
    public List getDownloadModes() {
        return Collections.singletonList(new ViewableMutablePair<>("Blog URL", ""));
    }

    private String fixUrl(String url) {
        url = url.replace("http://", "");
        if (!url.startsWith("www.")) {
            return "www." + url;
        } else {
            return url;
        }
    }

}
