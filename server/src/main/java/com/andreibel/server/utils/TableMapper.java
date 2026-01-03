package com.andreibel.server.utils;

import com.andreibel.message.DTO.TableResponse;
import com.andreibel.server.entity.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TableMapper {

    public static Table mapRelToTable(ResultSet rs) throws SQLException {
        return Table.builder()
                .capacity(rs.getInt(Table.CAPACITY))
                .quantity(rs.getInt(Table.QUANTITY))
                .build();
    }
    public static TableResponse mapTableToResonance(Table table) {
        return TableResponse.builder()
                .capacity(table.getCapacity())
                .quantity(table.getQuantity())
                .build();
    }
}
