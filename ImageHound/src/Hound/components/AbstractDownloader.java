package Hound.components;

import Hound.ui.Controller;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by Joshua on 06/02/2016.
 */
public abstract class AbstractDownloader {
    private String name;
    private Controller controller;
    public int maxProgress = 0;
    public AtomicLong currentProgress = new AtomicLong(0);
    protected String log = "";
    private String newLine = System.getProperty("line.separator");

    public abstract ArrayList<MutablePair<String, String>> getSmallImages(String searchURL, int pageToStart, int minWidth, int minHeight, String search);

    public abstract boolean saveLargeImages(String searchURL, String filePath, int minWidth, int minHeight, String downloadMode, int limit, String search);

    public abstract List getDownloadModes();



    public AbstractDownloader(String name, Controller controller) {
        this.name = name;
        this.controller = controller;
    }


    public int getMaxProgress() {
        return maxProgress;
    }
    public AtomicLong getCurrentProgress() {
        return currentProgress;
    }

    public String getLog() {
        return log;
    }


    @Override
    public String toString() {
        return name;
    }


    protected static String webRequest(String uri) throws IOException {
        URL url = new URL(uri);
        URLConnection spoof = url.openConnection();
        spoof.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36");
        BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
        String strline = "";
        String finalhtml = "";
        while((strline = in.readLine() ) != null){
            finalhtml = finalhtml + strline + System.getProperty("line.separator");
        }
        return finalhtml;
    }

    public boolean saveUrlArray(String filePath, ArrayList<String> urls) {
        maxProgress = urls.size();
        File directory = new File(filePath);
        if ((!directory.exists() && directory.mkdirs()) || directory.exists()) {
            urls.parallelStream().forEach(relevantURL -> {
                try {
                    File file = new File(filePath + System.getProperty("file.separator") + relevantURL.substring(relevantURL.lastIndexOf("/") + 1, relevantURL.length()));
                    if (!file.exists() && file.createNewFile()) {
                        FileUtils.copyURLToFile(new URL(relevantURL), file);
                        System.out.println("Downloaded: " + relevantURL);
                    } else {
                        System.out.println("Skipped: " + relevantURL);
                    }
                    currentProgress.getAndAdd(1);
                    updateLog("Downloaded " + currentProgress + " of " + maxProgress + " images.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            return false;
        }
        onDownloadFinished();
        return true;
    }

    private void onDownloadFinished() {
        Platform.runLater(controller::onDownloadFinished);
        log = "";
        currentProgress.getAndSet(0);
    }

    public void updateLog(String currentLine) {
        log += currentLine + newLine;
        Platform.runLater(() -> controller.updateLog(log));

    }



}
