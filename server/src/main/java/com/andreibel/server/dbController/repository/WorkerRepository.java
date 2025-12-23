package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Worker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.andreibel.server.utils.WorkerMapper.mapRelToWorker;

/**
 * Repository responsible for persistence operations on {@link Worker} entities.
 *
 * <p>
 * This repository uses plain JDBC and relies on {@link TransactionManager}
 * to obtain the current transactional {@link java.sql.Connection}.
 * All methods must be invoked within an active transaction
 * (via {@link TransactionManager#inTransaction}).
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Insert new workers into the database</li>
 *   <li>Retrieve workers by unique attributes (worker name)</li>
 * </ul>
 *
 * <p>
 * This class follows the Singleton pattern to provide a single repository
 * instance throughout the application lifecycle.
 * </p>
 *
 * @author Aviv peer
 */
public class WorkerRepository {

    /**
     * Transaction manager used to access the current JDBC connection.
     */
    private final TransactionManager tx;

    private static WorkerRepository instance;

    private WorkerRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * Returns the singleton instance of {@code WorkerRepository}.
     *
     * @return the global WorkerRepository instance
     */
    public static WorkerRepository getInstance() {
        if (instance == null) {
            instance = new WorkerRepository();
        }
        return instance;
    }

    /**
     * Inserts a new {@link Worker} record into the database.
     *
     * <p>
     * The worker password is expected to be already hashed (e.g. HMAC / bcrypt)
     * before calling this method.
     * </p>
     *
     * @param workerRequest the worker entity to insert
     * @return the persisted worker fetched from the database
     * @throws SQLException if a database error occurs
     * @see #findByWorkerName(String)
     */
    public Worker addWorker(Worker workerRequest) throws SQLException {
        String sql =
                "INSERT INTO bistro.worker (" + Worker.WORKER_NAME + "," + Worker.WORKER_PASSWORD + ", " +
                        Worker.WORKER_EMAIL + ", " + Worker.IS_MANAGER + ") VALUES (?,?,?,?)";
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, workerRequest.getWorkerName());
            stmt.setString(2, workerRequest.getWorkerPassword());
            stmt.setString(3, workerRequest.getWorkerEmail());
            stmt.setBoolean(4, workerRequest.isManager());
            stmt.executeUpdate();
        }

        return findByWorkerName(workerRequest.getWorkerName());
    }

    /**
     * Retrieves a {@link Worker} by its unique worker name.
     *
     * @param workerName the worker name to search for
     * @return the matching worker, or {@code null} if none exists
     * @throws SQLException if a database error occurs
     */
    public Worker findByWorkerName(String workerName) throws SQLException {
        String sql = "SELECT * " +
                "FROM bistro.worker " +
                "WHERE " + Worker.WORKER_NAME + "= ?";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, workerName);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToWorker(rs) : null;
            }
        }
    }
}