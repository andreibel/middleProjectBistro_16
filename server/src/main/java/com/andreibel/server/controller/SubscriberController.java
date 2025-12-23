package com.andreibel.server.controller;

import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.Message;
import com.andreibel.server.services.SubscriberService;

import static com.andreibel.message.APICallType.*;

/**
 * Controller responsible for handling subscriber-related requests.
 *
 * <p>
 * This controller receives {@link Message} objects, extracts
 * {@link SubscriberRequest} data, delegates processing to
 * {@link SubscriberService}, and wraps the results into response messages.
 * </p>
 *
 * <p>
 * Implemented as a Singleton to ensure a single controller instance
 * throughout the application lifecycle.
 * </p>
 * @author Andrei Beloziyorove
 */
public class SubscriberController {

    private static SubscriberController instance;
    private final SubscriberService subscriberService;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private SubscriberController() {
        subscriberService = SubscriberService.getInstance();
    }

    /**
     * Returns the singleton instance of {@code SubscriberController}.
     *
     * @return singleton instance of SubscriberController
     */
    public static SubscriberController getInstance() {
        if (instance == null) {
            instance = new SubscriberController();
        }
        return instance;
    }

    /**
     * Retrieves all subscribers.
     *
     * @return response message containing all subscribers
     */
    public Message getAllSub() {
        return new Message(
                GET_ALL_SUBSCRIBERS_RESPONSE,
                subscriberService.getAllSubscribers()
        );
    }

    /**
     * Retrieves a single subscriber.
     *
     * @param message request message containing {@link SubscriberRequest}
     * @return response message containing the requested subscriber
     */
    public Message getSub(Message message) {
        return new Message(
                GET_ONE_SUBSCRIBER_RESPONSE,
                subscriberService.getSubscriber(
                        (SubscriberRequest) message.getData()
                )
        );
    }

    /**
     * Retrieves a subscriber along with all associated orders.
     *
     * @param message request message containing {@link SubscriberRequest}
     * @return response message containing subscriber details and orders
     */
    public Message getSubOrders(Message message) {
        return new Message(
                GET_SUBSCRIBER_ORDERS_RESPONSE,
                subscriberService.getSubscriberAndOrders(
                        (SubscriberRequest) message.getData()
                )
        );
    }

    /**
     * Creates a new subscriber.
     *
     * @param message request message containing {@link SubscriberRequest}
     * @return response message containing the created subscriber
     */
    public Message createSub(Message message) {
        return new Message(
                CREATE_SUBSCRIBER_RESPONSE,
                subscriberService.createSubscriber(
                        (SubscriberRequest) message.getData()
                )
        );
    }
}