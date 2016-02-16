package Hound.components.Downloaders;

import Hound.components.AbstractDownloader;
import Hound.ui.Controller;
import org.apache.commons.lang3.tuple.MutablePair;

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
        return null;
    }

    @Override
    public boolean saveLargeImages(String searchURL, String filePath, int minWidth, int minHeight, String downloadMode, int limit, String search) {
        return false;
    }


    @Override
    public List getDownloadModes() {
        return Collections.singletonList("Blog name");
    }

}
