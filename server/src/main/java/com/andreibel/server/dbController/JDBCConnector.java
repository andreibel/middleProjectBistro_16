package com.andreibel.server.dbController;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <h1>JDBCConnector</h1>
 * <hr/>
 * <p>
 * Singleton helper responsible for providing a JDBC {@link Connection} to the
 * application's MySQL database. The connector opens a single {@code Connection}
 * via {@link DriverManager#getConnection(String, String, String)} and exposes
 * transaction helper methods.
 * </p>
 *
 * <p><strong>Important notes</strong>:</p>
 * <ul>
 *   <li>Credentials are currently hard-coded. This class contains a TODO to
 *       switch to environment variables or a secure secret store. Do not commit
 *       real credentials to source control.</li>
 *   <li>The current singleton implementation is not thread-safe. If the
 *       application may access {@link #getInstance()} concurrently during
 *       initialization, consider adding synchronization or using the
 *       initialization-on-demand holder idiom or an enum singleton.</li>
 * </ul>
 *
 * <p><strong>Transaction usage</strong>:</p>
 * <p>Use {@link #StartTransaction()}, {@link #CommitTransaction()} and
 * {@link #RollbackTransaction()} to manage transactions on the underlying
 * connection. These methods forward to the corresponding {@link Connection}
 * methods and can throw {@link SQLException}.</p>
 * Example usage:
 * <p><blockquote><pre>
 *
 *     // Obtain the singleton connector
 *     JDBCConnector connector = JDBCConnector.getInstance();
 *
 *     // Get the raw JDBC connection if needed
 *     Connection conn = connector.getConn();
 *
 *     try {
 *         // Start transaction
 *         connector.StartTransaction();
 *
 *         // ... perform JDBC operations using conn ...
 *
 *         // Commit when done
 *         connector.CommitTransaction();
 *     } catch (SQLException ex) {
 *         // Rollback on error
 *         connector.RollbackTransaction();
 *     }
 * </pre></blockquote></p>
 *
 * @see java.sql.Connection
 * @see java.sql.DriverManager
 * @since 1.0
 * @author Andrei Beloziyorove
 */
@Getter
public class JDBCConnector {

    /**
     * Singleton instance of {@code JDBCConnector}.
     *
     * <p>Note: lazy-initialized and not thread-safe. For a thread-safe
     * singleton, add synchronization or use the initialization-on-demand holder
     * pattern.</p>
     */
    private static JDBCConnector instance;

    /**
     * The underlying JDBC {@link Connection} opened by this connector.
     *
     * <p>Access via the generated getter {@link #getConn()} (from Lombok).</p>
     */
    private Connection conn;

    // TODO: change credentials to env variables

    /**
     * Private constructor that initializes the JDBC connection.
     *
     * <p>This constructor attempts to open a connection to a MySQL database
     * using {@link DriverManager#getConnection(String, String, String)}. Any
     * {@link SQLException} raised during connection is caught and basic error
     * information is printed to standard output.</p>
     *
     * <p><strong>Security:</strong> Currently uses hard-coded credentials and
     * connection URL. Replace with secure configuration before production use.</p>
     */
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
     * Returns the singleton instance of {@code JDBCConnector}.
     *
     * <p>This method lazily initializes the singleton instance on first call.
     * It is not synchronized; if multiple threads may call this method during
     * startup, consider synchronizing or using a thread-safe singleton pattern.</p>
     *
     * @return the shared {@code JDBCConnector} instance
     */
    public static JDBCConnector getInstance() {
        if (instance == null) {
            instance = new JDBCConnector();
        }
        return instance;
    }

    /**
     * Starts a transaction on the underlying connection by disabling auto-commit.
     *
     * @throws SQLException if setting auto-commit fails or if the connection is invalid
     */
    public void StartTransaction() throws SQLException {
        conn.setAutoCommit(false);
    }

    /**
     * Commits the current transaction on the underlying connection.
     *
     * @throws SQLException if commit fails
     */
    public void CommitTransaction() throws SQLException {
        conn.commit();
    }

    /**
     * Rolls back the current transaction on the underlying connection.
     *
     * @throws SQLException if rollback fails
     */
    @SuppressWarnings("unused")
    public void RollbackTransaction() throws SQLException {
        conn.rollback();
    }
}