package com.andreibel.server.utils;

import com.andreibel.message.DTO.WaitingListRequest;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.server.entity.Waiting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WaitingListMapper {
    public static Waiting mapRelToWaiting(ResultSet rs) throws SQLException {
        return Waiting.builder()
                .waitingNumber(rs.getInt(Waiting.WAITING_NUMBER))
                .waitingDateTime(rs.getTimestamp(Waiting.WAITING_DATE_TIME).toLocalDateTime())
                .isCurrentlyWaiting(rs.getBoolean(Waiting.IS_CURRENTLY_WAITING))
                .conformationCode(UUID.fromString(rs.getString(Waiting.CONFIRMATION_CODE)))
                .orderNumber(rs.getObject(Waiting.ORDER_NUMBER) != null ? rs.getInt(Waiting.ORDER_NUMBER) : null)
                .subscriberId(rs.getObject(Waiting.SUBSCRIBER_ID) != null ? rs.getInt(Waiting.SUBSCRIBER_ID) : null)
                .email(rs.getString(Waiting.EMAIL))
                .phoneNumber(rs.getString(Waiting.PHONE_NUMBER))
                .build();
    }

    public static WaitingListResponse mapWaitingToWaitingResponse(Waiting waiting) {
        return WaitingListResponse.builder()
                .waitingNumber(waiting.getWaitingNumber())
                .numberOfGuests(waiting.getNumberOfGuests())
                .isCurrentlyWaiting(waiting.isCurrentlyWaiting())
                .waitingDateTime(waiting.getWaitingDateTime())
                .conformationCode(waiting.getConformationCode())
                .orderNumber(waiting.getOrderNumber())
                .subscriberId(waiting.getSubscriberId())
                .email(waiting.getEmail())
                .phoneNumber(waiting.getPhoneNumber())
                .build();
    }

    public static Waiting mapWaitingRequestToWaiting(WaitingListRequest request) {
        return Waiting.builder()
                .numberOfGuests(request.getNumberOfGuests())
                .orderNumber(request.getOrderNumber())
                .conformationCode(UUID.randomUUID())
                .subscriberId(request.getSubscriberId())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }
}
