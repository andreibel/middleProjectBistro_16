module com.andreibel.server {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Java SE modules
    requires java.sql;

    // Lombok (compile-time only)
    requires static lombok;

    // Automatic modules (your custom dependencies)
    requires message;
    requires ocsf;

    // MySQL JDBC driver (automatic module)
    requires mysql.connector.j;

    // Open packages to JavaFX for FXML reflection (controllers with @FXML)
    opens com.andreibel.server.main to javafx.fxml, javafx.graphics;
    opens com.andreibel.server.controller to javafx.fxml, javafx.base;

    // Export packages that need to be accessible
    exports com.andreibel.server.main;
    exports com.andreibel.server.controller;
    exports com.andreibel.server.entity;
    exports com.andreibel.server.services;
    exports com.andreibel.server.utils;
    exports com.andreibel.server.dbController;
    exports com.andreibel.server.dbController.repository;
}