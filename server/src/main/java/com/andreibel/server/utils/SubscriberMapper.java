package com.andreibel.server.utils;

import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.server.entity.Subscriber;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SubscriberMapper {
    public static Subscriber mapRelToSubscriber(ResultSet rs) throws SQLException {
        return Subscriber.builder()
                .subscriberId(rs.getInt(Subscriber.SUBSCRIBER_ID))
                .email(rs.getString(Subscriber.EMAIL))
                .name(rs.getString(Subscriber.NAME))
                .phoneNumber(rs.getString(Subscriber.PHONE_NUMBER))
                .build();
    }

    public static SubscriberResponse mapSubscriberToSubscriberResponse(Subscriber subscriber) {
        return SubscriberResponse.builder()
                .subscriberId(subscriber.getSubscriberId())
                .email(subscriber.getEmail())
                .name(subscriber.getName())
                .phoneNumber(subscriber.getPhoneNumber())
                .build();
    }


}
