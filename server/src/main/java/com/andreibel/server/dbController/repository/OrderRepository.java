package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Order;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.andreibel.server.utils.OrderMapper.mapRelToOrder;

/**
 * JDBC repository for {@link Order} persistence and retrieval.
 *
 * <p>This repository encapsulates all SQL access to the {@code bistro.Order} table using
 * plain JDBC. It relies on {@link TransactionManager} for obtaining the current transactional
 * {@link Connection} via {@link TransactionManager#currentConnection()}.</p>
 *
 * <p><b>Transaction requirement:</b> all methods are expected to run inside an active transaction
 * (for example via {@code tx.inTransaction(...)}). Calling these methods without a transaction
 * may result in {@code null} connections or unexpected behavior.</p>
 *
 * <p><b>Order lifecycle flags:</b> the repository uses soft state flags rather than deleting rows:
 * {@code orderCancelled}, {@code orderArrive}, and {@code orderCompleted}. Some methods update these
 * flags (e.g., arrival, cancellation, completion) and scheduler-related methods perform batch updates
 * based on time conditions.</p>
 *
 * <p>Implemented as a thread-unsafe singleton (sufficient for typical single-instance server usage).
 * If you use multiple class loaders or need concurrency-safe lazy init, adjust {@link #getInstance()}.</p>
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
     * Returns the singleton instance of {@link OrderRepository}.
     *
     * @return singleton repository instance
     */
    public static OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    /**
     * Loads all currently active orders (arrived but not yet completed or cancelled).
     *
     * <p>An order is considered active when:
     * <ul>
     *     <li>{@code orderArrive = 1} (customer has checked in)</li>
     *     <li>{@code orderCompleted = 0} (not yet closed)</li>
     *     <li>{@code orderCancelled = 0} (not cancelled)</li>
     * </ul>
     *
     * @return list of active orders (possibly empty)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findActiveOrders() throws SQLException {
        String sql = """
                SELECT * FROM bistro.`Order`
                WHERE orderArrive = 1
                AND orderCompleted = 0
                AND orderCancelled = 0;
                """;
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) orders.add(mapRelToOrder(rs));
        }
        return orders;
    }
    /**
     * Loads all orders.
     *
     * <p>This method is useful for subscriber history/report screens. If you want truly "all orders",
     * remove the {@code subscriberId IS NOT NULL} filter.</p>
     *
     * @return list of subscriber orders (possibly empty)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findAll() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`;
                """;
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) orders.add(mapRelToOrder(rs));
        }
        return orders;
    }

    /**
     * Loads all orders.
     *
     * <p>This method is useful for subscriber history/report screens. If you want truly "all orders",
     * remove the {@code subscriberId IS NOT NULL} filter.</p>
     *
     * @return list of subscriber orders (possibly empty)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findAllActive() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`
                WHERE orderCancelled = 0
                  AND orderCompleted = 0
                  AND Date(orderDateTime) = CURRENT_DATE
                """;
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) orders.add(mapRelToOrder(rs));
        }
        return orders;
    }

    /**
     * Finds an order by its primary key ({@code orderNumber}).
     *
     * @param orderNumber primary key value
     * @return matching {@link Order} or {@code null} if not found
     * @throws SQLException if a database access error occurs
     */

    public Order findById(int orderNumber) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`
                WHERE orderNumber = ?;
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToOrder(rs) : null;
            }
        }
    }

    /**
     * Finds an order by its confirmation code.
     *
     * @param conformationCode confirmation code (UUID)
     * @return matching {@link Order} or {@code null} if not found
     * @throws SQLException if a database access error occurs
     */
    public Order findByConformationCode(UUID conformationCode) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`
                WHERE conformationCode = ?;
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToOrder(rs) : null;
            }
        }
    }

    /**
     * Loads all orders belonging to a subscriber id.
     *
     * @param subscriberId subscriber identifier
     * @return list of subscriber orders (possibly empty)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findBySubscriberId(int subscriberId) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`
                WHERE subscriberId = ?;
                """;

        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, subscriberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) orders.add(mapRelToOrder(rs));
            }
        }
        return orders;
    }

    /**
     * Loads all orders by customer email.
     *
     * <p>Used for guest flows where orders are not associated with a subscriber id.</p>
     *
     * @param email customer email address
     * @return list of orders with the given email (possibly empty)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findByEmail(String email) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`
                WHERE email = ?;
                """;

        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) orders.add(mapRelToOrder(rs));
            }
        }
        return orders;
    }

    /**
     * Loads all orders by customer phone number.
     *
     * @param phone customer phone number (string, as stored in DB)
     * @return list of orders with the given phone number (possibly empty)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findByPhone(String phone) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`
                WHERE phoneNumber = ?;
                """;

        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) orders.add(mapRelToOrder(rs));
            }
        }
        return orders;
    }

    /**
     * Persists a new {@link Order} row and returns the stored entity reloaded from the database.
     *
     * <p>Nullable fields ({@code subscriberId}, {@code email}, {@code phoneNumber}) are inserted as SQL
     * {@code NULL} when missing. After insertion, the method reads the generated primary key and calls
     * {@link #findById(int)} to return a fully mapped object.</p>
     *
     * @param newOrder order to persist
     * @return stored order (reloaded by generated key)
     * @throws SQLException if insert fails or no generated key is returned
     */
    public Order save(Order newOrder) throws SQLException {
        String sql = """
                INSERT INTO bistro.`Order`
                (numberOfGuests, conformationCode, orderDateTime, subscriberId, email, phoneNumber)
                values (?,?,?,?,?,?)
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, newOrder.getNumberOfGuests());
            stmt.setString(2, newOrder.getConformationCode().toString());
            stmt.setTimestamp(3, Timestamp.valueOf(newOrder.getOrderDateTime()));

            if (newOrder.getSubscriberId() == null) stmt.setNull(4, Types.NULL);
            else stmt.setInt(4, newOrder.getSubscriberId());

            if (newOrder.getEmail() == null) stmt.setNull(5, Types.NULL);
            else stmt.setString(5, newOrder.getEmail());

            if (newOrder.getPhoneNumber() == null) stmt.setNull(6, Types.NULL);
            else stmt.setString(6, newOrder.getPhoneNumber());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Insert succeeded but no generated key returned");
                return findById(keys.getInt(1));
            }
        }
    }

    /**
     * Soft-cancels an order by confirmation code.
     *
     * <p>The row is not removed from the database. This method sets {@code orderCancelled = 1} and
     * returns the number of affected rows (typically 0 or 1).</p>
     *
     * @param conformationCode order confirmation code
     * @return number of updated rows
     * @throws SQLException if a database access error occurs
     */
    public int deleteByConformationCode(UUID conformationCode) throws SQLException {
        String sql = """
                UPDATE bistro.`Order`
                SET orderCancelled = 1
                WHERE conformationCode = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            return stmt.executeUpdate();
        }
    }

    /**
     * Marks an order as arrived (check-in) and stores arrival time.
     *
     * <p>Updates {@code orderArrive = 1} and sets {@code orderArriveDateTime = NOW()} for the given
     * confirmation code.</p>
     *
     * @param conformationCode order confirmation code
     * @return number of updated rows (typically 0 or 1)
     * @throws SQLException if a database access error occurs
     */
    public int setArrived(UUID conformationCode) throws SQLException {
        String sql = """
                UPDATE bistro.`Order`
                SET orderArrive = 1, orderArriveDateTime = NOW()
                WHERE conformationCode = ?
                  AND orderDateTime BETWEEN DATE_SUB(NOW(), INTERVAL 15 MINUTE)
                                        AND DATE_ADD(NOW(), INTERVAL 30 MINUTE)
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            return stmt.executeUpdate();
        }
    }

    /**
     * Marks an order as completed (closed) by confirmation code.
     *
     * <p>This method sets {@code orderCompleted = 1}. It does not cancel the order and does not
     * modify arrival state.</p>
     *
     * @param conformationCode order confirmation code
     * @throws SQLException if a database access error occurs
     */
    public void completeOrder(UUID conformationCode) throws SQLException {
        String sql = """
                UPDATE bistro.`Order`
                SET orderCompleted = 1
                WHERE conformationCode = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            stmt.executeUpdate();
        }
    }

    /**
     * Loads all not-cancelled orders that start within the given calendar date.
     *
     * <p>The filter uses a half-open interval:
     * {@code [date 00:00, (date+1) 00:00)}. Only orders with {@code orderCancelled = 0} are returned.</p>
     *
     * @param date day to fetch (local date)
     * @return list of orders for the given day (possibly empty)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findAllDateOrders(LocalDate date) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`
                WHERE orderDateTime >= ?
                  AND orderDateTime <  ?
                  AND orderCancelled = 0
                """;

        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = tx.currentConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(date.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(date.plusDays(1).atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapRelToOrder(rs));
            }
        }
        return orders;
    }

    /**
     * Batch-cancels late orders (no arrival within a grace period).
     *
     * <p>An order is considered late if it is not cancelled, not completed, not arrived, and its
     * {@code orderDateTime} is earlier than {@code now - graceMinutes}. The method updates rows by
     * setting {@code orderCancelled = 1}.</p>
     *
     * @param graceMinutes allowed lateness window in minutes (e.g., 10–20)
     * @return list of orders that were cancelled (for printing notifications)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> cancelLateOrders(int graceMinutes) throws SQLException {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(graceMinutes);

        // First, SELECT the orders that will be cancelled
        String selectSql = """
                SELECT *
                FROM bistro.`Order`
                WHERE orderCancelled = 0
                  AND orderCompleted = 0
                  AND orderArrive = 0
                  AND orderDateTime < ?
                """;

        List<Order> ordersToCancel = new ArrayList<>();
        try (PreparedStatement ps = tx.currentConnection().prepareStatement(selectSql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoff));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ordersToCancel.add(mapRelToOrder(rs));
            }
        }

        // Then, UPDATE those orders to mark them as cancelled
        if (!ordersToCancel.isEmpty()) {
            String updateSql = """
                    UPDATE bistro.`Order`
                    SET orderCancelled = 1
                    WHERE orderCancelled = 0
                      AND orderCompleted = 0
                      AND orderArrive = 0
                      AND orderDateTime < ?
                    """;

            try (PreparedStatement ps = tx.currentConnection().prepareStatement(updateSql)) {
                ps.setTimestamp(1, Timestamp.valueOf(cutoff));
                ps.executeUpdate();
            }
        }

        return ordersToCancel;
    }

    /**
     * Batch-completes arrived orders that should be closed now (fixed 2-hour duration).
     *
     * <p>This method is intended for a scheduler. It marks orders as completed when:
     * they are not cancelled, arrived, and their {@code orderDateTime} is earlier than {@code now - 2 hours}.</p>
     *
     * @param now reference time used to compute the cutoff {@code now.minusHours(2)}
     * @return list of orders that were closed (for printing invoices)
     * @throws SQLException if a database access error occurs
     */
    public List<Order> findOrdersDueToClose(LocalDateTime now) throws SQLException {
        LocalDateTime cutoff = now.minusHours(2);

        // First, SELECT the orders that will be closed
        String selectSql = """
                SELECT *
                FROM bistro.`Order`
                WHERE orderCancelled = 0
                  AND orderArrive = 1
                  AND orderCompleted = 0
                  AND orderArriveDateTime <= ?
                """;

        List<Order> ordersToClose = new ArrayList<>();
        try (PreparedStatement ps = tx.currentConnection().prepareStatement(selectSql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoff));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ordersToClose.add(mapRelToOrder(rs));
            }
        }

        // Then, UPDATE those orders to mark them as completed
        if (!ordersToClose.isEmpty()) {
            String updateSql = """
                    UPDATE bistro.`Order`
                    SET orderCompleted = 1
                    WHERE orderCancelled = 0
                      AND orderArrive = 1
                      AND orderCompleted = 0
                      AND orderArriveDateTime <= ?
                    """;

            try (PreparedStatement ps = tx.currentConnection().prepareStatement(updateSql)) {
                ps.setTimestamp(1, Timestamp.valueOf(cutoff));
                ps.executeUpdate();
            }
        }

        return ordersToClose;
    }

    /**
     * Counts subscriber orders per day for the current month.
     *
     * <p>Only orders with a non-null {@code subscriberId} are counted.
     * Results are grouped by date and returned as a map.</p>
     *
     * @return map of date to subscriber order count for the current month
     * @throws SQLException if a database access error occurs
     */
    public Map<LocalDate, Integer> getCountInThisMount() throws SQLException {

        String sql = """
                SELECT DATE(orderDateTime) DateOnly , COUNT(*) as subCount
                FROM bistro.`Order`
                WHERE subscriberId IS NOT NULL
                  AND YEAR(orderDateTime) = YEAR(CURRENT_DATE)
                  AND MONTH(orderDateTime) = MONTH(CURRENT_DATE)
                GROUP BY DateOnly;
                """;
        Map<LocalDate, Integer> map = new TreeMap<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(LocalDate.from(rs.getTimestamp(1).toLocalDateTime()), rs.getInt("subCount"));
                }
            }
        }
        return map;
    }


    /**
     * Counts arrived orders per day and time slot for the current month.
     *
     * <p>Only orders with a recorded arrival time ({@code orderArriveDateTime IS NOT NULL})
     * are included. Results are grouped by date and time, useful for generating
     * customer traffic reports.</p>
     *
     * @return nested map of date to (time to order count) for the current month
     * @throws SQLException if a database access error occurs
     */
    public Map<LocalDate, Map<LocalTime, Integer>> getCountInThisMonthByTime() throws SQLException {

        String sql = """
            SELECT DATE(orderDateTime) AS dateOnly,
                   TIME(orderDateTime) AS timeOnly,
                   COUNT(*) AS subCount
            FROM bistro.`Order`
            WHERE YEAR(orderDateTime) = YEAR(CURRENT_DATE)
              AND MONTH(orderDateTime) = MONTH(CURRENT_DATE)
              AND orderArriveDateTime IS NOT NULL
            GROUP BY dateOnly, timeOnly
            ORDER BY dateOnly, timeOnly;
            """;

        Map<LocalDate, Map<LocalTime, Integer>> map = new TreeMap<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDate date = rs.getDate("dateOnly").toLocalDate();
                LocalTime time = rs.getTime("timeOnly").toLocalTime();
                int count = rs.getInt("subCount");

                map.computeIfAbsent(date, d -> new TreeMap<>())
                        .put(time, count);
            }
        }

        return map;
    }

    /**
     * Counts late arrivals per day for the current month.
     *
     * <p>An order is considered late when the customer arrived after the
     * scheduled reservation time ({@code orderArriveDateTime > orderDateTime}).
     * Results are grouped by date for reporting purposes.</p>
     *
     * @return map of date to late order count for the current month
     * @throws SQLException if a database access error occurs
     */
    public Map<LocalDate, Integer> getLateOrders() throws SQLException {

        String sql = """
            SELECT DATE(orderDateTime) DateOnly , COUNT(*) as OrderCount
                FROM bistro.`Order`
                WHERE orderArriveDateTime > orderDateTime
                AND YEAR(orderDateTime) = YEAR(CURRENT_DATE)
                  AND MONTH(orderDateTime) = MONTH(CURRENT_DATE)
                GROUP BY DateOnly;
            """;

        Map<LocalDate, Integer> map = new TreeMap<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(LocalDate.from(rs.getTimestamp(1).toLocalDateTime()), rs.getInt("OrderCount"));
                }
            }
        }
        return map;
    }

}