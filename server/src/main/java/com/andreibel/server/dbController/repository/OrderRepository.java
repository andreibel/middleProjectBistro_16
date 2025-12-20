package com.andreibel.server.dbController.repository;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Order;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.andreibel.server.utils.Mapper.mapRelToOrder;

public class OrderRepository {

    private static OrderRepository instance;
    private final TransactionManager tx;

    private OrderRepository() {
        this.tx = TransactionManager.getInstance();
    }

    public static OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    public List<Order> findAll() throws SQLException {
        String sql = "SELECT * FROM bistro.`order`;";
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapRelToOrder(rs));
            }
        }
        return orders;
    }

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

    public List<Order> findOrdersCollideByDateTime(LocalDateTime date) throws SQLException {
        Timestamp start = Timestamp.valueOf(date);
        Timestamp end = Timestamp.valueOf(date.plusHours(2));

        String sql =
                " SELECT * FROM bistro.`order` o WHERE o.{0} = 0 AND o.{1} = 0 AND o{1} < ? AND DATE_ADD(o.{1}, " +
                        "INTERVAL 2 HOUR) > ? ORDER BY o.{1} ASC;";
        sql = String.format(sql, Order.ORDER_CANCELLED, Order.ORDER_COMPLETED, Order.ORDER_DATE_TIME, Order.ORDER_DATE_TIME, Order.ORDER_DATE_TIME);

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
}