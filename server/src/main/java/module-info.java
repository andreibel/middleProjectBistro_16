module com.andreibel.server {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.andreibel.server to javafx.fxml;
    exports com.andreibel.server;
}