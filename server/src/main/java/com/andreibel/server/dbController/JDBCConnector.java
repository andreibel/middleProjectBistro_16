package com.andreibel.server.dbController;

import com.andreibel.server.BistroServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <h1>JDBC connector class.</h1>
 * <hr/>
 * this class is used to connect to the database.
 * singleton class.
 * @author Andrei Beloziyorove
 */
public class JDBCConnector {

    // singleton instance
    private static JDBCConnector instance;

    // database settings
    private final String DB_SETTINGS =
            "?allowLoadLocalInfile=true&serverTimezone=Asia/Jerusalem&useSSL=false&allowPublicKeyRetrieval=true";

    private JDBCConnector() {}

    /**
     * get the instance of this class.<br/>
     * use this method to get the instance of this class.
     * @return the instance of this class.
     */
    public static JDBCConnector getInstance() {
        if (instance == null) instance = new JDBCConnector();
        return instance;
    }

    /**
     * open a connection to the database.
     * use this method to open a connection to the database, per transaction.
     * @return a connection to the database.
     * @throws SQLException if an error occurs while opening a connection to the database.
     */
    public Connection openConnection() throws SQLException {
        String url = BistroServer.DB_URL + DB_SETTINGS;
        return DriverManager.getConnection(url, BistroServer.DB_USER, BistroServer.DB_PASSWORD);
    }
}
