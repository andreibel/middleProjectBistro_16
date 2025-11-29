module com.andreibel.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.andreibel.client to javafx.fxml;
    exports com.andreibel.client;
}