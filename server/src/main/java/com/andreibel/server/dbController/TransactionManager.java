package com.andreibel.server.dbController;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    private static TransactionManager instance;
    private final JDBCConnector connector;

    private final ThreadLocal<Connection> txConn = new ThreadLocal<>();

    private TransactionManager(JDBCConnector connector) {
        this.connector = connector;
    }

    public static TransactionManager getInstance() {
        if (instance == null) instance = new TransactionManager(JDBCConnector.getInstance());
        return instance;
    }

    public Connection currentConnection() throws SQLException {
        Connection con = txConn.get();
        if (con == null) {
            throw new SQLException("No active transaction. Call via TransactionManager.inTransaction().");
        }
        return con;
    }

    @FunctionalInterface
    public interface SqlWork<T> {
        T run() throws SQLException;
    }

    public <T> T inTransaction(SqlWork<T> work) {
        try {
            if (txConn.get() != null) {
                // nested call - reuse same connection
                return work.run();
            }

            Connection con = connector.openConnection();
            boolean oldAutoCommit = con.getAutoCommit();
            try {
                con.setAutoCommit(false);
                txConn.set(con);

                T result = work.run();
                con.commit();
                return result;

            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                }
                throw new RuntimeException("Failed to execute transaction", e);
            } finally {
                txConn.remove();
                try {
                    con.setAutoCommit(oldAutoCommit);
                } catch (SQLException ignored) {
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to manage transaction", e);
        }
    }
}