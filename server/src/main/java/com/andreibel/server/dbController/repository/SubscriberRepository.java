package com.andreibel.server.dbController.repository;

import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Subscriber;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.andreibel.server.utils.SubscriberMapper.mapRelToSubscriber;

/**
 * JDBC repository for {@link Subscriber} entities.
 *
 * <p>
 * Uses {@link TransactionManager} to access the current transactional
 * JDBC connection. All methods must be executed inside
 * an active transaction.
 * </p>
 *
 * <p>
 * Implemented as a Singleton.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class SubscriberRepository {

    private static SubscriberRepository instance;
    private final TransactionManager tx;

    private SubscriberRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * @return singleton instance of SubscriberRepository
     */
    public static SubscriberRepository getInstance() {
        if (instance == null) {
            instance = new SubscriberRepository();
        }
        return instance;
    }

    /**
     * Inserts a new subscriber and returns the persisted entity.
     *
     * @param sub subscriber creation request
     * @return stored subscriber
     */
    public Subscriber addSubscriber(SubscriberRequest sub) throws SQLException {
        String sql = """
                INSERT INTO bistro.`subscriber`
                (`email`, `name`, `phoneNumber`)
                VALUES (?,?,?);
                """;


        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, sub.getEmail());
            stmt.setString(2, sub.getName());
            stmt.setString(3, sub.getPhoneNumber());
            stmt.executeUpdate();
        }
        return getSubscriberByEmail(sub.getEmail());
    }

    /**
     * @param email subscriber email
     * @return matching subscriber or {@code null}
     */
    public Subscriber getSubscriberByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM bistro.subscriber WHERE " + Subscriber.EMAIL + " = ?;";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToSubscriber(rs) : null;
            }
        }
    }

    /**
     * @param phone subscriber phone number
     * @return matching subscriber or {@code null}
     */
    public Subscriber getSubscriberByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM bistro.subscriber WHERE " + Subscriber.PHONE_NUMBER + " = ?;";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToSubscriber(rs) : null;
            }
        }
    }

    /**
     * @param subscriberId subscriber primary key
     * @return matching subscriber or {@code null}
     */
    public Subscriber getSubscriberById(int subscriberId) throws SQLException {
        String sql = "SELECT * FROM bistro.subscriber WHERE " + Subscriber.SUBSCRIBER_ID + " = ?;";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, subscriberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToSubscriber(rs) : null;
            }
        }
    }

    /**
     * @return all subscribers in the database
     */
    public List<Subscriber> findAll() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`subscriber`;
                """;
        List<Subscriber> subscribers = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subscribers.add(mapRelToSubscriber(rs));
            }
        }
        return subscribers;
    }


    public void updateBySubID(SubscriberRequest data) {
        String sql = "UPDATE bistro.subscriber " + "SET " + Subscriber.NAME + " = ? , " + Subscriber.PHONE_NUMBER + " = ?, " + Subscriber.EMAIL + " = ?" + "WHERE " + Subscriber.SUBSCRIBER_ID + " = ?;";
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