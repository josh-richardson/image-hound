package Hound.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

/**
 * Created by joshuarichardson on 09/02/2016.
 */
public class ImageDetailsTableItem {
    private final SimpleStringProperty url;
    private final Image image;

    public ImageDetailsTableItem(String url, Image image) {
        this.image = image;
        this.url = new SimpleStringProperty(url);

    }

    public String getUrl() {
        return url.get();
    }

    public void setURL(String fName) {
        url.set(fName);
    }

    public Image getImage() {
        return image;
    }
}
