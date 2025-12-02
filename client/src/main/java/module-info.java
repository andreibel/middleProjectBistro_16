module com.andreibel.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens com.andreibel.client to javafx.fxml;
    exports com.andreibel.client;
}