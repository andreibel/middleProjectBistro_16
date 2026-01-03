package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.OpenTime;
import com.andreibel.server.utils.OpenTimeMapper;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class OpenTimeRepository {
    private static OpenTimeRepository instance;
    private final TransactionManager tx;

    private OpenTimeRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * @return singleton instance of OpenTimeRepository
     */
    public static OpenTimeRepository getInstance() {
        if (instance == null) {
            instance = new OpenTimeRepository();
        }
        return instance;
    }

    public OpenTime findRegular() throws SQLException {

        String sql = """
                SELECT * 
                FROM bistro.`OpenTime` 
                WHERE id = 1;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) {
                return null;
            }

            return OpenTimeMapper.mapRelToOpenTime(rs);
        }
    }

    public List<OpenTime> findSpecialActual() throws SQLException {
        List<OpenTime> openTimes = new java.util.ArrayList<>();
        String sql = """
                SELECT * 
                FROM bistro.`OpenTime` 
                WHERE id <> 1
                AND SpatialDate >= NOW(); 
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                openTimes.add(OpenTimeMapper.mapRelToOpenTime(rs));
            }
        }
        return openTimes;
    }

    public void addNewSpecial(LocalDate date, LocalTime open, LocalTime close, int interval) throws SQLException {
        String sql = """
                INSERT INTO bistro.`OpenTime`
                    (`SpatialDate`, `openTime`, `closeTime`, `interval`)
                VALUES (?, ?, ?, ?);
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setTime(2, Time.valueOf(open));
            stmt.setTime(3, Time.valueOf(close));
            stmt.setInt(4, interval);
            stmt.executeUpdate();
        }
    }

    public void updateRegular(LocalTime open, LocalTime close, int interval) throws SQLException {
        String sql = """
                UPDATE bistro.`OpenTime`
                SET `openTime` = ?, `closeTime` = ?, `interval` = ?
                WHERE id = 1;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setTime(1, Time.valueOf(open));
            stmt.setTime(2, Time.valueOf(close));
            stmt.setInt(3, interval);
            stmt.executeUpdate();
        }
    }

    public OpenTime findSpecial(Date date) throws SQLException {
        String sql = "SELECT * FROM bistro.`OpenTime` WHERE SpatialDate = ?;";
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? OpenTimeMapper.mapRelToOpenTime(rs) : null;
            }
        }
    }
}


