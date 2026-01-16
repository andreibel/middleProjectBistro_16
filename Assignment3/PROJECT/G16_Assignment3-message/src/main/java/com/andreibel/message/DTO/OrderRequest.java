package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for order request operations.
 * <p>
 * Used for various order-related API calls in the Bistro system.
 * The required fields depend on the specific operation:
 * </p>
 *
 * <h3>CREATE_ORDER:</h3>
 * <ul>
 *   <li>{@code numberOfGuests} - Required</li>
 *   <li>{@code orderDateTime} - Required</li>
 *   <li>{@code subscriberId} - Optional</li>
 *   <li>{@code email} or {@code phoneNumber} - Optional</li>
 * </ul>
 *
 * <h3>ORDER_ARRIVED:</h3>
 * <ul>
 *   <li>{@code ConformationCode} - Required (or subscriberId)</li>
 *   <li>{@code subscriberId} - Alternative to ConformationCode</li>
 * </ul>
 *
 * <h3>GET_ALL_TIMES_IN_DATE:</h3>
 * <ul>
 *   <li>{@code orderDateTime} - Required</li>
 *   <li>{@code numberOfGuests} - Required</li>
 * </ul>
 *
 * @author Bistro Team
 * @version 1.0
 * @see OrderResponse
 */
@Data
@ToString()
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest implements Serializable {

    /**
     * Unique confirmation code for the order.
     * Used to identify and verify orders upon customer arrival.
     */
    private UUID ConformationCode;

    /**
     * Number of guests for the reservation.
     */
    private Integer numberOfGuests;

    /**
     * Date and time of the reservation.
     */
    private LocalDateTime orderDateTime;

    /**
     * Subscriber ID if the order is placed by a registered subscriber.
     * Optional field.
     */
    private Integer subscriberId;

    /**
     * Email address for order confirmation and notifications.
     * Optional field.
     */
    private String email;

    /**
     * Phone number for order confirmation and notifications.
     * Optional field.
     */
    private String phoneNumber;

}
