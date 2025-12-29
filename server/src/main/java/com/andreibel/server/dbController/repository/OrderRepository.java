package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Order;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.andreibel.server.utils.OrderMapper.mapRelToOrder;

/**
 * JDBC repository for {@link Order} entities.
 *
 * <p>
 * Uses {@link TransactionManager} to access the current transactional
 * {@link java.sql.Connection}. All methods must be executed inside
 * an active transaction.
 * </p>
 *
 * <p>
 * Implemented as a Singleton.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class OrderRepository {

    private static OrderRepository instance;
    private final TransactionManager tx;

    private OrderRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * @return singleton instance of OrderRepository
     */
    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    /**
     * @return all orders in the database
     */
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

    /**
     * @param orderNumber order primary key
     * @return matching order or {@code null}
     */
    public Order findById(int orderNumber) throws SQLException {
        String sql = "SELECT * FROM bistro.`order` WHERE " + Order.ORDER_NUMBER + " = ?;";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToOrder(rs) : null;
            }
        }
    }

    /**
     * @param conformationCode order confirmation code
     * @return matching order or {@code null}
     */
    public Order findByConformationCode(UUID conformationCode) throws SQLException {
        String sql = "SELECT * FROM bistro.`order` WHERE " + Order.CONFIRMATION_CODE + " = ?;";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToOrder(rs) : null;
            }
        }
    }


    /**
     * Finds orders that overlap a 2-hour reservation window.
     *
     * @param date reservation start time
     * @return colliding orders
     */
    public List<Order> findOrdersCollideByDateTime(LocalDateTime date) throws SQLException {
        Timestamp start = Timestamp.valueOf(date);
        Timestamp end = Timestamp.valueOf(date.plusHours(2));

        String sql = "SELECT * FROM bistro.`order` o " + "WHERE o." + Order.ORDER_CANCELLED + " = 0 " + "AND o." + Order.ORDER_COMPLETED + " = 0 " + "AND o." + Order.ORDER_DATE_TIME + " < ? " + "AND DATE_ADD(o." + Order.ORDER_DATE_TIME + ", INTERVAL 2 HOUR) > ? " + "ORDER BY o." + Order.ORDER_DATE_TIME + " ASC;";

        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setTimestamp(1, end);
            stmt.setTimestamp(2, start);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRelToOrder(rs));
                }
            }
        }
        return orders;
    }

    /**
     * @param subscriberId subscriber identifier
     * @return orders belonging to the subscriber
     */
    public List<Order> findBySubscriberId(int subscriberId) throws SQLException {
        String sql = "SELECT * FROM bistro.`order` WHERE " + Order.SUBSCRIBER_ID + " = ?;";

        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, subscriberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRelToOrder(rs));
                }
            }
        }
        return orders;
    }

    /**
     * Inserts a new order and returns the persisted entity.
     *
     * @param newOrder order to persist
     * @return stored order
     */
    public Order save(Order newOrder) throws SQLException {
        String sql = "INSERT INTO bistro.`order` (" + Order.NUMBER_OF_GUESTS + "," + Order.CONFIRMATION_CODE + "," + Order.ORDER_DATE_TIME + "," + Order.SUBSCRIBER_ID + "," + Order.EMAIL + "," + Order.PHONE_NUMBER + ") VALUES (?,?,?,?,?,?);";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, newOrder.getNumberOfGuests());
            stmt.setString(2, newOrder.getConformationCode().toString());
            stmt.setTimestamp(3, Timestamp.valueOf(newOrder.getOrderDateTime()));

            if (newOrder.getSubscriberId() == null) stmt.setNull(4, Types.INTEGER);
            else stmt.setInt(4, newOrder.getSubscriberId());

            if (newOrder.getEmail() == null) stmt.setNull(5, Types.VARCHAR);
            else stmt.setString(5, newOrder.getEmail());

            if (newOrder.getPhoneNumber() == null) stmt.setNull(6, Types.VARCHAR);
            else stmt.setString(6, newOrder.getPhoneNumber());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Insert succeeded but no generated key returned");
                }
                return findById(keys.getInt(1));
            }
        }
    }

    /**
     * Deletes an order by confirmation code.
     *
     * @param conformationCode order confirmation code
     * @return number of deleted rows
     */
    public int deleteByConformationCode(UUID conformationCode) throws SQLException {
        String sql = "UPDATE bistro.`order` " + "SET " + Order.ORDER_CANCELLED + " = 1 " + "WHERE " + Order.CONFIRMATION_CODE + " = ?;";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            return stmt.executeUpdate();
        }
    }

    public int setArrived(UUID conformationCode) throws SQLException {
        String sql =
                "UPDATE bistro.`Order` " + "SET " + Order.ORDER_ARRIVED + " = 1 " + "WHERE " + Order.CONFIRMATION_CODE + " = ?;";

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            return stmt.executeUpdate();
        }
    }

    public void completeOrder(UUID conformationCode) throws SQLException {
        String sql = "UPDATE bistro.`order` " + "SET " + Order.ORDER_COMPLETED + " = 1 " + "WHERE " + Order.CONFIRMATION_CODE + " = ?;";
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            stmt.executeUpdate();
        }

    }


    public List<Order> findAllDateOrders(LocalDate date) throws SQLException {
        String sql = """
        SELECT *
        FROM bistro.`order`
        WHERE orderDateTime >= ?
          AND orderDateTime <  ?
          AND orderCancelled = 0
        """;

        List<Order> orders = new ArrayList<>();

        try (PreparedStatement ps = tx.currentConnection().prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(date.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(date.plusDays(1).atStartOfDay()));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(mapRelToOrder(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return orders;
    }

    public int cancelLateOrders(int graceMinutes) throws SQLException {
        String sql = """
        UPDATE bistro.`order`
        SET orderCancelled = 1
        WHERE orderCancelled = 0
          AND orderCompleted = 0
          AND orderArrive = 0
          AND orderDateTime < ?
        """;

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(graceMinutes);

        try (PreparedStatement ps = tx.currentConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoff));
            ps.executeUpdate();
        }
        return 1;
    }
}