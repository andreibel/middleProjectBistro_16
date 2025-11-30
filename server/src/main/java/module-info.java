module com.andreibel.server {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.andreibel.server to javafx.fxml;
    exports com.andreibel.server;
    exports com.andreibel.server.controller;
    opens com.andreibel.server.controller to javafx.fxml;
}