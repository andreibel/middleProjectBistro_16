package com.andreibel.server.utils;

import com.andreibel.server.entity.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TableMapper {

    public static Table mapRelToTable(ResultSet rs) throws SQLException {
        return Table.builder().tableId(rs.getInt(Table.TABLE_ID))
                .capacity(rs.getInt(Table.CAPACITY))
                .quantity(rs.getInt(Table.QUANTITY))
                .build();
    }
}
