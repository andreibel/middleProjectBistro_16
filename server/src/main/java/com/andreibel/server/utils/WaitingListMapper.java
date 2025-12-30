package com.andreibel.server.utils;

import com.andreibel.message.DTO.WaitingListRequest;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.server.entity.Waiting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Mapper for Waiting List entity conversions.
 * 
 * Handles conversions between:
 * - ResultSet → Waiting entity (database to object)
 * - Waiting entity → WaitingListResponse DTO (object to API response)
 * - WaitingListRequest DTO → Waiting entity (API request to object)
 */
public class WaitingListMapper {

    /**
     * Maps a database ResultSet row to a Waiting entity.
     * 
     * Converts all columns from the Waiting table row to the Waiting object.
     * Handles nullable fields appropriately.
     * 
     * @param rs the ResultSet row from database query
     * @return Waiting entity object with all fields populated
     * @throws SQLException if column access fails
     */
    public static Waiting mapRelToWaiting(ResultSet rs) throws SQLException {
        return Waiting.builder()
                .waitingNumber(rs.getInt(Waiting.WAITING_NUMBER))
                .numberOfGuests(rs.getInt(Waiting.NUMBER_OF_GUESTS))
                .waitingDateTime(rs.getTimestamp(Waiting.WAITING_DATE_TIME).toLocalDateTime())
                .isCurrentlyWaiting(rs.getBoolean(Waiting.IS_CURRENTLY_WAITING))
                .conformationCode(UUID.fromString(rs.getString(Waiting.CONFIRMATION_CODE)))
                .waitingArriveDateTime(
                    rs.getTimestamp(Waiting.WAITING_ARRIVE_DATE_TIME) != null 
                        ? rs.getTimestamp(Waiting.WAITING_ARRIVE_DATE_TIME).toLocalDateTime() 
                        : null
                )
                .orderNumber(rs.getObject(Waiting.ORDER_NUMBER) != null ? rs.getInt(Waiting.ORDER_NUMBER) : null)
                .subscriberId(rs.getObject(Waiting.SUBSCRIBER_ID) != null ? rs.getInt(Waiting.SUBSCRIBER_ID) : null)
                .email(rs.getString(Waiting.EMAIL))
                .phoneNumber(rs.getString(Waiting.PHONE_NUMBER))
                .build();
    }

    /**
     * Maps a Waiting entity to a WaitingListResponse DTO.
     * 
     * Converts the internal entity to the API response format.
     * All fields are included for client representation.
     * 
     * @param waiting the Waiting entity to convert
     * @return WaitingListResponse DTO ready for API response
     */
    public static WaitingListResponse mapWaitingToWaitingResponse(Waiting waiting) {
        return WaitingListResponse.builder()
                .waitingNumber(waiting.getWaitingNumber())
                .numberOfGuests(waiting.getNumberOfGuests())
                .waitingDateTime(waiting.getWaitingDateTime())
                .isCurrentlyWaiting(waiting.isCurrentlyWaiting())
                .conformationCode(waiting.getConformationCode())
                .waitingArriveDateTime(waiting.getWaitingArriveDateTime())
                .orderNumber(waiting.getOrderNumber())
                .subscriberId(waiting.getSubscriberId())
                .email(waiting.getEmail())
                .phoneNumber(waiting.getPhoneNumber())
                .build();
    }

    /**
     * Maps a WaitingListRequest DTO to a Waiting entity.
     * 
     * Converts incoming API request to internal entity format.
     * Auto-generates confirmation code and initializes waiting status.
     * Note: waitingDateTime should be set by the service layer to current time.
     * 
     * @param request the WaitingListRequest DTO from API
     * @return Waiting entity ready for persistence
     */
    public static Waiting mapWaitingRequestToWaiting(WaitingListRequest request) {
        return Waiting.builder()
                .numberOfGuests(request.getNumberOfGuests())
                .orderNumber(request.getOrderNumber())
                .conformationCode(UUID.randomUUID())
                .subscriberId(request.getSubscriberId())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                // isCurrentlyWaiting defaults to true in entity
                // waitingDateTime is set by service layer
                .build();
    }
}