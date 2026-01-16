package com.andreibel.server.utils;

import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.server.entity.Subscriber;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility mapper for converting between {@link Subscriber} entity,
 * database result sets, and subscriber-related DTOs.
 * <p>
 * Contains only static mapping methods and holds no state.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class SubscriberMapper {

    /**
     * Maps a database {@link ResultSet} row to a {@link Subscriber} entity.
     *
     * @param rs result set positioned at the current row
     * @return mapped Subscriber entity
     * @throws SQLException if a column access error occurs
     */
    public static Subscriber mapRelToSubscriber(ResultSet rs) throws SQLException {
        return Subscriber.builder()
                .subscriberId(rs.getInt(Subscriber.SUBSCRIBER_ID))
                .email(rs.getString(Subscriber.EMAIL))
                .name(rs.getString(Subscriber.NAME))
                .phoneNumber(rs.getString(Subscriber.PHONE_NUMBER))
                .build();
    }

    /**
     * Maps a {@link Subscriber} entity to a {@link SubscriberResponse} DTO.
     *
     * @param subscriber the subscriber entity to convert
     * @return subscriber response DTO for client communication
     */
    public static SubscriberResponse mapSubscriberToSubscriberResponse(Subscriber subscriber) {
        return SubscriberResponse.builder()
                .subscriberId(subscriber.getSubscriberId())
                .email(subscriber.getEmail())
                .name(subscriber.getName())
                .phoneNumber(subscriber.getPhoneNumber())
                .build();
    }


}
