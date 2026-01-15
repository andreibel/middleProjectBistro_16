package com.andreibel.server.controller;

import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.WaitingListRequest;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import com.andreibel.server.services.WaitingListService;

import java.util.List;

import static com.andreibel.message.APICallType.*;

/**
 * Server-side controller responsible for handling waiting list–related
 * requests and reports.
 *
 * <p>This controller acts as an intermediary between incoming client messages
 * and the {@link WaitingListService}. It validates service responses and
 * converts them into appropriate {@link Message} objects containing
 * {@link APICallType} values.</p>
 *
 * <p>The controller follows the singleton pattern to ensure a single
 * shared instance throughout the server lifecycle.</p>
 */
public class WaitingController {

    /**
     * Singleton instance of the controller.
     */
    private static WaitingController instance;

    /**
     * Service responsible for waiting list and report business logic.
     */
    private final WaitingListService waitingListService;

    /**
     * Private constructor to enforce singleton usage.
     */
    private WaitingController() {
        this.waitingListService = WaitingListService.getInstance();
    }

    /**
     * Returns the singleton instance of {@code WaitingController}.
     *
     * @return the shared {@code WaitingController} instance
     */
    public static WaitingController getInstance() {
        if (instance == null) {
            instance = new WaitingController();
        }
        return instance;
    }

    /**
     * Handles a request to retrieve the current waiting list.
     *
     * <p>The returned list contains all guests currently waiting
     * to be seated.</p>
     *
     * @return a {@link Message} containing either
     *         {@link APICallType#GET_WAITING_LIST_RESPONSE} with the waiting list
     *         or {@link APICallType#GET_WAITING_LIST_ERROR} if retrieval fails
     */
    public Message getWaitingList() {
        List<WaitingListResponse> result = waitingListService.getCurrentWaitingActive();
        if (result == null) return new Message(GET_WAITING_LIST_ERROR, null);
        return new Message(GET_WAITING_LIST_RESPONSE, result);
    }

    /**
     * Handles a request to add a new entry to the waiting list.
     *
     * <p>The request data must contain a {@link WaitingListRequest} object
     * describing the guest details.</p>
     *
     * @param message the incoming request message containing
     *                {@link WaitingListRequest} data
     * @return a {@link Message} containing either
     *         {@link APICallType#ADD_TO_WAITING_LIST_RESPONSE} with the created entry
     *         or {@link APICallType#ADD_TO_WAITING_LIST_ERROR} if the operation fails
     */
    public Message addWaitingList(Message message) {
        WaitingListResponse result = waitingListService.addNewWaiting((WaitingListRequest)message.getData());
        if (result == null) return new Message(ADD_TO_WAITING_LIST_ERROR, null);
        return new Message(ADD_TO_WAITING_LIST_RESPONSE, result);
    }
}
