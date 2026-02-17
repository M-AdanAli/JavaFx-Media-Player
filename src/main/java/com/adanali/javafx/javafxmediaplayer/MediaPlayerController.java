package com.adanali.javafx.javafxmediaplayer;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MediaPlayerController implements Initializable {

    private Application app;
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
    private ComboBox<String> playbackSpeedComboBox;
    @FXML
    private Slider mediaSlider;
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
        mediaView.fitWidthProperty().bind(myStackPane.widthProperty().subtract(10));
        mediaView.fitHeightProperty().bind(myStackPane.heightProperty().subtract(20));

        // Setting up the file chooser
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select the Media File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.flv", "*.m4v", "*.fxm")
        );
        playbackSpeedComboBox.getItems().addAll("0.5x", "1x", "1.5x", "2x");
    }

    public void setApp(Application app) {
        this.app = app;
    }

    public void openNewFile(){
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        mediaFile = fileChooser.showOpenDialog(currentStage);
        if (mediaFile != null) {
            isMediaFileSelected = true;
            updateTitleToFileName();
            setupMediaPlayer();
            setupVolumeSlider();
            setupMediaSeekSlider();
            wireUpSpeedComboBox();
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

            // Added some Error Handling
            media.setOnError(() -> {
                showErrorAlert("Could not load media",
                        "This video uses a format or codec that JavaFX cannot play.\n\n" +
                                "Try re-exporting it as H.264 video with AAC audio in an MP4 container.");
            });

            mediaPlayer.setOnError(() -> {
                showErrorAlert("Playback error",
                        "There was a problem playing this file.\n\n" +
                                "Details:\n" + mediaPlayer.getError().getMessage());
            });

        }
    }

    public void setupVolumeSlider(){
        if (isMediaFileSelected){
            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                mediaPlayer.setVolume(newVal.doubleValue() / 100);
            });
        }
    }

    public void setupMediaSeekSlider(){
        if (isMediaFileSelected) {
            // Setting the Media Slider Range on Media load
            mediaPlayer.setOnReady(() -> {
                mediaSlider.setMin(0);
                mediaSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            });

            // Syncing the Media Slider with the Media Play
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!mediaSlider.isValueChanging()) // Making sure the user is not seeking the Media Slider
                    mediaSlider.setValue(newTime.toSeconds());
            });

            // Seeking on User Drag
            mediaSlider.valueChangingProperty().addListener((obs, wasChanging, changing) -> {
                if (!changing) { // User finished dragging
                    mediaPlayer.seek(Duration.seconds(mediaSlider.getValue()));
                }
            });

            mediaSlider.setOnMousePressed(event -> {
                double mouseX = event.getX();
                double width = mediaSlider.getWidth();
                double max = mediaSlider.getMax();
                double min = mediaSlider.getMin();

                // Calculate click position as a value from min to max
                double value = min + (mouseX / width) * (max - min);
                mediaSlider.setValue(value);
                mediaPlayer.seek(Duration.seconds(value));
            });
        }
    }

    public void wireUpSpeedComboBox(){
        if (isMediaFileSelected) {
            playbackSpeedComboBox.setValue("1x"); // Set default

            playbackSpeedComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                double rate = Double.parseDouble(newVal.replace("x", ""));
                mediaPlayer.setRate(rate);
            });
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

    public void reverseMedia(){
        if (isMediaFileSelected) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration back = currentTime.subtract(Duration.seconds(5));

            // Clamp to min duration
            if (back.lessThan(Duration.ZERO)) {
                back = Duration.ZERO;
            }
            mediaPlayer.seek(back);
        }
    }

    public void toggleMedia(){
        if (isMediaFileSelected){
            if (toggleButton.getText().equals("⏸")){
                mediaPlayer.pause();
                toggleButton.setText("▶");
            } else if (toggleButton.getText().equals("▶")) {
                mediaPlayer.play();
                toggleButton.setText("⏸");
            }
        }
    }

    public void fastForwardMedia(){
        if (isMediaFileSelected){
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration totalDuration = mediaPlayer.getTotalDuration();
            Duration forward = currentTime.add(Duration.seconds(5));

            // Clamping to max duration
            if (forward.greaterThan(totalDuration)) {
                forward = totalDuration;
            }
            mediaPlayer.seek(forward);
        }
    }
    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
