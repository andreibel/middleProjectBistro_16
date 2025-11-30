package com.andreibel.server.utils;

import com.andreibel.server.entity.Order;

import java.sql.ResultSet;
import java.sql.SQLException;

public class mapper {


    public static Order mapRelToOrder(ResultSet rs) throws SQLException {
        return Order.builder()
                .orderNumber(rs.getInt("order_number"))
                .numberOfGuests(rs.getInt("number_of_guests"))
                .conformationCode(rs.getInt("conformation_code"))
                .subscriberId(rs.getInt("subscriber_id"))
                .orderDateTime(rs.getTimestamp("order_date").toLocalDateTime())
                .placedOrderDateTime(rs.getTimestamp("date_of_placing_order").toLocalDateTime())
                .build();
    }
}
