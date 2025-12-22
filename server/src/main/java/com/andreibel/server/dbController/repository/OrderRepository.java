package com.andreibel.server.dbController.repository;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.andreibel.server.utils.OrderMapper.mapRelToOrder;

/**
 * <h1>Order repository class.</h1>
 * <hr/>
 * this class is used to manage the orders in the database.
 * in this class has all the methods to interact with the database.
 * the methods are to create new orders, update orders, find orders, etc.
 * this class is a singleton class.<br/>
 * use TransactionManager.getInstance() to get the instance of this class.
 *
 * @author Andrei Beloziyorove
 * @see TransactionManager
 */
public class OrderRepository {

    private static OrderRepository instance;
    private final TransactionManager tx;

    /**
     * private constructor.<br/>
     * use TransactionManager.getInstance() to get the instance of this class.
     */
    private OrderRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * get the instance of this class.<br/>
     * use this method to get the instance of this class.
     *
     * @return the instance of this class.
     */
    public static OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    /**
     * get all orders from the database.
     *
     * @return a list of orders. {@code List<Order>}
     * @throws SQLException if an error occurs while getting orders from the database.
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
     * find order by order number.
     * use to get order by order number if save new order.
     * or update order if the order is already in the database.
     *
     * @param orderNumber order number.
     * @return order entity. {@code Order}
     * @throws SQLException if an error occurs while getting order from the database.
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
     * update order in the database.
     * use this method to update order in the database.
     *
     * @param order order entity to be updated. {@code Order}
     * @throws SQLException if an error occurs while updating order in the database.
     */
    public void update(OrderRequest order) throws SQLException {
        String sql = "UPDATE bistro.`order` SET {0} = ?, {1} = ? WHERE {2} = ?; ";
        sql = String.format(sql, Order.NUMBER_OF_GUESTS, Order.ORDER_DATE_TIME, Order.ORDER_NUMBER);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, order.getNumberOfGuests());
            stmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDateTime()));
            stmt.setInt(3, order.getOrderNumber());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Order not found: " + order.getOrderNumber());
            }
        }
    }

    /**
     * find orders that collide with the requested date time.
     * use this method to find orders that collide with the requested date time.
     * to check if we can place a new order at the requested time.
     *
     * @param date requested date time.
     * @return a list of orders that collide with the requested date time. {@code List<Order>}
     * @throws SQLException if an error occurs while getting orders from the database.
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
     * find orders by subscriber id.
     * use this method to find orders by subscriber id.
     * @param subscriberId subscriber id.
     * @return a list of orders by subscriber id. {@code List<Order>}
     * @throws SQLException if an error occurs while getting orders from the database.
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
     * save a new order in the database.
     * use this method to save new order in the database.
     * return a new instance of the order entity that was saved.
     * @param newOrder order entity to be saved. {@code Order}
     * @return a new instance of the order entity that was saved. {@code Order}
     * @throws SQLException if an error occurs while saving order in the database.
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
}

