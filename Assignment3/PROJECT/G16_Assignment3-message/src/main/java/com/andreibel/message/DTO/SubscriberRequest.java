package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for subscriber request operations.
 * <p>
 * Used for creating and updating subscriber information in the Bistro system.
 * This DTO transfers subscriber data from the client to the server.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see SubscriberResponse
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class SubscriberRequest implements Serializable {

    /**
     * Unique identifier for the subscriber.
     * May be null for new subscriber creation requests.
     */
    private Integer subscriberId;

    /**
     * Email address of the subscriber.
     * Used for notifications and communication.
     */
    private String email;

    /**
     * Full name of the subscriber.
     */
    private String name;

    /**
     * Phone number of the subscriber.
     * Used for contact and notifications.
     */
    private String phoneNumber;
}
