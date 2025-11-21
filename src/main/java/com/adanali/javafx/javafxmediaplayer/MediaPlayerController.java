package com.adanali.javafx.javafxmediaplayer;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MediaPlayerController implements Initializable {

    private Application app;
    private Stage stage;
    @FXML
    private BorderPane mainPane;
    @FXML
    private Text fileNameText;
    @FXML
    private StackPane myStackPane;
    @FXML
    private MediaView mediaView;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar videoProgressBar;
    @FXML
    private ComboBox<String> playbackSpeedComboBox;
    @FXML
    private HBox controlBar;
    @FXML
    private Button toggleButton;

    private File mediaFile;

    private FileChooser fileChooser;

    private boolean isMediaFileSelected;

    private Media media;

    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // making sure that video resizes with the window
        mediaView.fitWidthProperty().bind(myStackPane.widthProperty());
        mediaView.fitHeightProperty().bind(myStackPane.heightProperty());

        // making sure that progress bar resizes with the window
        videoProgressBar.prefWidthProperty().bind(controlBar.widthProperty().subtract(20));

        // Setting up the file chooser
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select the Media File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.flv", "*.m4v", "*.fxm")
        );
    }

    public void setApp(Application app) {
        this.app = app;
    }

    public void openNewFile(){
        mediaFile = fileChooser.showOpenDialog(new Stage());
        if (mediaFile != null) {
            isMediaFileSelected = true;
            updateTitleToFileName();
            setupMediaPlayer();
        }
    }

    public void updateTitleToFileName(){
        if (isMediaFileSelected){
            fileNameText.setText(mediaFile.getName());
        }
    }

    public void setupMediaPlayer(){
        if (isMediaFileSelected){
            media = new Media(mediaFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaView.setMediaPlayer(mediaPlayer);
        }
    }

    public void closePlayer(){

        Stage stage = (Stage) mainPane.getScene().getWindow();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit the best Media Player...");
        alert.setContentText("Are you sure?");

        if (alert.showAndWait().get() == ButtonType.OK){
            stage.close();
        }
    }

    public void about(){
        app.getHostServices().showDocument("https://github.com/M-AdanAli/JavaFx-Media-Player/blob/main/README.md");
    }

    public void toggleMedia(){
        if (toggleButton.getText().equals("⏸")){
            mediaPlayer.pause();
            toggleButton.setText("▶");
        } else if (toggleButton.getText().equals("▶")) {
            mediaPlayer.play();
            toggleButton.setText("⏸");
        }
    }
}
