package com.andreibel.server.utils;

import com.andreibel.server.entity.OpenTime;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OpenTimeMapper {
    public static OpenTime mapRelToOpenTime(ResultSet rs) throws SQLException {
        return OpenTime.builder()
                .id(rs.getInt(OpenTime.ID))
                .SpatialDate(rs.getDate(OpenTime.SPATIAL_DATE))
                .title(rs.getString(OpenTime.TITLE))
                .openTime(rs.getTime(OpenTime.OPEN_TIME))
                .closeTime(rs.getTime(OpenTime.CLOSE_TIME))
                .interval(rs.getInt(OpenTime.INTERVAL))
                .build();
    }
}
