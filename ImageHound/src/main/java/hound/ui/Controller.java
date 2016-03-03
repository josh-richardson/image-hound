package hound.ui;

import hound.components.AbstractDownloader;
import hound.components.Downloaders.FlickrDownloader;
import hound.components.Downloaders.FourChanDownloader;
import hound.components.Downloaders.TumblrDownloader;
import hound.components.ImageDetailsTableItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.swing.filechooser.FileSystemView;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class Controller implements Initializable {
    @FXML
    public Accordion acdnMain;
    @FXML
    public TitledPane paneHost;
    @FXML
    public ComboBox cbxHosts;
    @FXML
    public Button btnHelp;
    @FXML
    public TitledPane paneOptions;
    @FXML
    public ComboBox cbxDownloadMethod;
    @FXML
    public Label lblMethod;
    @FXML
    public TextField txtSearch;
    @FXML
    public TextField txtMinResolution;
    @FXML
    public TextField txtDirectory;
    @FXML
    public TitledPane paneSelectItems;
    @FXML
    public RadioButton rbAllImages;
    @FXML
    public TextField txtMaxImages;
    @FXML
    public RadioButton rbCertainImages;
    @FXML
    public TableView tableImages;
    @FXML
    public TableColumn columnImage1;
    @FXML
    public TableColumn columnImage2;
    @FXML
    public Label lblSelectedImages;
    @FXML
    public Button btnLoadMoreImages;
    @FXML
    public TitledPane paneDownloading;
    @FXML
    public TextArea txtLog;

    final ObservableList<ImageDetailsTableItem> data = FXCollections.observableArrayList();
    int currentPage = 0;
    AbstractDownloader currentDownloader = null;
    MutablePair<String, String> downloadMethod =  null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();

        rbAllImages.selectedProperty().addListener((observable, oldValue, newValue) -> {
            txtMaxImages.setDisable(!newValue);
            btnLoadMoreImages.setDisable(newValue);
            tableImages.setDisable(newValue);
            if (newValue) {
                data.clear();
            }
            updateUI(acdnMain.getExpandedPane());
        });

        cbxHosts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                paneOptions.setDisable(false);
                currentDownloader = (AbstractDownloader) newValue;
            }
            updateUI(acdnMain.getExpandedPane());
        });

        acdnMain.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == paneOptions) {
                cbxDownloadMethod.getItems().setAll(currentDownloader.getDownloadModes());
                cbxDownloadMethod.getSelectionModel().select(0);
            } else if (newValue == paneDownloading) {
                if (rbCertainImages.isSelected()) {
                    ArrayList<String> relevantURLs = (ArrayList<String>) tableImages.getSelectionModel().getSelectedCells().stream().map(e -> data.get(((TablePosition) e).getRow()).getUrl()).collect(Collectors.toList());
                    new Thread(() -> {
                        currentDownloader.saveUrlArray(txtDirectory.getText(), relevantURLs);
                    }).start();
                } else {
                    new Thread(() -> {
                        currentDownloader.saveLargeImages(downloadMethod.getRight(), txtDirectory.getText(), Integer.valueOf(txtMinResolution.getText().split(", ")[0].replace(", ", "")), Integer.valueOf(txtMinResolution.getText().split(", ")[1].replace(", ", "")), cbxDownloadMethod.getSelectionModel().getSelectedItem().toString(), txtMaxImages.getText().equals("None") ? -1 : Integer.valueOf(txtMaxImages.getText()), txtSearch.getText());
                    }).start();
                }
            } else if (newValue == paneHost) {
                cbxHosts.getSelectionModel().clearSelection();
                paneOptions.setDisable(true);
            }
            updateUI(newValue);
        });


        rbCertainImages.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateUI(acdnMain.getExpandedPane());
            if (newValue && tableImages.getItems().size() == 0) {
                new Thread(() -> {
                    data.add(new ImageDetailsTableItem("Loading...", null));
                    currentDownloader.getSmallImages(downloadMethod.getRight(), currentPage, Integer.valueOf(txtMinResolution.getText().split(", ")[0].replace(", ", "")), Integer.valueOf(txtMinResolution.getText().split(", ")[1].replace(", ", "")), txtSearch.getText()).forEach(e -> data.add(new ImageDetailsTableItem(e.getRight(), new Image(e.getLeft(), true))));
                    data.remove(0);
                }).start();
            }
        });

        btnLoadMoreImages.setOnAction(event -> {
            //TODO: Smoother implementation for this
            showMessage("The view is updating. Please do not click the button again.");
            new Thread(() -> {
                currentPage += 1;
                currentDownloader.getSmallImages(downloadMethod.getRight(), currentPage, Integer.valueOf(txtMinResolution.getText().split(", ")[0].replace(", ", "")), Integer.valueOf(txtMinResolution.getText().split(", ")[1].replace(", ", "")), txtSearch.getText()).forEach(e -> data.add(new ImageDetailsTableItem(e.getLeft(), new Image(e.getRight(), true))));
            }).start();
        });


        columnImage1.setCellFactory(param -> new TableCell<Object, Image>() {
            VBox vb;
            ImageView iv;

            {
                vb = new VBox();
                vb.setAlignment(Pos.CENTER);
                iv = new ImageView();
                iv.setFitHeight(90);
                iv.setFitWidth(90);
                vb.getChildren().add(iv);
                setGraphic(vb);
            }

            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);

                } else {
                    iv.setImage(item);
                    setGraphic(vb);
                }
            }
        });

        tableImages.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selected = tableImages.getSelectionModel().getSelectedCells().size();
            lblSelectedImages.setText(String.valueOf(selected) + " image" + (selected == 0 || selected >= 2 ? "s" : "") + " selected");
            updateUI(acdnMain.getExpandedPane());
        });

        cbxDownloadMethod.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue != null) {
               downloadMethod = (MutablePair)newValue;
               lblMethod.setText(downloadMethod.getLeft() + ":");
           }
        });


        ((VBox) paneOptions.getContent()).getChildren().forEach(e -> {
            if (e instanceof TextField) {
                ((TextField) e).textProperty().addListener((observable, oldValue, newValue) -> {
                    updateUI(acdnMain.getExpandedPane());
                });
            }
        });

        txtMaxImages.textProperty().addListener((observable, oldValue, newValue) -> {
            updateUI(acdnMain.getExpandedPane());
        });
        txtLog.textProperty().addListener((observable, oldValue, newValue) -> {
            updateUI(acdnMain.getExpandedPane());
        });

    }

    private void setupUI() {

        paneHost.setExpanded(true);
        acdnMain.setExpandedPane(paneHost);

        cbxHosts.getItems().addAll(Arrays.asList(new FlickrDownloader(this), new TumblrDownloader(this), new FourChanDownloader(this)));  //, new FourChanDownloader(this), new TumblrDownloader(this)));
        txtDirectory.setText(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());

        final ToggleGroup downloadModes = new ToggleGroup();
        rbCertainImages.setToggleGroup(downloadModes);
        rbAllImages.setToggleGroup(downloadModes);

        columnImage1.prefWidthProperty().bind(tableImages.widthProperty().divide(4));
        columnImage2.prefWidthProperty().bind(tableImages.widthProperty().divide(4).multiply(3));

        columnImage1.setCellValueFactory(new PropertyValueFactory("image"));
        columnImage2.setCellValueFactory(new PropertyValueFactory("url"));
        tableImages.setItems(data);

        tableImages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    private void updateUI(TitledPane selectedPane) {
        if (selectedPane == null) return;
        if (selectedPane == paneDownloading) {
            acdnMain.getPanes().stream().filter(e -> e != paneDownloading).forEach(e -> e.setDisable(true));
        } else {
            paneSelectItems.setDisable(!(isNumeric(txtMinResolution.getText().replace(", ", "")) && txtMinResolution.getText().contains(", ") && !txtSearch.getText().equals("") && !txtDirectory.getText().equals("") &&
                    (selectedPane.equals(paneOptions) || selectedPane.equals(paneSelectItems))));
            paneDownloading.setDisable(
                    !(selectedPane.equals(paneDownloading) ||
                            (selectedPane.equals(paneSelectItems) && ((rbAllImages.isSelected() && (isNumeric(txtMaxImages.getText()) ||
                                    txtMaxImages.getText().equals("None"))) || (rbCertainImages.isSelected() && tableImages.getSelectionModel().getSelectedCells().size() != 0)))));
        }
    }

    public void updateLog(String log) {
        txtLog.setText(log);
        txtLog.setScrollTop(Double.MAX_VALUE);
    }

    public void showMessage(String content) {
        showMessage("Information", null, content);
    }

    public void showMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onDownloadFinished() {
        //TODO: Find a nicer way to do this.
        acdnMain.getPanes().stream().forEach(e -> e.setDisable(true));
        paneHost.setDisable(false);
        showMessage("Finished downloading!");
        acdnMain.setExpandedPane(paneHost);
        rbAllImages.setSelected(true);
        txtMaxImages.setText("None");
        lblSelectedImages.setText("0 images selected");
        txtSearch.setText("");
        txtLog.setText("");
        txtMinResolution.setText("0, 0");
        txtDirectory.setText(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());
        data.clear();
        currentPage = 0;

    }


    private boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

}
