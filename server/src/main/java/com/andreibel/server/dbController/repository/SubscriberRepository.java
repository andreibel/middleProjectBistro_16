package com.andreibel.server.dbController.repository;

import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Subscriber;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.andreibel.server.utils.SubscriberMapper.mapRelToSubscriber;

/**
 * JDBC repository (DAO) for {@link Subscriber} entities.
 *
 * <p>This class provides low-level CRUD operations for the {@code bistro.subscriber} table using
 * plain JDBC. It relies on {@link TransactionManager} to supply the current transactional
 * {@link java.sql.Connection} via {@code tx.currentConnection()}.</p>
 *
 * <p><b>Transaction requirement:</b> Most methods throw {@link SQLException} and assume they are called
 * inside an active transaction (for example, through {@code tx.inTransaction(...)}). If there is no
 * active transaction, {@code tx.currentConnection()} may fail or use an unexpected connection.</p>
 *
 * <p><b>Singleton:</b> Implemented as a singleton to keep a single repository instance across the server
 * lifecycle.</p>
 *
 * <p><b>Mapping:</b> ResultSet rows are converted to {@link Subscriber} using
 * {@link com.andreibel.server.utils.SubscriberMapper#mapRelToSubscriber(ResultSet)}.</p>
 *
 * @author Andrei Beloziyorove
 */
public class SubscriberRepository {

    private static SubscriberRepository instance;
    private final TransactionManager tx;

    /**
     * Private constructor to enforce singleton usage.
     */
    private SubscriberRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * Returns the singleton instance of {@link SubscriberRepository}.
     *
     * @return singleton repository instance
     */
    public static SubscriberRepository getInstance() {
        if (instance == null) {
            instance = new SubscriberRepository();
        }
        return instance;
    }

    /**
     * Inserts a new subscriber and returns the persisted entity (reloaded from DB).
     *
     * <p><b>Expected fields in request:</b> {@code email}, {@code name}, {@code phoneNumber}.</p>
     *
     * <p><b>SQL:</b>
     * <pre>
     * INSERT INTO bistro.`subscriber` (`email`, `name`, `phoneNumber`)
     * VALUES (?,?,?);
     * </pre>
     * </p>
     *
     * <p>This method executes an INSERT, reads the generated primary key, and then calls
     * {@link #findById(int)} to return the stored row.</p>
     *
     * @param sub subscriber creation request DTO
     * @return stored subscriber (as persisted in DB)
     * @throws SQLException if a JDBC error occurs or if no generated key is returned
     */
    public Subscriber addSubscriber(SubscriberRequest sub) throws SQLException {
        String sql = """
                INSERT INTO bistro.`subscriber`
                (`email`, `name`, `phoneNumber`)
                VALUES (?,?,?);
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, sub.getEmail());
            stmt.setString(2, sub.getName());
            stmt.setString(3, sub.getPhoneNumber());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Insert succeeded but no generated key returned");
                return findById(keys.getInt(1));
            }
        }
    }

    /**
     * Finds a subscriber by its primary key.
     *
     * <p><b>SQL:</b>
     * <pre>
     * SELECT *
     * FROM bistro.`Subscriber`
     * WHERE subscriberId = ?;
     * </pre>
     * </p>
     *
     * @param subscriberId subscriber primary key
     * @return matching subscriber, or {@code null} if not found
     * @throws SQLException if a JDBC error occurs
     */
    public Subscriber findById(int subscriberId) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Subscriber`
                WHERE subscriberId = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, subscriberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToSubscriber(rs) : null;
            }
        }
    }

    /**
     * Retrieves all subscribers from the database.
     *
     * <p><b>SQL:</b>
     * <pre>
     * SELECT *
     * FROM bistro.`subscriber`;
     * </pre>
     * </p>
     *
     * @return list of all subscribers (may be empty)
     * @throws SQLException if a JDBC error occurs
     */
    public List<Subscriber> findAll() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`subscriber`;
                """;
        List<Subscriber> subscribers = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) subscribers.add(mapRelToSubscriber(rs));
        }
        return subscribers;
    }

    /**
     * Updates an existing subscriber by its {@code subscriberId}.
     *
     * <p><b>Expected fields in request:</b> {@code subscriberId}, {@code name}, {@code phoneNumber}, {@code email}.</p>
     *
     * <p><b>SQL:</b>
     * <pre>
     * UPDATE bistro.`Subscriber`
     * SET name = ?, phoneNumber = ?, email = ?
     * WHERE subscriberId = ?;
     * </pre>
     * </p>
     *
     * <p><b>Error handling:</b> This method currently catches {@link SQLException} and prints the stack trace
     * instead of propagating the error. In most repository designs it is preferable to declare
     * {@code throws SQLException} so the service layer can decide how to handle failures.</p>
     *
     * @param data DTO containing subscriber id and updated fields
     */
    public void updateBySubID(SubscriberRequest data) {
        String sql = """
                UPDATE bistro.`Subscriber`
                SET  name = ?, phoneNumber = ?, email = ?
                WHERE subscriberId = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, data.getName());
            stmt.setString(2, data.getPhoneNumber());
            stmt.setString(3, data.getEmail());
            stmt.setInt(4, data.getSubscriberId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}