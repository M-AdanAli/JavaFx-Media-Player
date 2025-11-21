module com.adanali.javafx.javafxmediaplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    exports com.adanali.javafx.javafxmediaplayer;
    opens com.adanali.javafx.javafxmediaplayer to javafx.graphics, javafx.fxml;
}