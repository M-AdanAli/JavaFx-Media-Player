package com.adanali.javafx.javafxmediaplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.stage.Stage;

public class MainApplication extends Application {

    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layout/MediaPlayer.fxml"));
        Parent root = loader.load();

        MediaPlayerController controller = loader.getController();
        controller.setApp(this);

        Scene scene = new Scene(root);
        stage.setTitle("Media Player");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText("You are about to exit the best Media Player...");
            alert.setContentText("Are you sure?");

            if (alert.showAndWait().get() == ButtonType.OK){
                stage.close();
            }
        });
    }
}
