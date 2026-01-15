package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for waiting list response operations.
 * <p>
 * Used for returning waiting list entry information from the server to the client.
 * Contains complete waiting status and customer information.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see WaitingListRequest
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WaitingListResponse implements Serializable {

    /**
     * Queue position number in the waiting list.
     */
    private int waitingNumber;

    /**
     * Number of guests in the waiting party.
     */
    private Integer numberOfGuests;

    /**
     * Timestamp when the customer was added to the waiting list.
     */
    private LocalDateTime waitingDateTime;

    /**
     * Indicates whether the customer is currently waiting.
     * {@code true} if still waiting, {@code false} if seated or left.
     */
    private boolean isCurrentlyWaiting;

    /**
     * Unique confirmation code for the waiting list entry.
     */
    private UUID conformationCode;

    /**
     * Associated order number if the waiting customer was converted to an order.
     */
    private Integer orderNumber;

    /**
     * Subscriber ID if the waiting customer is a registered subscriber.
     * Optional - may be null for guest customers.
     */
    private Integer subscriberId;

    /**
     * Email address of the waiting customer.
     * Optional - may be null.
     */
    private String email;

    /**
     * Phone number of the waiting customer.
     * Optional - may be null.
     */
    private String phoneNumber;
}
