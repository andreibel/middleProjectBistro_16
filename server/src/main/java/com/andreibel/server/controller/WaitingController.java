package com.andreibel.server.controller;

import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.SchedulesReportResponse;
import com.andreibel.message.DTO.SubscriberReportResponse;
import com.andreibel.message.DTO.WaitingListRequest;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import com.andreibel.server.services.WaitingListService;

import java.util.List;
import java.util.UUID;

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
     * Handles a request for the schedules report.
     *
     * <p>This method retrieves aggregated scheduling data from the service
     * layer and returns it to the client.</p>
     *
     * @param message the incoming request message (data not used)
     * @return a {@link Message} containing either
     *         {@link APICallType#SCHEDULES_REPORT_RESPONSE} with report data
     *         or {@link APICallType#SCHEDULES_REPORT_ERROR} if retrieval fails
     */
    public Message scheduleReport(Message message) {
        SchedulesReportResponse schedulesReportResponse =
                waitingListService.getSchedulesReport();

        if (schedulesReportResponse == null) {
            return new Message(APICallType.SCHEDULES_REPORT_ERROR, null);
        }

        return new Message(
                APICallType.SCHEDULES_REPORT_RESPONSE,
                schedulesReportResponse
        );
    }

    /**
     * Handles a request for the subscriber report.
     *
     * <p>This report provides aggregated data about restaurant subscribers.</p>
     *
     * @param message the incoming request message (data not used)
     * @return a {@link Message} containing either
     *         {@link APICallType#SUBSCRIBER_REPORT_RESPONSE} with report data
     *         or {@link APICallType#SUBSCRIBER_REPORT_ERROR} if retrieval fails
     */
    public Message subscriberReport(Message message) {
        SubscriberReportResponse subscriberReportResponse =
                waitingListService.getSubscriberReport();

        if (subscriberReportResponse == null) {
            return new Message(APICallType.SUBSCRIBER_REPORT_ERROR, null);
        }

        return new Message(
                APICallType.SUBSCRIBER_REPORT_RESPONSE,
                subscriberReportResponse
        );
    }

    /**
     * Handles a request to retrieve the current waiting list.
     *
     * <p>The returned list contains all guests currently waiting
     * to be seated.</p>
     *
     * @param message the incoming request message (data not used)
     * @return a {@link Message} containing either
     *         {@link APICallType#GET_WAITING_LIST_RESPONSE} with the waiting list
     *         or {@link APICallType#GET_WAITING_LIST_ERROR} if retrieval fails
     */
    public Message getWaitingList(Message message) {
        List<WaitingListResponse> waitingListResponseList =
                waitingListService.getWaitingList();

        if (waitingListResponseList == null) {
            return new Message(APICallType.GET_WAITING_LIST_ERROR, null);
        }

        return new Message(
                APICallType.GET_WAITING_LIST_RESPONSE,
                waitingListResponseList
        );
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
        WaitingListResponse waitingListResponse =
                waitingListService.addNewWaiting(
                        (WaitingListRequest) message.getData()
                );

        if (waitingListResponse == null) {
            return new Message(APICallType.ADD_TO_WAITING_LIST_ERROR, null);
        }

        return new Message(
                APICallType.ADD_TO_WAITING_LIST_RESPONSE,
                waitingListResponse
        );
    }

    /**
     * Handles a request to remove an entry from the waiting list.
     *
     * <p>The request data must contain the {@link UUID} of the waiting
     * list entry to be removed.</p>
     *
     * @param message the incoming request message containing the entry UUID
     * @return a {@link Message} containing either
     *         {@link APICallType#REMOVE_FROM_WAITING_LIST_RESPONSE} on success
     *         or {@link APICallType#REMOVE_FROM_WAITING_LIST_ERROR} on failure
     */
    public Message removeFromWaitingList(Message message) {
        boolean success =
                waitingListService.removeFromWaitingList(
                        (UUID) message.getData()
                );

        return success
                ? new Message(APICallType.REMOVE_FROM_WAITING_LIST_RESPONSE, null)
                : new Message(APICallType.REMOVE_FROM_WAITING_LIST_ERROR, null);
    }

    /**
     * Handles a request indicating that a guest has arrived from the waiting list.
     *
     * <p>The request data must contain the {@link UUID} of the waiting list
     * entry being marked as arrived.</p>
     *
     * @param message the incoming request message containing the entry UUID
     * @return a {@link Message} with
     *         {@link APICallType#ARRIVE_WAITING_LIST_RESPONSE}
     */
    public Message arriveWaitingList(Message message) {
        waitingListService.arriveWaitingList(
                (UUID) message.getData()
        );

        return new Message(
                APICallType.ARRIVE_WAITING_LIST_RESPONSE,
                null
        );
    }
}
