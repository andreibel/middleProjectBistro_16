package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object for subscriber response operations.
 * <p>
 * Used for returning subscriber information from the server to the client.
 * Contains complete subscriber profile data including associated orders.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see SubscriberRequest
 * @see OrderResponse
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubscriberResponse implements Serializable {

    /**
     * Unique identifier for the subscriber.
     */
    private int subscriberId;

    /**
     * Email address of the subscriber.
     */
    private String email;

    /**
     * Full name of the subscriber.
     */
    private String name;

    /**
     * Phone number of the subscriber.
     */
    private String phoneNumber;

    /**
     * List of orders associated with this subscriber.
     * May be null or empty if no orders exist.
     */
    private List<OrderResponse> orders;
}
