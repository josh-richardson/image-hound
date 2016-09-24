package hound.components.Downloaders;

import hound.components.AbstractDownloader;
import hound.components.ViewableMutablePair;
import hound.ui.Controller;
import org.apache.commons.lang3.tuple.MutablePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Joshua on 06/02/2016.
 */
public class FlickrDownloader extends AbstractDownloader {


    public FlickrDownloader(Controller controller) {
        super("Flickr", controller);
    }

    @Override
    public ArrayList<MutablePair<String, String>> getSmallImages(String searchURL, int pageToStart, int minWidth, int minHeight, String search) {
        return getImagesProperty(searchURL, "url_s", minHeight, 100, pageToStart, minWidth, search);
    }

    @Override
    public boolean saveLargeImages(String searchURL, String filePath, int minWidth, int minHeight, String downloadMode, int limit, String search) {
        ArrayList<String> urls = new ArrayList<>();
        getImagesProperty(searchURL, "url_o", minHeight, limit, 1, minWidth, search).forEach(e -> urls.add(e.getRight()));
        saveUrlArray(filePath, urls);
        return true;
    }


    public ArrayList<MutablePair<String, String>> getImagesProperty(String searchURL, String property, int minHeight, int limit, int pageToStart, int minWidth, String search) {
        ArrayList<MutablePair<String, String>> properties = new ArrayList<>();

        try {
            String apiKey = getApiKey();
            MutablePair<Integer, Integer> requestDetails = getRequestDetails(searchURL, search, apiKey, minWidth, minHeight);
            for (int j = pageToStart; j < requestDetails.getLeft(); j++) {
                updateLog("Indexing search page " + j + " of " + requestDetails.getLeft());
                try {
                    String results = webRequest(searchURL.replace("{SEARCH}", search.replace(" ", "%20")).replace("{APIKEY}", apiKey).replace("{MINWIDTH}", String.valueOf(minWidth)).replace("{MINHEIGHT}", String.valueOf(minHeight)).replace("{PAGE}", String.valueOf(j)));
                    while (results.contains("Invalid API Key") && results.contains("\"stat\":\"fail\"")) {
                        System.out.println("API Key Failed");
                        apiKey = getApiKey();
                        results = webRequest(searchURL.replace("{SEARCH}", search.replace(" ", "%20")).replace("{APIKEY}", apiKey).replace("{MINWIDTH}", String.valueOf(minWidth)).replace("{MINHEIGHT}", String.valueOf(minHeight)).replace("{PAGE}", String.valueOf(j)));
                    }

                    JSONObject parent = new JSONObject(results);
                    JSONObject photos;
                    if (parent.has("photos")) {
                        photos = parent.getJSONObject("photos");
                    } else {
                        photos = parent.getJSONObject("photoset");
                    }
                    JSONArray photosArray = photos.getJSONArray("photo");
                    for (int i = 0; i < photosArray.length(); i++) {
                        JSONObject photo = photosArray.getJSONObject(i);
                        if ((properties.size() < limit && limit != -1 || limit == -1) && photo.has(property) && photo.has("url_o")) {
                            properties.add(new MutablePair<>(photo.getString(property), photo.getString("url_o")));
                        } else if (photo.has(property) && !photo.has("url_o")) {
                            String id = photo.getString("id");
                        } else if (properties.size() >= limit && limit != -1) {
                            return properties;
                        }

                    }
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }



    private MutablePair<Integer, Integer> getRequestDetails(String searchURL, String search, String apiKey, int minWidth, int minHeight) throws IOException {
        updateLog("Getting query details");
        System.out.println(searchURL.replace("{SEARCH}", search.replace(" ", "%20")).replace("{APIKEY}", apiKey).replace("{MINWIDTH}", String.valueOf(minWidth)).replace("{MINHEIGHT}", String.valueOf(minHeight)).replace("{PAGE}", "1"));
        String results = webRequest(searchURL.replace("{SEARCH}", search.replace(" ", "%20")).replace("{APIKEY}", apiKey).replace("{MINWIDTH}", String.valueOf(minWidth)).replace("{MINHEIGHT}", String.valueOf(minHeight)).replace("{PAGE}", "1"));
        JSONObject parent = new JSONObject(results);
        JSONObject photos;
        System.out.println(parent.toString());
        if (parent.has("photos")) {
            photos = parent.getJSONObject("photos");
        } else {
            photos = parent.getJSONObject("photoset");
        }


        return new MutablePair<>(photos.getInt("pages"), photos.getInt("total"));
    }

    private String getApiKey() throws IOException {
        updateLog("Scraping a new API key from Flickr");
        String apiKey = webRequest("https://www.flickr.com/search/?text=hello");
        int index = apiKey.indexOf("site_key = ") + 12;
        apiKey = apiKey.substring(index, index + 32);
        return apiKey;
    }



    @Override
    public List getDownloadModes() {
        //// TODO: Un-bodge the download method to support getting info for individual photos, as photosets don't support url_o
        return Arrays.asList(new ViewableMutablePair<>("Search", "https://api.flickr.com/services/rest?sort=relevance&parse_tags=1&content_type=7&extras=url_o%2Curl_s&per_page=100&page={PAGE}&lang=en-US&text={SEARCH}&dimension_search_mode=min&height={MINHEIGHT}&width={MINWIDTH}&method=flickr.photos.search&api_key={APIKEY}&format=json&hermesClient=1&nojsoncallback=1"),
                new ViewableMutablePair<>("Album", "https://api.flickr.com/services/rest?extras=url_o%2Curl_s&per_page=50&page={PAGE}&get_user_info=1&primary_photo_extras=url_c%2C%20url_h%2C%20url_k%2C%20url_l%2C%20url_m%2C%20url_n%2C%20url_o%2C%20url_q%2C%20url_s&photoset_id={SEARCH}&method=flickr.photosets.getPhotos&api_key={APIKEY}&format=json&hermesClient=1&nojsoncallback=1"),
                new ViewableMutablePair<>("User", "https://api.flickr.com/services/rest?per_page=100&page={PAGE}&view_as=use_pref&sort=use_pref&&extras=url_o%2Curl_s&user_id={SEARCH}&method=flickr.people.getPhotos&api_key={APIKEY}&format=json&hermesClient=1&nojsoncallback=1&dimension_search_mode=min&height={MINHEIGHT}&width={MINWIDTH}"));
    }
}
