package com.andreibel.server.dbController;

import com.andreibel.server.BistroServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnector {

    private static JDBCConnector instance;

    private final String DB_SETTINGS =
            "?allowLoadLocalInfile=true&serverTimezone=Asia/Jerusalem&useSSL=false&allowPublicKeyRetrieval=true";

    private JDBCConnector() {}

    public static JDBCConnector getInstance() {
        if (instance == null) instance = new JDBCConnector();
        return instance;
    }

    public Connection openConnection() throws SQLException {
        String url = BistroServer.DB_URL + DB_SETTINGS;
        return DriverManager.getConnection(url, BistroServer.DB_USER, BistroServer.DB_PASSWORD);
    }
}