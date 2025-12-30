package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Waiting;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.andreibel.server.utils.WaitingListMapper.mapRelToWaiting;

/**
 * Repository for Waiting List operations.
 * 
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
     * 
     * If the customer has an order (reservation), validates that it exists and is not cancelled.
     * Auto-generates waitingNumber and sets waitingDateTime to current timestamp.
     * 
     * @param waiting the waiting list entry to add
     * @return the newly created Waiting object with generated waitingNumber
     * @throws SQLException if validation fails or database operation fails
     */
    public Waiting addWaiting(Waiting waiting) throws SQLException {
        // Validate that if orderNumber is provided, the order actually exists and is not cancelled
        if (waiting.getOrderNumber() != null) {
            validateOrderExists(waiting.getOrderNumber());
        }

        String sql = """
                INSERT INTO bistro.`Waiting`
                (numberOfGuests, waitingDateTime, conformationCode, orderNumber, subscriberId, email, phoneNumber, isCurrentlyWaiting)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, waiting.getNumberOfGuests());
            stmt.setTimestamp(2, Timestamp.valueOf(waiting.getWaitingDateTime()));
            stmt.setString(3, waiting.getConformationCode().toString());

            if (waiting.getOrderNumber() == null) stmt.setNull(4, java.sql.Types.INTEGER);
            else stmt.setInt(4, waiting.getOrderNumber());
            
            if (waiting.getSubscriberId() == null) stmt.setNull(5, java.sql.Types.INTEGER);
            else stmt.setInt(5, waiting.getSubscriberId());
            
            if (waiting.getEmail() == null) stmt.setNull(6, java.sql.Types.VARCHAR);
            else stmt.setString(6, waiting.getEmail());
            
            if (waiting.getPhoneNumber() == null) stmt.setNull(7, java.sql.Types.VARCHAR);
            else stmt.setString(7, waiting.getPhoneNumber());
            
            stmt.setBoolean(8, true); // isCurrentlyWaiting = 1 by default
            
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
                WHERE waitingNumber = ?
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToWaiting(rs) : null;
            }
        }
    }

    /**
     * Finds a waiting list entry by confirmation code (UUID).
     * 
     * @param conformationCode the customer's confirmation code
     * @return the Waiting object, or null if not found
     * @throws SQLException if database operation fails
     */
    public Waiting findByConformationCode(UUID conformationCode) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Waiting`
                WHERE conformationCode = ?
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToWaiting(rs) : null;
            }
        }
    }

    /**
     * Counts the number of currently active waiting customers.
     * 
     * @return count of customers in waiting list where isCurrentlyWaiting = 1
     * @throws SQLException if database operation fails
     */
    public int countNumberOfActive() throws SQLException {
        String sql = """
                SELECT COUNT(*) as count
                FROM bistro.`Waiting`
                WHERE isCurrentlyWaiting = 1
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); 
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Gets waiting list entries for a specific month and year.
     * Results are ordered by waitingDateTime descending (most recent first).
     * 
     * @param month the month (1-12)
     * @param year the year
     * @return list of waiting entries for that month
     * @throws SQLException if database operation fails
     */
    public List<Waiting> getWaitingByMonth(int month, int year) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Waiting`
                WHERE MONTH(waitingDateTime) = ?
                AND YEAR(waitingDateTime) = ?
                ORDER BY waitingDateTime DESC
                """;
        List<Waiting> waitingList = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    waitingList.add(mapRelToWaiting(rs));
                }
            }
        }
        return waitingList;
    }

    /**
     * Updates the waiting state of a customer (seated or left).
     * 
     * @param conformationCode the customer's confirmation code
     * @param isWaiting true if still waiting, false if seated/left
     * @throws SQLException if database operation fails
     */
    public void updateWaitingState(UUID conformationCode, boolean isWaiting) throws SQLException {
        String sql = """
                UPDATE bistro.`Waiting`
                SET isCurrentlyWaiting = ?
                WHERE conformationCode = ?
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setBoolean(1, isWaiting);
            stmt.setString(2, conformationCode.toString());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves active waiting customers sorted by priority:
     * 
     * Priority 1: Customers with reservations, sorted by reservation time (orderDateTime) - earliest first
     * Priority 2: Customers without reservations, sorted by FIFO (waitingDateTime) - arrival first
     * 
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
                ORDER BY 
                    CASE 
                        WHEN w.orderNumber IS NOT NULL THEN 0  -- Reservations first
                        ELSE 1                                   -- Walk-ins second
                    END ASC,
                    CASE 
                        WHEN w.orderNumber IS NOT NULL THEN o.orderDateTime  -- Sort reservations by reservation time
                        ELSE w.waitingDateTime                                -- Sort walk-ins by arrival time
                    END ASC
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
     * 
     * If the customer has a reservation (orderNumber), the order will be cancelled.
     * If the customer is a walk-in (no orderNumber), they are simply removed from waiting.
     * 
     * The entry is marked as isCurrentlyWaiting = 0 (soft delete to preserve history).
     * 
     * @param conformationCode the customer's confirmation code
     * @return true if successfully removed, false if not found
     * @throws SQLException if database operation fails or customer not found
     */
    public boolean removeFromWaitingList(UUID conformationCode) throws SQLException {
        // First, find the waiting entry to check if there's an associated order
        Waiting waiting = findByConformationCode(conformationCode);
        
        if (waiting == null) {
            throw new SQLException("Waiting list entry with confirmation code " + conformationCode + " not found");
        }
        
        // If customer has a reservation, cancel the order
        if (waiting.getOrderNumber() != null) {
            cancelOrder(waiting.getOrderNumber());
        }
        
        // Remove from waiting list by setting isCurrentlyWaiting to false
        String sql = """
                UPDATE bistro.`Waiting`
                SET isCurrentlyWaiting = 0
                WHERE conformationCode = ?
                """;
        
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Alternative method: Remove from waiting list by ID (waitingNumber).
     * Useful if you have the waiting number instead of confirmation code.
     * 
     * If the customer has a reservation, the order will be cancelled.
     * If the customer is a walk-in, they are simply removed from waiting.
     * 
     * @param waitingNumber the waiting list entry ID
     * @return true if successfully removed, false if not found
     * @throws SQLException if database operation fails or customer not found
     */
    public boolean removeFromWaitingListById(int waitingNumber) throws SQLException {
        Waiting waiting = findById(waitingNumber);
        
        if (waiting == null) {
            throw new SQLException("Waiting list entry #" + waitingNumber + " not found");
        }
        
        // If customer has a reservation, cancel the order
        if (waiting.getOrderNumber() != null) {
            cancelOrder(waiting.getOrderNumber());
        }
        
        String sql = """
                UPDATE bistro.`Waiting`
                SET isCurrentlyWaiting = 0
                WHERE waitingNumber = ?
                """;
        
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, waitingNumber);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Validates that an order exists and is not cancelled.
     * 
     * @param orderNumber the order number to validate
     * @throws SQLException if order doesn't exist or is cancelled
     */
    private void validateOrderExists(int orderNumber) throws SQLException {
        String sql = """
                SELECT orderCancelled
                FROM bistro.`Order`
                WHERE orderNumber = ?
                """;
        
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Order #" + orderNumber + " does not exist");
                }
                if (rs.getBoolean("orderCancelled")) {
                    throw new SQLException("Order #" + orderNumber + " has been cancelled");
                }
            }
        }
    }

    /**
     * Cancels an order by setting orderCancelled flag to 1.
     * 
     * @param orderNumber the order number to cancel
     * @throws SQLException if order doesn't exist or database operation fails
     */
    private void cancelOrder(int orderNumber) throws SQLException {
        String sql = """
                UPDATE bistro.`Order`
                SET orderCancelled = 1
                WHERE orderNumber = ?
                """;
        
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, orderNumber);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Order #" + orderNumber + " not found for cancellation");
            }
        }
    }
}