package com.andreibel.server.dbController.repository;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.andreibel.server.utils.Mapper.mapRelToTable;

public class TableRepository {

    private static TableRepository instance;
    private final TransactionManager tx;

    private TableRepository() {
        this.tx = TransactionManager.getInstance();
    }

    public static TableRepository getInstance() {
        if (instance == null) instance = new TableRepository();
        return instance;
    }

    public List<Table> findAll() throws SQLException {
        String sql = "SELECT * FROM bistro.`Table`;";
        List<Table> tables = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tables.add(mapRelToTable(rs));
            }
        }
        return tables;
    }
}
