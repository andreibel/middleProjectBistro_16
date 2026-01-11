package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.OpenTime;
import com.andreibel.server.utils.OpenTimeMapper;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Repository for accessing and managing the restaurant opening-hours configuration stored in
 * the {@code bistro.OpenTime} table.
 *
 * <p>The table is modeled with a single "regular" configuration row (identified by {@code id = 1})
 * and optional "special day" configurations identified by their {@code SpatialDate}. A special day
 * may override the regular configuration for a specific date.</p>
 *
 * <p>This repository is JDBC-based and relies on {@link TransactionManager} to provide the
 * current connection via {@link TransactionManager#currentConnection()}.
 * All methods are expected to be called within an active transaction/context managed by
 * {@link TransactionManager}.</p>
 */
public class OpenTimeRepository {

    private static OpenTimeRepository instance;
    private final TransactionManager tx;

    private OpenTimeRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * Returns the singleton instance of this repository.
     *
     * <p>This repository is state-less and holds only a reference to the
     * {@link TransactionManager} singleton.</p>
     *
     * @return the singleton {@link OpenTimeRepository} instance
     */
    public static OpenTimeRepository getInstance() {
        if (instance == null) instance = new OpenTimeRepository();
        return instance;
    }

    /**
     * Loads the "regular" (default) opening-hours configuration.
     *
     * <p>By convention, the regular configuration is stored in {@code bistro.OpenTime}
     * with {@code id = 1}. If the row does not exist, this method returns {@code null}.</p>
     *
     * @return the regular {@link OpenTime} record, or {@code null} if not found
     * @throws SQLException if a database access error occurs
     */
    public OpenTime findRegular() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`OpenTime`
                WHERE id = 1;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) return null;
            return OpenTimeMapper.mapRelToOpenTime(rs);
        }
    }

    /**
     * Inserts a new "special day" opening-hours configuration for a specific calendar date.
     *
     * <p>The created row will apply only for the provided {@code date} and can be used by higher-level
     * logic to override the regular opening hours. The {@code interval} typically represents the
     * reservation slot size in minutes.</p>
     *
     * @param date     the special date for which the opening hours apply
     * @param title    human-readable title/description for the special day
     * @param open     opening time for the given date
     * @param close    closing time for the given date
     * @param interval slot interval (usually minutes) used for availability calculations
     * @throws SQLException if the insert fails or a database access error occurs
     */
    public void addNewSpecial(LocalDate date, String title, LocalTime open, LocalTime close, int interval) throws SQLException {
        String sql = """
                INSERT INTO bistro.`OpenTime`
                (`SpatialDate`,`title`, `openTime`, `closeTime`, `interval`)
                VALUES (?,?, ?, ?, ?);
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setString(2, title);
            stmt.setTime(3, Time.valueOf(open));
            stmt.setTime(4, Time.valueOf(close));
            stmt.setInt(5, interval);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the "regular" (default) opening-hours configuration.
     *
     * <p>This method updates the row with {@code id = 1}. It does not affect any special-day
     * rows. The {@code interval} typically represents the reservation slot size in minutes.</p>
     *
     * @param open     new default opening time
     * @param close    new default closing time
     * @param interval new slot interval (usually minutes)
     * @throws SQLException if the update fails or a database access error occurs
     */
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

    /**
     * Finds a special-day opening-hours configuration for the given date.
     *
     * <p>If there is no special configuration for {@code date}, the method returns {@code null}.
     * Callers typically fall back to {@link #findRegular()} when this method returns {@code null}.</p>
     *
     * @param date the calendar date to look up (as {@link java.sql.Date})
     * @return the matching special-day {@link OpenTime}, or {@code null} if none exists
     * @throws SQLException if a database access error occurs
     */
    public OpenTime findSpecial(Date date) throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`OpenTime`
                WHERE SpatialDate = ?;
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? OpenTimeMapper.mapRelToOpenTime(rs) : null;
            }
        }
    }
}