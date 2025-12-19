
// module-info.java
module com.andreibel.client {

    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires message;
    requires ocsf;

    //requires com.andreibel.client;

    // FXML controllers:
    opens com.andreibel.server to javafx.fxml;

    // *** important line: allow JavaFX reflection on DTOs ***

    // if other modules need these types too:
    exports com.andreibel.server;
}