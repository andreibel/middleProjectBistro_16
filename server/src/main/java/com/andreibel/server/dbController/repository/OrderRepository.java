package com.andreibel.server.dbController.repository;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.andreibel.server.utils.OrderMapper.mapRelToOrder;

/**
 * Repository responsible for database access related to {@link Order} entities.
 *
 * <p>
 * This repository uses plain JDBC and depends on {@link TransactionManager}
 * to provide the active transactional {@link java.sql.Connection}.
 * All methods must be executed within an active transaction
 * (via {@link TransactionManager#inTransaction}).
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Create new orders</li>
 *   <li>Update existing orders</li>
 *   <li>Retrieve orders by different criteria (id, subscriber id, date collision)</li>
 * </ul>
 *
 * <p>
 * The class follows the Singleton pattern to ensure a single instance
 * is used throughout the application lifecycle.
 * </p>
 * @author Andrei Beloziyorove
 */
public class OrderRepository {

    /**
     * Singleton instance of {@code OrderRepository}.
     */
    private static OrderRepository instance;

    /**
     * Transaction manager used to access the current JDBC connection.
     */
    private final TransactionManager tx;

    private OrderRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * Returns the singleton instance of {@code OrderRepository}.
     *
     * @return the global OrderRepository instance
     */
    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    /**
     * Retrieves all {@link Order} records from the database.
     *
     * @return a list of all orders; an empty list if no records exist
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findAll() throws SQLException {
        String sql = "SELECT * FROM bistro.`order`;";
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapRelToOrder(rs));
            }
        }
        return orders;
    }

    /**
     * Retrieves an {@link Order} by its order number (primary key).
     *
     * @param orderNumber the order number to search for
     * @return the matching order, or {@code null} if none exists
     * @throws SQLException if a database access error occurs
     */
    public Order findById(int orderNumber) throws SQLException {
        String sql = "SELECT * FROM bistro.`order` WHERE {0} = ?;";
        sql = String.format(sql, Order.ORDER_NUMBER);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToOrder(rs) : null;
            }
        }
    }

    /**
     * Retrieves an {@link Order} by its order number (primary key).
     *
     * @param conformationCode the order number to search for
     * @return the matching order, or {@code null} if none exists
     * @throws SQLException if a database access error occurs
     */
    public Order findByConformationCode(UUID conformationCode) throws SQLException {
        String sql = "SELECT * FROM bistro.`order` WHERE {0} = ?;";
        sql = String.format(sql, Order.CONFIRMATION_CODE);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToOrder(rs) : null;
            }
        }
    }

    /**
     * Updates an existing order (currently: number of guests and order date/time).
     *
     * @param order the update request containing the order number and updated fields
     * @throws SQLException if the order does not exist or a database access error occurs
     */
    public void update(OrderRequest order) throws SQLException {
        String sql = "UPDATE bistro.`order` SET {0} = ?, {1} = ? WHERE {2} = ?; ";
        sql = String.format(sql, Order.NUMBER_OF_GUESTS, Order.ORDER_DATE_TIME, Order.CONFIRMATION_CODE);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, order.getNumberOfGuests());
            stmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDateTime()));
            stmt.setString(3, order.getConformationCode().toString());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Order not found: " + order.getConformationCode());
            }
        }
    }

    /**
     * Finds orders that collide (overlap) with the requested reservation time window.
     *
     * <p>
     * This method assumes a fixed reservation duration of 2 hours:
     * requested window = [{@code date}, {@code date + 2 hours}].
     * </p>
     *
     * <p>
     * The query filters out orders that are marked as cancelled or completed.
     * </p>
     *
     * @param date requested reservation start date/time
     * @return a list of orders that overlap the requested window; empty list if none collide
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findOrdersCollideByDateTime(LocalDateTime date) throws SQLException {
        Timestamp start = Timestamp.valueOf(date);
        Timestamp end = Timestamp.valueOf(date.plusHours(2));

        String sql = " SELECT * FROM bistro.`order` o WHERE o.{0} = 0 AND o.{1} = 0 AND o{1} < ? AND DATE_ADD(o.{1}, " +
                "INTERVAL 2 HOUR) > ? ORDER BY o.{1} ASC;";
        sql = String.format(sql, Order.ORDER_CANCELLED, Order.ORDER_COMPLETED, Order.ORDER_DATE_TIME);

        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setTimestamp(1, end);   // existing.start < requested.end
            stmt.setTimestamp(2, start); // existing.end > requested.start

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRelToOrder(rs));
                }
            }
        }
        return orders;
    }

    /**
     * Retrieves all orders for a given subscriber.
     *
     * @param subscriberId the subscriber id to search for
     * @return a list of orders for the subscriber; empty list if none exist
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findBySubscriberId(int subscriberId) throws SQLException {
        String sql = "SELECT * FROM bistro.`order` WHERE {0} = ?;";
        sql = String.format(sql, Order.SUBSCRIBER_ID);

        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, subscriberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRelToOrder(rs));
                }
            }
            return orders;
        }
    }

    /**
     * Persists a new order into the database and returns the stored entity.
     *
     * <p>
     * Nullable fields are supported (e.g. subscriberId / email / phoneNumber).
     * After insert, the generated order number is used to fetch and return
     * the saved order.
     * </p>
     *
     * @param newOrder the order entity to insert
     * @return the persisted order fetched from the database
     * @throws SQLException if insert fails or a database access error occurs
     */
    public Order save(Order newOrder) throws SQLException {
        String sql = "INSERT INTO bistro.`order` ({0}, {1}, {2}, {3},{4},{5}) VALUES (?,?,?,?,?,?); ";
        sql = String.format(sql, Order.NUMBER_OF_GUESTS, Order.CONFIRMATION_CODE, Order.ORDER_DATE_TIME,
                Order.SUBSCRIBER_ID, Order.EMAIL, Order.PHONE_NUMBER);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, newOrder.getNumberOfGuests());
            stmt.setString(2, newOrder.getConformationCode().toString());
            stmt.setTimestamp(3, Timestamp.valueOf(newOrder.getOrderDateTime()));

            // all these params can be nullable
            if (newOrder.getSubscriberId() == null) stmt.setNull(4, Types.INTEGER);
            else stmt.setInt(4, newOrder.getSubscriberId());

            if (newOrder.getEmail() == null) stmt.setNull(5, Types.VARCHAR);
            else stmt.setString(5, newOrder.getEmail());

            if (newOrder.getPhoneNumber() == null) stmt.setNull(6, Types.VARCHAR);
            else stmt.setString(6, newOrder.getPhoneNumber());

            int affected = stmt.executeUpdate();
            if (affected != 1) {
                throw new SQLException("Insert failed, affected rows: " + affected);
            }

            int generatedId;
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Insert succeeded but no generated key returned");
                generatedId = keys.getInt(1);
            }

            return findById(generatedId);
        }
    }

    public int deleteByConformationCode(UUID conformationCode) throws SQLException {
        String sql = "delete FROM bistro.`order` WHERE {0} = ?;";
        sql = String.format(sql, Order.CONFIRMATION_CODE);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            return stmt.executeUpdate();
        }
    }
}