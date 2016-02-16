package Hound.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

/**
 * Created by joshuarichardson on 09/02/2016.
 */
public class ImageDetailsTableItem {
    private final SimpleStringProperty url;
    private final Image image;

    public ImageDetailsTableItem(String fName, Image image) {
        this.image = image;
        this.url = new SimpleStringProperty(fName);
    }

    public String getUrl() {
        System.out.println(url.get());
        return url.get();
    }

    public void setURL(String fName) {
        url.set(fName);
    }

    public Image getImage() {
        return image;
    }
}
