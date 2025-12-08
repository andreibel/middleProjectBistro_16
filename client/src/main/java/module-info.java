module com.andreibel.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires ocsf;
    requires message;

    // FXML controllers:
    opens com.andreibel.client to javafx.fxml;


    // if other modules need these types too:
    exports com.andreibel.client;

}