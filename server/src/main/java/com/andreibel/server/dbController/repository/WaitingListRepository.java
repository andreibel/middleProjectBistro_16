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

public class WaitingListRepository {
    private static WaitingListRepository instance;
    private final TransactionManager tx;

    private WaitingListRepository() {
        this.tx = TransactionManager.getInstance();
    }

    public static WaitingListRepository getInstance() {
        if (instance == null) {
            instance = new WaitingListRepository();
        }
        return instance;
    }

    public Waiting addWaiting(Waiting waiting) throws SQLException {
        String sql = """
                INSERT INTO bistro.`waiting`
                (numberOfGuests, waitingDateTime, conformationCode, orderNumber,subscriberId, email, phoneNumber)
                VALUES (?,?,?,?,?,?,?)
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
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Insert succeeded but no generated key returned");
                }
                return findById(keys.getInt(1));
            }
        }
    }

    public Waiting findById(int id) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`waiting`
                WHERE waitingNumber = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToWaiting(rs) : null;
            }
        }
    }

    public Waiting findByConformationCode(UUID conformationCode) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`waiting`
                WHERE conformationCode = ?;
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, conformationCode.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToWaiting(rs) : null;
            }
        }
    }

    public int countNumberOfActive() throws SQLException {
        String sql = """
                SELECT COUNT(*) as count
                FROM bistro.`waiting`
                WHERE isCurrentlyWaiting=1;
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    public List<Waiting> getWaitingByMonth(int month, int year) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`waiting`
                WHERE MONTH(waitingDateTime) = ?
                AND YEAR(waitingDateTime) = ?
                ORDER BY waitingDateTime DESC;
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

    public void updateWaitingState(UUID conformationCode, boolean isWaiting) throws SQLException {
        String sql = """
                UPDATE bistro.`waiting`
                SET isCurrentlyWaiting = ?
                WHERE conformationCode = ?;
                """;

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setBoolean(1, isWaiting);
            stmt.setString(2, conformationCode.toString());
            stmt.executeUpdate();
        }
    }

    public List<Waiting> getCurrentWaitingActive() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`waiting`
                WHERE isCurrentlyWaiting = true
                ORDER BY (orderNumber IS NOT NULL) DESC, waitingDateTime ASC;
                """;
        List<Waiting> waitingList = new ArrayList<>();
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                waitingList.add(mapRelToWaiting(rs));
            }
        }
        return waitingList;
    }

}


