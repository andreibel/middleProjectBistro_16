package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Data Transfer Object for waiting list request operations.
 * <p>
 * Used to add customers to the restaurant's waiting list when no tables
 * are immediately available. Supports both guest and subscriber customers.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see WaitingListResponse
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WaitingListRequest implements Serializable {

    /**
     * Unique confirmation code for the waiting list entry.
     * Generated upon successful addition to the waiting list.
     */
    private UUID ConformationCode;

    /**
     * Number of guests in the party waiting for a table.
     */
    private Integer numberOfGuests;

    /**
     * Subscriber ID if the waiting customer is a registered subscriber.
     * Optional - may be null for guest customers.
     */
    private Integer subscriberId;

    /**
     * Email address for notifications when table becomes available.
     * Optional - may be null.
     */
    private String email;

    /**
     * Phone number for notifications when table becomes available.
     * Optional - may be null.
     */
    private String phoneNumber;
}
