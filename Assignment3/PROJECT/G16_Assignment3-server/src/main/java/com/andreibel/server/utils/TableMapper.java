package com.andreibel.server.utils;

import com.andreibel.message.DTO.TableResponse;
import com.andreibel.server.entity.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class for mapping between Table entity, database ResultSet, and TableResponse DTO.
 * Provides static methods for converting table data across different layers of the application.
 */
public class TableMapper {

    /**
     * Maps a database ResultSet row to a Table entity.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return a Table entity populated with capacity and quantity from the ResultSet
     * @throws SQLException if a database access error occurs or column names are invalid
     */
    public static Table mapRelToTable(ResultSet rs) throws SQLException {
        return Table.builder()
                .capacity(rs.getInt(Table.CAPACITY))
                .quantity(rs.getInt(Table.QUANTITY))
                .build();
    }

    /**
     * Maps a Table entity to a TableResponse DTO for client communication.
     *
     * @param table the Table entity to convert
     * @return a TableResponse DTO containing the table's capacity and quantity
     */
    public static TableResponse mapTableToResonance(Table table) {
        return TableResponse.builder()
                .capacity(table.getCapacity())
                .quantity(table.getQuantity())
                .build();
    }
}
