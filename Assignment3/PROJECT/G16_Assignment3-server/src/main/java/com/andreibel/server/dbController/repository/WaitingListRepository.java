package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Waiting;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.andreibel.server.utils.WaitingListMapper.mapRelToWaiting;

/**
 * Repository for Waiting List operations.
 * <p>
 * Manages waiting list entries with smart priority sorting:
 * - Customers with reservations sorted by reservation time (earliest first)
 * - Walk-in customers sorted by arrival time (FIFO)
 *
 * @author Aviv
 */
public class WaitingListRepository {
    private static WaitingListRepository instance;
    private final TransactionManager tx;

    /**
     * Private constructor - Singleton pattern.
     */
    private WaitingListRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * Gets singleton instance of WaitingListRepository.
     *
     * @return singleton instance
     */
    public static WaitingListRepository getInstance() {
        if (instance == null) {
            instance = new WaitingListRepository();
        }
        return instance;
    }
    /**
     * Adds a new waiting list entry to the database.
     * <p>
     * If the customer has an order (reservation), validates that it exists and is not cancelled.
     * Auto-generates waitingNumber and sets waitingDateTime to current timestamp.
     *
     * @param waiting the waiting list entry to add
     * @return the newly created Waiting object with generated waitingNumber
     * @throws SQLException if validation fails or database operation fails
     */
    public Waiting addWaiting(Waiting waiting) throws SQLException {

        String sql = """
                INSERT INTO bistro.`Waiting`
                (numberOfGuests, conformationCode, orderNumber, subscriberId, email, phoneNumber)
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, waiting.getNumberOfGuests());
            stmt.setString(2, waiting.getConformationCode().toString());

            if (waiting.getOrderNumber() == null) stmt.setNull(3, Types.INTEGER);
            else stmt.setInt(3, waiting.getOrderNumber());

            if (waiting.getSubscriberId() == null) stmt.setNull(4, Types.INTEGER);
            else stmt.setInt(4, waiting.getSubscriberId());

            if (waiting.getEmail() == null) stmt.setNull(5, Types.VARCHAR);
            else stmt.setString(5, waiting.getEmail());

            if (waiting.getPhoneNumber() == null) stmt.setNull(6, Types.VARCHAR);
            else stmt.setString(6, waiting.getPhoneNumber());


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
     * Finds a waiting list entry by ID (waitingNumber).
     *
     * @param id the waitingNumber
     * @return the Waiting object, or null if not found
     * @throws SQLException if database operation fails
     */
    public Waiting findById(int id) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Waiting`
                WHERE waitingNumber = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToWaiting(rs) : null;
            }
        }
    }




    /**
     * Retrieves active waiting customers sorted by priority:
     * <p>
     * Priority  1: Customers with reservations, sorted by reservation time (orderDateTime) - earliest first
     * Priority 2: C ustomers without reservations, sorted by FIFO (waitingDateTime) - arrival first
     * <p>
     * Example scenario (current time 14:30):
     * - Customer with reservation 14:45 arrives at 14:30 → placed before reservation 15:00
     * - Customer with reservation 15:00 → placed after reservation 14:45
     * - Walk-in customer with no reservation → placed after all reservations, sorted by arrival time
     *
     * @return sorted list of currently waiting customers (isCurrentlyWaiting = 1)
     * @throws SQLException if database operation fails
     */
    public List<Waiting> getCurrentWaitingActive() throws SQLException {
        String sql = """
                SELECT w.*
                FROM bistro.`Waiting` w
                LEFT JOIN bistro.`Order` o ON w.orderNumber = o.orderNumber
                WHERE w.isCurrentlyWaiting = 1
                AND w.waitingDateTime >= CURDATE()
                  AND w.waitingDateTime  < CURDATE() + INTERVAL 1 DAY
                ORDER BY
                    CASE
                        WHEN w.orderNumber IS NOT NULL THEN 0  -- Reservations first
                        ELSE 1                                   -- Walk-ins second
                    END ASC,
                    CASE
                        WHEN w.orderNumber IS NOT NULL THEN o.orderDateTime  -- Sort reservations by reservation time
                        ELSE w.waitingDateTime                                -- Sort walk-ins by arrival time
                    END ASC;
                """;

        List<Waiting> waitingList = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                waitingList.add(mapRelToWaiting(rs));
            }
        }
        return waitingList;
    }

    /**
     * Removes a customer from the waiting list.
     * <p>
     * If the customer has a reservation (orderNumber), the order will be cancelled.
     * If the customer is a walk-in (no orderNumber), they are simply removed from waiting.
     * <p>
     * The entry is marked as isCurrentlyWaiting = 0 (soft delete to preserve history).
     *
     * @throws SQLException if database operation fails or customer not found
     */
    public void removeNotTodayWaitingList() throws SQLException {
        String sql = """
                UPDATE bistro.`Waiting`
                SET isCurrentlyWaiting = 0
                WHERE DATE(waitingDateTime) <> CURDATE();
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the arrive timestamp for a waiting customer.
     * Sets waitingArriveDateTime to current timestamp when customer arrives.
     *
     * @param conformationCode the customer's confirmation code
     * @throws SQLException if customer not found or database operation fails
     */
    public void waitingArriveToTable(UUID conformationCode) throws SQLException {
        String sql = """
                UPDATE bistro.`Waiting`
                SET waitingArriveDateTime = NOW(), isCurrentlyWaiting = 0
                WHERE conformationCode = ?;
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            stmt.executeUpdate();
        }
    }

    /**
     * Gets all waiting list entries (both active and inactive) for reporting.
     * Used for generating reports and analytics.
     *
     * @return list of all Waiting entries ordered by date descending
     * @throws SQLException if database operation fails
     */
    public List<Waiting> getAllWaitingSitNow() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Waiting`
                WHERE isCurrentlyWaiting = 0
                AND isWaitingCompleted = 0
                AND waitingArriveDateTime IS NOT NULL;
                """;

        List<Waiting> waitingList = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                waitingList.add(mapRelToWaiting(rs));
            }
        }
        return waitingList;
    }

    public void completeWaitingListByConformationCode(UUID conformationCode) throws SQLException {
        String sql = """
                UPDATE bistro.`Waiting`
                SET isWaitingCompleted = 1
                WHERE conformationCode = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            stmt.executeUpdate();
        }
    }

    public Waiting getWaitingByConformationCode(UUID conformationCode) throws SQLException {

        String sql = """
                SELECT *
                FROM bistro.`Waiting`
                WHERE conformationCode = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1,conformationCode.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToWaiting(rs) : null;
            }
        }
    }

    public Map<LocalDate, Integer> getCountInThisMount() throws SQLException {

        String sql = """
                SELECT DATE(waitingDateTime) DateOnly, COUNT(*) as subCount
                FROM bistro.`Waiting`
                WHERE subscriberId IS NOT NULL
                  AND YEAR(waitingDateTime) = YEAR(CURRENT_DATE)
                  AND MONTH(waitingDateTime) = MONTH(CURRENT_DATE)
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
    public Map<LocalDate, Map<LocalTime, Integer>> getCountInThisMonthByTime() throws SQLException {

        String sql = """
            SELECT DATE(waitingDateTime) AS dateOnly,
                   TIME(waitingDateTime) AS timeOnly,
                   COUNT(*) AS subCount
            FROM bistro.`Waiting`
            WHERE YEAR(waitingDateTime) = YEAR(CURRENT_DATE)
              AND MONTH(waitingDateTime) = MONTH(CURRENT_DATE)
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

    public Map<LocalDate, Integer> getDelaysOrders() throws SQLException {

        String sql = """
                SELECT DATE(waitingDateTime) DateOnly, COUNT(*) as orderCount
                FROM bistro.`Waiting`
                WHERE orderNumber IS NOT NULL
                  AND YEAR(waitingDateTime) = YEAR(CURRENT_DATE)
                  AND MONTH(waitingDateTime) = MONTH(CURRENT_DATE)
                GROUP BY DateOnly;
                """;
        Map<LocalDate, Integer> map = new TreeMap<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(LocalDate.from(rs.getTimestamp(1).toLocalDateTime()), rs.getInt("orderCount"));
                }
            }
        }
        return map;
    }

    /**
     * Batch-completes waiting entries that have been seated for 2 hours.
     *
     * <p>This method is intended for a scheduler. It marks waiting entries as completed when:
     * they have arrived (waitingArriveDateTime IS NOT NULL), are not yet completed,
     * and their arrival time is earlier than {@code now - 2 hours}.</p>
     *
     * @param now reference time used to compute the cutoff {@code now.minusHours(2)}
     * @return list of waiting entries that were closed (for printing invoices)
     * @throws SQLException if a database access error occurs
     */
    public List<Waiting> findWaitingDueToClose(LocalDateTime now) throws SQLException {
        LocalDateTime cutoff = now.minusHours(2);

        // First, SELECT the waiting entries that will be closed
        String selectSql = """
                SELECT *
                FROM bistro.`Waiting`
                WHERE waitingArriveDateTime IS NOT NULL
                  AND isWaitingCompleted = 0
                  AND waitingArriveDateTime <= ?
                """;

        List<Waiting> waitingToClose = new ArrayList<>();
        try (PreparedStatement ps = tx.currentConnection().prepareStatement(selectSql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoff));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) waitingToClose.add(mapRelToWaiting(rs));
            }
        }

        // Then, UPDATE those entries to mark them as completed
        if (!waitingToClose.isEmpty()) {
            String updateSql = """
                    UPDATE bistro.`Waiting`
                    SET isWaitingCompleted = 1
                    WHERE waitingArriveDateTime IS NOT NULL
                      AND isWaitingCompleted = 0
                      AND waitingArriveDateTime <= ?
                    """;

            try (PreparedStatement ps = tx.currentConnection().prepareStatement(updateSql)) {
                ps.setTimestamp(1, Timestamp.valueOf(cutoff));
                ps.executeUpdate();
            }
        }

        return waitingToClose;
    }

}