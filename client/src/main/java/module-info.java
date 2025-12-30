module com.andreibel.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires ocsf;
    requires message;
    requires jdk.compiler;
    requires java.desktop;

    // FXML controllers:
    opens com.andreibel.client to javafx.fxml;


    // if other modules need these types too:
    exports com.andreibel.client;
    exports com.andreibel.client.Main;
    exports com.andreibel.client.Order;
    exports com.andreibel.client.Table;
    exports com.andreibel.client.Subscriber;
    exports com.andreibel.client.Worker;
    opens com.andreibel.client.Main to javafx.fxml;
    opens com.andreibel.client.Order to javafx.fxml;
    opens com.andreibel.client.Table to javafx.fxml;
    opens com.andreibel.client.Subscriber to javafx.fxml;
    opens com.andreibel.client.Worker to javafx.fxml;
}