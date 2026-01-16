package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for order response operations.
 * <p>
 * Used for returning order information from the server to the client.
 * Contains complete order details including timing and customer information.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see OrderRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse implements Serializable {

    /**
     * Number of guests for the reservation.
     */
    private Integer numberOfGuests;

    /**
     * Unique confirmation code for the order.
     * Used for order identification and verification.
     */
    private UUID conformationCode;

    /**
     * Subscriber ID if the order was placed by a registered subscriber.
     * Optional - may be null for guest orders.
     */
    private Integer subscriberId;

    /**
     * Email address associated with the order.
     * Optional - may be null.
     */
    private String email;

    /**
     * Phone number associated with the order.
     * Optional - may be null.
     */
    private String phoneNumber;

    /**
     * The reserved date and time for the order.
     */
    private LocalDateTime orderDateTime;

    /**
     * The timestamp when the order was originally placed.
     */
    private LocalDateTime placedOrderDateTime;
}

