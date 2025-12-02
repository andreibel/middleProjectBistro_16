package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.JDBCConnector;
import com.andreibel.server.entity.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.andreibel.server.utils.Mapper.mapRelToOrder;

public class OrderRepository {

    private static OrderRepository instance;
    private final JDBCConnector connector;
    private OrderRepository() {
        connector = JDBCConnector.getInstance();
    }

    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public List<Order> getAllOrders() {
        // Implementation to retrieve all orders from the database
        Statement stmt;
        List<Order> orders = new ArrayList<>();
        try {
            stmt = connector.getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT t.* FROM bistro.`order` t;");
            while(rs.next()) {
                //System.out.println(rs.getString(1) + " " + rs.getString(2));
                orders.add(mapRelToOrder(rs));
            }
            rs.close();
            return orders;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        }
    }

    public Order editOrder(Order order) {
        Connection conn = connector.getConn();
        String updateSql = """
        UPDATE bistro.`order`\s
        SET number_of_guests = ?,\s
            conformation_code = ?,\s
            subscriber_id = ?,\s
            order_date = ?,\s
            date_of_placing_order = ?
        WHERE order_number = ?;
       \s""";

        String selectSql = """
        SELECT order_number,
               order_date,
               number_of_guests,
               conformation_code,
               subscriber_id,
               date_of_placing_order
        FROM bistro.`order`
        WHERE order_number = ?;
        """;

        Order updatedOrder = null;

        try {
            // Start transaction
            conn.setAutoCommit(false);

            // UPDATE
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, order.getNumberOfGuests());
                stmt.setInt(2, order.getConformationCode());
                stmt.setInt(3, order.getSubscriberId());
                stmt.setTimestamp(4, Timestamp.valueOf(order.getOrderDateTime()));
                stmt.setTimestamp(5, Timestamp.valueOf(order.getPlacedOrderDateTime()));
                stmt.setInt(6, order.getOrderNumber());

                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("No order found with order_number = " + order.getOrderNumber());
                }
            }

            // SELECT updated row
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setInt(1, order.getOrderNumber());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        updatedOrder = mapRelToOrder(rs); // helper method below
                    }
                }
            }

            // Commit transaction
            conn.commit();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            try {
                conn.rollback();
            } catch (SQLException rollEx) {
                System.out.println("Rollback failed: " + rollEx.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Failed to reset autoCommit: " + e.getMessage());
            }
        }

        return updatedOrder;
    }
}
