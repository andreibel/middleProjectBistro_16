package com.andreibel.server.utils;

import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.server.entity.Order;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.server.entity.Subscriber;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Mapper {


    public static Order mapRelToOrder(ResultSet rs) throws SQLException {
        return Order.builder()
                .orderNumber(rs.getInt(Order.ORDER_NUMBER))
                .numberOfGuests(rs.getInt(Order.NUMBER_OF_GUESTS))
                .conformationCode(UUID.fromString(rs.getString(Order.CONFIRMATION_CODE)))
                .subscriberId(rs.getInt(Order.SUBSCRIBER_ID))
                .orderDateTime(rs.getTimestamp(Order.ORDER_DATE_TIME).toLocalDateTime())
                .placedOrderDateTime(rs.getTimestamp(Order.PLACED_ORDER_DATE_TIME).toLocalDateTime())
                .build();
    }
    public static OrderResponse mapOrderToOrderResponse(Order order) {
        return new OrderResponse(order.getOrderNumber(),
                order.getNumberOfGuests(),
                order.getConformationCode(),
                order.getSubscriberId(),
                order.getOrderDateTime(),
                order.getPlacedOrderDateTime());
    }



    public static Subscriber mapRelToSubscriber(ResultSet rs) throws SQLException {
        return Subscriber.builder()
                .subscriberId(rs.getInt(Subscriber.SUBSCRIBER_ID))
                .email(rs.getString(Subscriber.EMAIL))
                .name(rs.getString(Subscriber.NAME))
                .phoneNumber(rs.getString(Subscriber.PHONE_NUMBER))
                .build();
    }
    public static SubscriberResponse mapSubscriberToSubscriberResponse(Subscriber subscriber) {
        return new SubscriberResponse(subscriber.getSubscriberId(), subscriber.getEmail(), subscriber.getName(), subscriber.getPhoneNumber());
    }
}
