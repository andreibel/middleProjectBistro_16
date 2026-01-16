package com.andreibel.server.utils;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.server.entity.Order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <h1>Order mapper class.</h1>
 * <hr/>
 * this class is used to map between the Order entity and the OrderRequest and OrderResponse DTOs.
 * also make result sets from the database to Order entities.
 */
public class OrderMapper {
    /**
     * maps an order entity to an order response DTO.
     * @param order {@link Order} entity to be mapped.
     * @return {@link OrderResponse} DTO.
     */
    public static OrderResponse mapOrderToOrderResponse(Order order) {
        return new OrderResponse(
                order.getNumberOfGuests(),
                order.getConformationCode(),
                order.getSubscriberId(),
                order.getEmail(),
                order.getPhoneNumber(),
                order.getOrderDateTime(),
                order.getPlacedOrderDateTime()
        );
    }

    /**
     * maps an order request DTO to an order entity.
     * @param order {@link OrderRequest} DTO to be mapped.
     * @return {@link Order} entity.
     */
    public static Order mapNewOrderRequestToOrder(OrderRequest order) {
        return Order.builder()
                .orderNumber(-1)
                .conformationCode(UUID.randomUUID())
                .numberOfGuests(order.getNumberOfGuests())
                .orderDateTime(order.getOrderDateTime())
                .placedOrderDateTime(LocalDateTime.now())
                .subscriberId(order.getSubscriberId())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .orderCompleted(false)
                .orderCancelled(false)
                .orderArrive(false)
                .build();
    }

    /**
     * maps a result set to an order entity.
     * @param rs {@link ResultSet} to be mapped.
     * @return {@link Order} entity.
     * @throws SQLException if the result set is invalid.
     */
    public static Order mapRelToOrder(ResultSet rs) throws SQLException {
        LocalDateTime arriveDateTime = rs.getTimestamp(Order.ORDER_ARRIVE_DATE_TIME) == null ? null
                : rs.getTimestamp(Order.ORDER_ARRIVE_DATE_TIME).toLocalDateTime();

        return Order.builder()
                .orderNumber(rs.getInt(Order.ORDER_NUMBER))
                .numberOfGuests(rs.getInt(Order.NUMBER_OF_GUESTS))
                .conformationCode(UUID.fromString(rs.getString(Order.CONFIRMATION_CODE)))
                .orderDateTime(rs.getTimestamp(Order.ORDER_DATE_TIME).toLocalDateTime())
                .placedOrderDateTime(rs.getTimestamp(Order.PLACED_ORDER_DATE_TIME).toLocalDateTime())
                .orderArriveDateTime(arriveDateTime)
                .orderCancelled(rs.getBoolean(Order.ORDER_CANCELLED))
                .orderArrive(rs.getBoolean(Order.ORDER_ARRIVED))
                .orderCompleted(rs.getBoolean(Order.ORDER_COMPLETED))
                .subscriberId(rs.getInt(Order.SUBSCRIBER_ID))
                .email(rs.getString(Order.EMAIL))
                .phoneNumber(rs.getString(Order.PHONE_NUMBER))
                .build();
    }
}

