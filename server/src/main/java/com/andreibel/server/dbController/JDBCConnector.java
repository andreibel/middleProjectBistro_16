package com.andreibel.server.dbController;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connector class.
 * this class is responsible for connecting to the database.
 * and give this connection to the other repositories.
 * also class is singleton.
 * <hr/>
 * provide option for start and commit transaction. and rollback.
 * */
@Getter
public class JDBCConnector {

    private static JDBCConnector instance;
    private Connection conn;

    // TODO: change credentials to env variables
    /**
     * Constructor: creates a new JDBC connector.
     * */
    private JDBCConnector() {
        String url = "jdbc:mysql://localhost:3306/bistro?allowLoadLocalInfile=true&serverTimezone=Asia/Jerusalem&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "tikraetzeM4!";
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    /**
     * Returns the singleton instance of the JDBC connector ({@code JDBCConnector}).
     * */
    public static JDBCConnector getInstance() {
        if (instance == null) {
            instance = new JDBCConnector();
        }
        return instance;
    }

    /**
     * Starts a transaction.
     * @throws SQLException  if failed to start transaction.
     * */
    public void StartTransaction() throws SQLException {
        conn.setAutoCommit(false);
    }
    /**
     * commit / finish a transaction.
     * @throws SQLException  if failed to commit / finish a transaction.
     * */
    public void CommitTransaction() throws SQLException {
        conn.commit();
    }
    /**
     * roll-back the transaction if something was fail. a transaction.
     * @throws SQLException  if failed to make role-back a transaction.
     * */
    public void RollbackTransaction() throws SQLException {
        conn.rollback();
    }
}
