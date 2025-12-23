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

public class SubscriberRepository {

    private final TransactionManager tx;
    private static SubscriberRepository instance;

    private SubscriberRepository() {
        this.tx = TransactionManager.getInstance();
    }

    public static SubscriberRepository getInstance() {
        if (instance == null) instance = new SubscriberRepository();
        return instance;
    }

    public Subscriber addSubscriber(SubscriberRequest sub) throws SQLException {
        String sql = "INSERT INTO bistro.subscriber ({0}, {1}, {2}) VALUES (?,?,?);";
        sql = String.format(sql, Subscriber.EMAIL, Subscriber.NAME, Subscriber.PHONE_NUMBER);
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, sub.getEmail());
            stmt.setString(2, sub.getName());
            stmt.setString(3, sub.getPhoneNumber());
            stmt.executeUpdate();
        }
        return getSubscriberByEmail(sub.getEmail());
    }

    public Subscriber getSubscriberByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM bistro.subscriber WHERE {0} = ?;";
        sql = String.format(sql, Subscriber.EMAIL);
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToSubscriber(rs) : null;
            }
        }
    }

    public Subscriber getSubscriberByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM bistro.subscriber WHERE {0} = ?;";
        sql = String.format(sql, Subscriber.PHONE_NUMBER);
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToSubscriber(rs) : null;
            }
        }
    }
    public Subscriber getSubscriberById(int subscriberId) throws SQLException {
        String sql = "SELECT * FROM bistro.subscriber WHERE {0} = ?;";
        sql = String.format(sql, Subscriber.SUBSCRIBER_ID);
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, subscriberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToSubscriber(rs) : null;
            }
        }
    }


    public List<Subscriber> findAll() throws SQLException {
        String sql = "SELECT * FROM bistro.`subscriber`;";
        List<Subscriber> orders = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapRelToSubscriber(rs));
            }
        }
        return orders;
    }
}