
// module-info.java
module com.andreibel.client {

    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;

    // FXML controllers:
    opens com.andreibel.server to javafx.fxml;

    // *** important line: allow JavaFX reflection on DTOs ***
    opens message.DTO to javafx.base;

    // if other modules need these types too:
    exports com.andreibel.server;
    exports message;
    exports message.DTO;
}