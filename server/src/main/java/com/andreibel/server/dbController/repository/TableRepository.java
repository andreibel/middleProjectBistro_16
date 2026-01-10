package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.andreibel.server.utils.TableMapper.mapRelToTable;

/**
 * Repository responsible for database access related to {@link Table} entities.
 *
 * <p>
 * This repository uses plain JDBC and depends on {@link TransactionManager}
 * to provide the active transactional {@link java.sql.Connection}.
 * All methods must be executed within an active transaction
 * (via {@link TransactionManager#inTransaction}).
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Retrieve all restaurant tables from the database</li>
 * </ul>
 *
 * <p>
 * The class follows the Singleton pattern to ensure a single instance
 * is used throughout the application lifecycle.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class TableRepository {

    /**
     * Singleton instance of {@code TableRepository}.
     */
    private static TableRepository instance;

    /**
     * Transaction manager used to access the current JDBC connection.
     */
    private final TransactionManager tx;

    private TableRepository() {
        this.tx = TransactionManager.getInstance();
    }

    /**
     * Returns the singleton instance of {@code TableRepository}.
     *
     * @return the global TableRepository instance
     */
    public static TableRepository getInstance() {
        if (instance == null) {
            instance = new TableRepository();
        }
        return instance;
    }

    /**
     * Retrieves all {@link Table} records from the database.
     *
     * @return a list of all tables; an empty list if no records exist
     * @throws SQLException if a database access error occurs
     */
    public List<Table> findAll() throws SQLException {
        String sql = """
                SELECT *
                FROM bistro.`Table`
                """;
        List<Table> tables = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tables.add(mapRelToTable(rs));
            }
        }

        return tables;
    }

    public void editTable(int capacity, int quantity)throws SQLException {
        String sql = """
                UPDATE bistro.`Table`
                SET quantity = ?
                WHERE capacity = ?
                """;
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, capacity);
            stmt.executeUpdate();

        }
    }
}