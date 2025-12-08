package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.JDBCConnector;
import com.andreibel.server.entity.Order;
import com.andreibel.message.DTO.OrderRequest;

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
        PreparedStatement stmt;
        List<Order> orders = new ArrayList<>();
        try {
            stmt = connector.getConn().prepareStatement("SELECT t.* FROM bistro.`order` t WHERE t.conformation_code = ?;");
            ResultSet rs = stmt.executeQuery("SELECT t.* FROM bistro.`order` t;");
            while(rs.next()) {
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


    
    
    public Order editOrder(OrderRequest order) {
        Connection conn = connector.getConn();

        String updateSql = """
        UPDATE bistro.`order`\s
        SET number_of_guests = ?,\s
            order_date = ?\s
        WHERE order_number = ?;
       \s""";
        String selectSql = """
        SELECT *
        FROM bistro.`order`
        WHERE order_number = ?;
        """;

        Order updatedOrder = null;

        try {
            // Start transaction
            connector.StartTransaction();

            // UPDATE
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, order.getNumberOfGuests());
                stmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDateTime()));
                stmt.setInt(3, order.getOrderNumber());

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
            connector.CommitTransaction();
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
