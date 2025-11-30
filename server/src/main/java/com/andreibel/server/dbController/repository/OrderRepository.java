package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.JDBCConnector;
import com.andreibel.server.entity.Order;
import com.andreibel.server.utils.mapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
                orders.add(mapper.mapRelToOrder(rs));
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

    public void editOrder(Order order) {
        PreparedStatement stmt;
        try {
            stmt = connector.getConn().prepareStatement("update bistro.`order` SET " +
                    "number_of_guests = ?, conformation_code = ?, subscriber_id = ?, order_date = ?, " +
                    "date_of_placing_order = ? WHERE order_number = ?;");
            stmt.setInt(1, order.getNumberOfGuests());
            stmt.setInt(2, order.getConformationCode());
            stmt.setInt(3, order.getSubscriberId());
            stmt.setObject(4, order.getOrderDateTime());
            stmt.setObject(5, order.getPlacedOrderDateTime());
            stmt.setInt(6, order.getOrderNumber());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
