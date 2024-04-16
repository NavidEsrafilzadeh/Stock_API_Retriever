package prosjekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProsjektObjektApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String path = "/ProsjektApp.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(path));
        primaryStage.setTitle("Min JavaFX App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}