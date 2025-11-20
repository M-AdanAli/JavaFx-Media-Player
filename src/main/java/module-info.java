module com.adanali.javafx.javafxmediaplayer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.adanali.javafx.javafxmediaplayer to javafx.fxml;
    exports com.adanali.javafx.javafxmediaplayer;
}