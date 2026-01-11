package com.andreibel.client.util;

import com.andreibel.message.DTO.SubscriberResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Singleton class that manages the global subscriber state for the client application.
 *
 * <p>This class provides a central location to store and share subscriber-related
 * information across different GUI controllers (FXML forms) without passing data
 * explicitly between screens.</p>
 *
 * <p>The main responsibilities of this class include:</p>
 * <ul>
 *     <li>Storing the current subscriber information after login or identification</li>
 *     <li>Tracking whether the subscriber has arrived at a table</li>
 *     <li>Storing the confirmation code associated with the subscriber's active order</li>
 *     <li>Providing a single shared instance to all GUI controllers via the
 *     {@link #getInstance()} method</li>
 * </ul>
 *
 * <p>This class does not perform any server communication; it purely stores
 * client-side state.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * CustomerStateManager state = CustomerStateManager.getInstance();
 * if (state.getSubscriber() != null) {
 *     // subscriber is logged in
 *     String name = state.getSubscriber().getName();
 * }
 * </pre>
 */
@Getter
@Setter
public class CustomerStateManager {

    /**
     * The singleton instance of {@link CustomerStateManager}.
     */
    private static CustomerStateManager instance;

    /**
     * The subscriber information received from the server, or {@code null} if no subscriber is logged in.
     */
    private SubscriberResponse subscriber;

    /**
     * Indicates whether the subscriber has arrived at a table.
     */
    private boolean arrivedToTable = false;

    /**
     * The confirmation code associated with the subscriber's current order, or {@code null} if none.
     */
    private Integer confirmationCode = null;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private CustomerStateManager() {}

    /**
     * Returns the shared singleton instance of {@link CustomerStateManager}.
     *
     * <p>If the instance does not yet exist, it is created.</p>
     *
     * @return the singleton {@link CustomerStateManager} instance
     */
    public static CustomerStateManager getInstance() {
        if (instance == null) {
            instance = new CustomerStateManager();
        }
        return instance;
    }

    /**
     * Checks whether a subscriber has logged in (i.e., whether the singleton instance exists).
     *
     * @return {@code true} if a subscriber has logged in, {@code false} otherwise
     */
    public static boolean hasSubscriberLoggedIn() {
        return instance != null && instance.getSubscriber() != null;
    }
    /**
     * Retrieves the subscriber ID of the currently logged-in subscriber.
     *
     * <p>If there is no subscriber logged in, this method returns {@code null}.
     * This is useful for differentiating between guests and subscribers when making
     * requests that require a subscriber ID.</p>
     *
     * @return the subscriber ID if a subscriber is logged in; {@code null} otherwise
     */
    public static Integer fillSubscriberIDDetails() {
        return CustomerStateManager.getInstance().getSubscriber() != null
                ? CustomerStateManager.getInstance().getSubscriber().getSubscriberId()
                : null;
    }
}
