module com.andreibel.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    // FXML controllers:
    opens com.andreibel.client to javafx.fxml;

    // *** important line: allow JavaFX reflection on DTOs ***
    opens message.DTO to javafx.base;

    // if other modules need these types too:
    exports com.andreibel.client;
    exports message;
    exports message.DTO;
}