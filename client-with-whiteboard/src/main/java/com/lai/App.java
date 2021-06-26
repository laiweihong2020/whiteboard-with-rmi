package com.lai;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxml = new FXMLLoader();

        fxml.setLocation(getClass().getResource("/com/lai/primary.fxml"));
        fxml.setController(new PrimaryController(stage));
        VBox root = fxml.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Whiteboard");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}