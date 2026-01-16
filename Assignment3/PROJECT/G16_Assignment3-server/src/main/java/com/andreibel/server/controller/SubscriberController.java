package com.andreibel.server.controller;

import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import com.andreibel.server.services.OrderService;
import com.andreibel.server.services.SubscriberService;

import java.util.List;

import static com.andreibel.message.APICallType.*;

/**
 * Handles subscriber-related API calls coming from the network layer.
 *
 * <p>This controller is a thin routing/adaptation layer between incoming {@link Message} objects
 * and the {@link SubscriberService} (and related services). Each method is responsible for:</p>
 * <ul>
 *   <li>extracting and casting the expected payload from {@link Message#getData()}</li>
 *   <li>delegating the operation to the service layer</li>
 *   <li>wrapping the result into a response {@link Message} with the correct API type</li>
 * </ul>
 *
 * <p><b>Payload contract:</b> In this controller, the payload is usually an {@link Integer} subscriber id
 * (for read operations) or a {@link SubscriberRequest} (for create/update). If the payload type does not match,
 * a {@link ClassCastException} may occur.</p>
 *
 * <p><b>Singleton:</b> implemented as a singleton to keep one controller instance during server runtime.</p>
 *
 * @author Andrei Beloziyorove
 */
public class SubscriberController {

    private static SubscriberController instance;

    /** Business service for subscriber operations (CRUD, login, etc.). */
    private final SubscriberService subscriberService;

    /**
     * Order service dependency (currently unused inside this controller).
     * Keep it only if you plan to use it in future subscriber flows; otherwise remove to avoid dead code.
     */
    private final OrderService orderService;

    /**
     * Creates the controller and initializes service dependencies.
     * Private to enforce the singleton pattern.
     */
    private SubscriberController() {
        subscriberService = SubscriberService.getInstance();
        orderService = OrderService.getInstance();
    }

    /**
     * Returns the singleton instance of {@link SubscriberController}.
     *
     * @return singleton controller instance
     */
    public static SubscriberController getInstance() {
        if (instance == null) {
            instance = new SubscriberController();
        }
        return instance;
    }

    /**
     * Retrieves all subscribers from the system.
     *
     * <p><b>Response:</b> {@code GET_ALL_SUBSCRIBERS_RESPONSE} with {@code List<SubscriberResponse>} on success,
     * or {@code GET_ALL_SUBSCRIBERS_ERROR} if the list is {@code null} or empty.</p>
     *
     * @return response message containing all subscribers or an error type
     */
    public Message getAllSub() {
        List<SubscriberResponse> subscribers = subscriberService.getAllSubscribers();
        if (subscribers == null || subscribers.isEmpty()) {
            return new Message(GET_ALL_SUBSCRIBERS_ERROR, null);
        }
        return new Message(GET_ALL_SUBSCRIBERS_RESPONSE, subscribers);
    }

    /**
     * Retrieves a single subscriber by id (used for subscriber login / fetch by id).
     *
     * <p><b>Expected payload:</b> {@link Integer} subscriber id in {@code message.getData()}.</p>
     *
     * <p><b>Response:</b> {@code SUBSCRIBER_LOGIN_RESPONSE} with {@link SubscriberResponse} on success,
     * or {@code SUBSCRIBER_LOGIN_ERROR} if not found.</p>
     *
     * @param message request message containing an {@link Integer} subscriber id
     * @return response message containing the subscriber or an error type
     */
    public Message getSub(Message message) {
        Integer subscriberId = (Integer) message.getData();
        SubscriberResponse subscriber = subscriberService.getSubscriber(subscriberId);
        if (subscriber == null) return new Message(SUBSCRIBER_LOGIN_ERROR, null);
        return new Message(SUBSCRIBER_LOGIN_RESPONSE, subscriber);
    }

    /**
     * Retrieves a subscriber and their orders by subscriber id.
     *
     * <p><b>Expected payload:</b> {@link Integer} subscriber id in {@code message.getData()}.</p>
     *
     * <p><b>Response:</b> {@code GET_SUBSCRIBER_ORDERS_RESPONSE} with {@link SubscriberResponse} (including orders)
     * on success, or {@code GET_SUBSCRIBER_ORDERS_ERROR} if the request is invalid or data is missing.</p>
     *
     * <p><b>Note:</b> your current implementation checks {@code message.getData() == null} after calling the service.
     * Typically you want to check {@code subscriber == null} instead.</p>
     *
     * @param message request message containing an {@link Integer} subscriber id
     * @return response message containing the subscriber + orders or an error type
     */
    public Message getSubOrders(Message message) {
        Integer subscriberId = (Integer) message.getData();
        SubscriberResponse subscriber = subscriberService.getSubscriberAndOrders(subscriberId);

        // Recommended check:
        if (subscriberId == null || subscriber == null) {
            return new Message(GET_SUBSCRIBER_ORDERS_ERROR, null);
        }
        return new Message(GET_SUBSCRIBER_ORDERS_RESPONSE, subscriber);
    }

    /**
     * Creates a new subscriber.
     *
     * <p><b>Expected payload:</b> {@link SubscriberRequest} in {@code message.getData()}.</p>
     *
     * <p><b>Response:</b> {@code CREATE_SUBSCRIBER_RESPONSE} with {@link SubscriberResponse} on success,
     * or {@code CREATE_SUBSCRIBER_ERROR} if creation failed.</p>
     *
     * @param message request message containing {@link SubscriberRequest}
     * @return response message containing the created subscriber or an error type
     */
    public Message createSub(Message message) {
        SubscriberRequest subscriber = (SubscriberRequest) message.getData();
        SubscriberResponse created = subscriberService.createSubscriber(subscriber);
        if (created == null) return new Message(CREATE_SUBSCRIBER_ERROR, null);
        return new Message(CREATE_SUBSCRIBER_RESPONSE, created);
    }

    /**
     * Updates an existing subscriber.
     *
     * <p><b>Expected payload:</b> {@link SubscriberRequest} containing the subscriber id and updated fields.</p>
     *
     * <p><b>Response:</b> {@code UPDATE_SUBSCRIBER_RESPONSE} with {@link SubscriberResponse} on success,
     * or {@code UPDATE_SUBSCRIBER_ERROR} if update failed.</p>
     *
     * @param message request message containing {@link SubscriberRequest}
     * @return response message containing the updated subscriber or an error type
     */
    public Message updateSub(Message message) {
        SubscriberRequest data = (SubscriberRequest) message.getData();
        SubscriberResponse updated = subscriberService.updateSub(data);
        if (updated == null) return new Message(UPDATE_SUBSCRIBER_ERROR, null);
        return new Message(UPDATE_SUBSCRIBER_RESPONSE, updated);
    }
}