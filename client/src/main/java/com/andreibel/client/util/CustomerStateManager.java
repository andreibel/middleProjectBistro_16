package com.andreibel.client.util;

import com.andreibel.message.DTO.SubscriberResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * A global application state manager used to store and share
 * user-related state across different GUI controllers.
 *
 * <p>This class follows the Singleton pattern and provides a single
 * shared instance throughout the client application.</p>
 *
 * <p>Its main responsibility is to keep track of the current subscriber
 * state after login or identification, allowing different screens
 * (FXML controllers) to access subscriber information without
 * passing it explicitly between forms.</p>
 *
 * <p>Typical usage examples:</p>
 * <ul>
 *     <li>Checking whether the current user is a subscriber</li>
 *     <li>Accessing subscriber details (ID, name, status, etc.)</li>
 *     <li>Conditionally enabling or disabling GUI features</li>
 * </ul>
 *
 * <p>This class does not perform any server communication and is purely
 * client-side state storage.</p>
 */
@Getter
@Setter
public class CustomerStateManager {

    /**
     * The single instance of the StateManager.
     */
    private static CustomerStateManager customerStateManager;

    /**
     * Holds the subscriber information received from the server,
     * or {@code null} if the user is not a subscriber.
     */
    private SubscriberResponse subscriber;

    /**
     * Private constructor to prevent external instantiation.
     */
    private CustomerStateManager() {}

    /**
     * Returns the singleton instance of the StateManager.
     *
     * @return the shared StateManager instance
     */
    public static CustomerStateManager getInstance() {
        if (customerStateManager == null) {
            customerStateManager = new CustomerStateManager();
        }
        return customerStateManager;
    }
}
