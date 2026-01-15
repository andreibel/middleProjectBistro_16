package com.andreibel.server.utils;

import com.andreibel.message.DTO.BistroTimeDTO;
import com.andreibel.server.entity.OpenTime;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility mapper for converting between {@link OpenTime} entity,
 * database result sets, and opening time DTOs.
 * <p>
 * Contains only static mapping methods and holds no state.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class OpenTimeMapper {

    /**
     * Maps a database {@link ResultSet} row to an {@link OpenTime} entity.
     *
     * @param rs result set positioned at the current row
     * @return mapped OpenTime entity
     * @throws SQLException if a column access error occurs
     */
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

    /**
     * Maps an {@link OpenTime} entity to a {@link BistroTimeDTO}.
     *
     * @param regular the OpenTime entity to convert
     * @return BistroTimeDTO containing open time, close time, and interval
     */
    public static BistroTimeDTO mapOpenTimeToDTO(OpenTime regular) {
        return new BistroTimeDTO(
                regular.getOpenTime().toLocalTime(),
                regular.getCloseTime().toLocalTime(),
                regular.getInterval()
        );
    }
}
