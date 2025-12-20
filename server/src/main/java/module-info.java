
// module-info.java
module com.andreibel.client {

    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires message;
    requires ocsf;
    //requires com.andreibel.client;

    //requires com.andreibel.client;

    // FXML controllers:
    opens com.andreibel.server to javafx.fxml;

    // *** important line: allow JavaFX reflection on DTOs ***


}