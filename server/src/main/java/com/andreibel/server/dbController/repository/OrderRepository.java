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
 * <h1>JDBC repository for {@link Order} entities.</h1>
 *
 * <p>
 * Uses {@link TransactionManager} to access the current transactional {@link Connection}.
 * All methods must be executed inside an active transaction (i.e., via {@code tx.inTransaction(...)}),
 * otherwise {@code tx.currentConnection()} may fail or return an unexpected connection.
 * </p>
 *
 * <h2>SQL statements used by this repository</h2>
 * <ul>
 *   <li><b>findAll</b>: {@code SELECT * FROM bistro.`order`}</li>
 *   <li><b>findById</b>: {@code SELECT * FROM bistro.`order` WHERE orderNumber = ?}</li>
 *   <li><b>findByConformationCode</b>: {@code SELECT * FROM bistro.`order` WHERE conformationCode = ?}</li>
 *   <li><b>findOrdersCollideByDateTime</b>: selects non-cancelled, non-completed orders that overlap a 2-hour window</li>
 *   <li><b>findBySubscriberId</b>: {@code SELECT * FROM bistro.`order` WHERE subscriberId = ?}</li>
 *   <li><b>save</b>: {@code INSERT INTO bistro.`order` (...columns...) VALUES (?,?,?,?,?,?)}</li>
 *   <li><b>deleteByConformationCode</b>: soft-cancel via {@code UPDATE ... SET orderCancelled = 1 WHERE conformationCode = ?}</li>
 *   <li><b>setArrived</b>: {@code UPDATE ... SET orderArrive = 1 WHERE conformationCode = ?}</li>
 *   <li><b>completeOrder</b>: currently updates {@code orderCancelled = 1} by confirmation code (see method doc)</li>
 *   <li><b>findAllDateOrders</b>: selects orders in the date range {@code [dayStart, nextDayStart)} that are not cancelled</li>
 *   <li><b>cancelLateOrders</b>: cancels orders that are late by grace minutes and not arrived</li>
 *   <li><b>findOrdersDueToClose</b>: selects arrived orders that started at least 2 hours ago and are not cancelled/completed</li>
 * </ul>
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
     * Returns the singleton instance of {@link OrderRepository}.
     *
     * @return singleton instance of OrderRepository
     */
    public static OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    /**
     * Fetches all orders from the database.
     *
     * <h3>SQL</h3>
     * <pre>
     * SELECT *
     * FROM bistro.`order`;
     * </pre>
     *
     * <h3>Parameters</h3>
     * None.
     *
     * <h3>Result</h3>
     * Returns a list of {@link Order} mapped from the result set rows.
     *
     * @return all orders in the database
     */
    public List<Order> findAll() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Order`;
                """;
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapRelToOrder(rs));
            }
        }
        return orders;
    }

    /**
     * Fetches a single order by its primary key.
     *
     * <h3>SQL</h3>
     * <pre>
     * SELECT *
     * FROM bistro.`order`
     * WHERE orderNumber = ?;
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code orderNumber} (int) — the order primary key.</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Returns the matching {@link Order} or {@code null} if no row is found.
     *
     * @param orderNumber order primary key
     * @return matching order or {@code null}
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
     * Fetches a single order by its confirmation code.
     *
     * <h3>SQL</h3>
     * <pre>
     * SELECT *
     * FROM bistro.`order`
     * WHERE conformationCode = ?;
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code conformationCode} (UUID as String) — confirmation code to match.</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Returns the matching {@link Order} or {@code null} if no row is found.
     *
     * @param conformationCode order confirmation code
     * @return matching order or {@code null}
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
     * Finds orders that overlap a fixed 2-hour reservation window that starts at {@code date}.
     *
     * <p>
     * This query returns orders that are:
     * <ul>
     *   <li>Not cancelled ({@code orderCancelled = 0})</li>
     *   <li>Not completed ({@code orderCompleted = 0})</li>
     *   <li>Overlapping the window {@code [date, date + 2h)}.</li>
     * </ul>
     * </p>
     *
     * <h3>SQL</h3>
     * <pre>
     * SELECT *
     * FROM bistro.`order`
     * WHERE orderCancelled = 0
     *   AND orderCompleted = 0
     *   AND orderDateTime &lt; ?                       -- end of requested window (date + 2h)
     *   AND DATE_ADD(orderDateTime, INTERVAL 2 HOUR) &gt; ?  -- start of requested window (date)
     * ORDER BY orderDateTime ASC;
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code end} (Timestamp) = {@code date + 2 hours}</li>
     *   <li>2: {@code start} (Timestamp) = {@code date}</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Returns a list of colliding {@link Order} rows (can be empty).
     *
     * @param date reservation start time
     * @return colliding orders that overlap {@code [date, date + 2h)}
     */
    public List<Order> findOrdersCollideByDateTime(LocalDateTime date) throws SQLException {
        Timestamp start = Timestamp.valueOf(date);
        Timestamp end = Timestamp.valueOf(date.plusHours(2));

        String sql = """
                SELECT *
                FROM bistro.`Order`
                WHERE orderCancelled = 0
                AND orderCompleted = 0
                AND orderDateTime < ?
                AND DATE_ADD(orderDateTime, INTERVAL 2 HOUR) > ?
                ORDER BY orderDateTime ASC;
                """;
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
     * Fetches all orders that belong to a specific subscriber.
     *
     * <h3>SQL</h3>
     * <pre>
     * SELECT *
     * FROM bistro.`order`
     * WHERE subscriberId = ?;
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code subscriberId} (int) — subscriber identifier.</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Returns a list of {@link Order} rows for the subscriber (can be empty).
     *
     * @param subscriberId subscriber identifier
     * @return orders belonging to the subscriber
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
                while (rs.next()) {
                    orders.add(mapRelToOrder(rs));
                }
            }
        }
        return orders;
    }

    /**
     * Inserts a new order row and returns the persisted entity.
     *
     * <h3>SQL</h3>
     * <pre>
     * INSERT INTO bistro.`order`
     *   (numberOfGuests, conformationCode, orderDateTime, subscriberId, email, phoneNumber)
     * VALUES (?,?,?,?,?,?)
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code numberOfGuests} (int)</li>
     *   <li>2: {@code conformationCode} (UUID as String)</li>
     *   <li>3: {@code orderDateTime} (Timestamp)</li>
     *   <li>4: {@code subscriberId} (nullable int)</li>
     *   <li>5: {@code email} (nullable varchar)</li>
     *   <li>6: {@code phoneNumber} (nullable varchar)</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Performs an INSERT, then reads the generated primary key and returns the stored {@link Order}.
     *
     * @param newOrder order to persist
     * @return stored order (reloaded from DB by generated key)
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
     * Soft-deletes (cancels) an order by its confirmation code.
     *
     * <p>
     * This method does not remove the row. It sets {@code orderCancelled = 1}.
     * </p>
     *
     * <h3>SQL</h3>
     * <pre>
     * UPDATE bistro.`order`
     * SET orderCancelled = 1
     * WHERE conformationCode = ?;
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code conformationCode} (UUID as String)</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Performs an UPDATE and returns the number of affected rows.
     *
     * @param conformationCode order confirmation code
     * @return number of updated rows (0 if not found, usually 1 if updated)
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
     * Marks an order as "arrived" (checked-in) by confirmation code.
     *
     * <h3>SQL</h3>
     * <pre>
     * UPDATE bistro.`Order`
     * SET orderArrive = 1
     * WHERE conformationCode = ?;
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code conformationCode} (UUID as String)</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Performs an UPDATE and returns the number of affected rows.
     *
     * <p><b>Note:</b> this method uses {@code bistro.`Order`} (capital O) while other methods use {@code bistro.`order`}.</p>
     *
     * @param conformationCode confirmation code of the order
     * @return number of updated rows (0 if not found, usually 1 if updated)
     */
    public int setArrived(UUID conformationCode) throws SQLException {
        String sql = """
                UPDATE bistro.`Order`
                SET orderArrive = 1, orderArriveDateTime = NOW()
                WHERE conformationCode = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            return stmt.executeUpdate();
        }
    }
    /**
     * Marks an order as completed/closed by confirmation code.
     *
     * <h3>SQL</h3>
     * <pre>
     * UPDATE bistro.`order`
     * SET orderCancelled = 1
     * WHERE conformationCode = ?;
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code conformationCode} (UUID as String)</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Performs an UPDATE.
     *
     * <p><b>Important:</b> the current SQL sets {@code orderCancelled = 1}. If your intention is
     * to mark completion, you likely want {@code SET orderCompleted = 1} instead.</p>
     *
     * @param conformationCode confirmation code of the order
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
     * Fetches all not-cancelled orders that start within a given calendar date.
     *
     * <p>
     * The date filter is implemented using a half-open interval:
     * {@code [date 00:00, (date+1) 00:00)}.
     * </p>
     *
     * <h3>SQL</h3>
     * <pre>
     * SELECT *
     * FROM bistro.`order`
     * WHERE orderDateTime &gt;= ?
     *   AND orderDateTime &lt;  ?
     *   AND orderCancelled = 0
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code from} (Timestamp) = {@code date.atStartOfDay()}</li>
     *   <li>2: {@code to} (Timestamp) = {@code date.plusDays(1).atStartOfDay()}</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Returns a list of orders that start on the given date and are not cancelled.
     *
     * @param date calendar date (local)
     * @return all not-cancelled orders that start on the given date
     */
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
        }
        return orders;
    }
    /**
     * Cancels orders that are considered "late" (not arrived within a grace period).
     *
     * <p>
     * This method is intended to be called by a scheduler or as a "lazy cleanup" step.
     * It soft-cancels orders by setting {@code orderCancelled = 1} for orders that:
     * </p>
     *
     * <ul>
     *   <li>Not cancelled ({@code orderCancelled = 0})</li>
     *   <li>Not completed ({@code orderCompleted = 0})</li>
     *   <li>Not arrived ({@code orderArrive = 0})</li>
     *   <li>Started earlier than {@code now - graceMinutes}</li>
     * </ul>
     *
     * <h3>SQL</h3>
     * <pre>
     * UPDATE bistro.`order`
     * SET orderCancelled = 1
     * WHERE orderCancelled = 0
     *   AND orderCompleted = 0
     *   AND orderArrive = 0
     *   AND orderDateTime &lt; ?
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code cutoff} (Timestamp) = {@code LocalDateTime.now().minusMinutes(graceMinutes)}</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Performs an UPDATE. The current implementation returns {@code 1} regardless of affected rows.
     * If you want the actual count, return {@code ps.executeUpdate()} instead.
     *
     * @param graceMinutes allowed lateness in minutes (e.g. 15)
     * @return currently always {@code 1} (consider returning affected rows count)
     */
    public int cancelLateOrders(int graceMinutes) throws SQLException {
        String sql = """
                UPDATE bistro.`Order`
                SET orderCancelled = 1
                WHERE orderCancelled = 0
                  AND orderCompleted = 0
                  AND orderArrive = 0
                  AND orderArriveDateTime < ?
                """;
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(graceMinutes);
        try (PreparedStatement ps = tx.currentConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoff));
            ps.executeUpdate();
        }
        return 1;
    }
    /**
     * Finds arrived orders that should be closed now (fixed 2-hour duration).
     *
     * <p>
     * An order is considered "due to close" if:
     * </p>
     * <ul>
     *   <li>Not cancelled ({@code orderCancelled = 0})</li>
     *   <li>Not completed ({@code orderCompleted = 0})</li>
     *   <li>Arrived ({@code orderArrive = 1})</li>
     *   <li>Started at or before {@code now - 2 hours}</li>
     * </ul>
     *
     * <h3>SQL</h3>
     * <pre>
     * SELECT *
     * FROM bistro.`order`
     * WHERE orderCancelled = 0
     *   AND orderCompleted = 0
     *   AND orderArrive = 1
     *   AND orderDateTime &lt;= ?
     * </pre>
     *
     * <h3>Parameters</h3>
     * <ul>
     *   <li>1: {@code cutoff} (Timestamp) = {@code now.minusHours(2)}</li>
     * </ul>
     *
     * <h3>Result</h3>
     * Returns a list of orders that should be closed (can be empty).
     *
     * @param now reference "current time" used to compute {@code now - 2h}
     * @return list of orders due to close
     */
    public void findOrdersDueToClose(LocalDateTime now) throws SQLException {
        String sql = """
                UPDATE bistro.`Order`
                SET orderCompleted = 1
                WHERE orderCancelled = 0
                  AND orderArrive = 1
                  AND orderDateTime <= ?
                """;
        LocalDateTime cutoff = now.minusHours(2);
        try (PreparedStatement ps = tx.currentConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoff));
            ps.executeUpdate();
        }
    }
}