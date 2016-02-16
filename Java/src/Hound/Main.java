package Hound;

import Hound.ui.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hound/ui/main_form.fxml"));
        fxmlLoader.setController(new Controller());
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Image Hound");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
