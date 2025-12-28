package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.OpenTime;
import com.andreibel.server.utils.OpenTimeMapper;

import java.sql.*;
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

        String sql = "SELECT * FROM bistro.`OpenTime` WHERE id = 1;";
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) {
                return null; // או throw חדש: אין קונפיגורציה רגילה
            }

            return OpenTimeMapper.mapRelToOpenTime(rs);
        }
    }

    public List<OpenTime> findSpecialActual() throws SQLException {
        List<OpenTime> openTimes = new java.util.ArrayList<>();
        String sql = "SELECT * " + "FROM bistro.`OpenTime` " + "WHERE id <> 1 and " + OpenTime.SPATIAL_DATE + " >= NOW();";
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                openTimes.add(OpenTimeMapper.mapRelToOpenTime(rs));
            }
        }
        return openTimes;
    }

    public void addNewSpecial(Date date, Time open, Time close, int interval) throws SQLException {
        String sql = "INSERT INTO bistro.`OpenTime` (`id`, `SpatialDate`, `openTime`, `closeTime`, `interval`) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setDate(2, date);
            stmt.setTime(3, open);
            stmt.setTime(4, close);
            stmt.setInt(5, interval);
            stmt.executeUpdate();
        }
    }

    public void updateRegular(Time open, Time close, int interval) throws SQLException {
        String sql = "UPDATE bistro.`OpenTime` " + "SET `openTime` = ?, `closeTime` = ?, `interval` = ? " + "WHERE id = 1;";
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setTime(1, open);
            stmt.setTime(2, close);
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


