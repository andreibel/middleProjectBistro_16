package com.andreibel.server.dbController;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages JDBC transactions using a {@link ThreadLocal} connection.
 * <p>
 * This class provides a lightweight transaction boundary mechanism without
 * relying on external frameworks (e.g. Spring).
 * Each transaction is bound to the current thread and automatically committed
 * or rolled back.
 * </p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * TransactionManager tx = TransactionManager.getInstance();
 *
 * User user = tx.inTransaction(() -> {
 *     repository.insertUser(user);
 *     return repository.findUserById(id);
 * });
 * }</pre>
 *
 * <h2>Behavior</h2>
 * <ul>
 *   <li>Automatically opens a JDBC {@link Connection}</li>
 *   <li>Disables auto-commit during the transaction</li>
 *   <li>Commits on success</li>
 *   <li>Rolls back on {@link SQLException}</li>
 *   <li>Supports nested transactions by reusing the same connection</li>
 * </ul>
 *
 * <p>
 * The connection is removed from the {@link ThreadLocal} after completion,
 * ensuring no leaks between requests.
 * </p>
 * @author andrei beloziorove
 */
public class TransactionManager {

    private static TransactionManager instance;
    private final JDBCConnector connector;

    /**
     * Holds the current transaction connection for the executing thread.
     */
    private final ThreadLocal<Connection> txConn = new ThreadLocal<>();

    private TransactionManager(JDBCConnector connector) {
        this.connector = connector;
    }

    /**
     * Returns the singleton instance of {@code TransactionManager}.
     *
     * @return the global TransactionManager instance
     */
    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager(JDBCConnector.getInstance());
        }
        return instance;
    }

    /**
     * Returns the current active {@link Connection} bound to the thread.
     *
     * @return the current transaction connection
     * @throws SQLException if no transaction is active
     */
    public Connection currentConnection() throws SQLException {
        Connection con = txConn.get();
        if (con == null) {
            throw new SQLException("No active transaction. Call via TransactionManager.inTransaction().");
        }
        return con;
    }

    /**
     * Functional interface representing a unit of SQL work to be executed
     * within a transaction.
     *
     * @param <T> return type of the transactional operation
     */
    @FunctionalInterface
    public interface SqlWork<T> {

        /**
         * Executes SQL logic inside a transaction.
         *
         * @return result of the operation
         * @throws SQLException if a database error occurs
         */
        T run() throws SQLException;
    }

    /**
     * Executes the given SQL work inside a transaction boundary.
     *
     * <p>
     * If a transaction is already active on the current thread, the existing
     * connection is reused (nested transaction behavior).
     * </p>
     *
     * @param work the SQL work to execute
     * @param <T> return type
     * @return result produced by the SQL work
     *
     * @throws RuntimeException if the transaction fails
     */
    public <T> T inTransaction(SqlWork<T> work) {
        try {
            // Nested transaction: reuse current connection
            if (txConn.get() != null) {
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