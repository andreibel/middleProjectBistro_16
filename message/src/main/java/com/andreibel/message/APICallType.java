package com.andreibel.message;

/**
 * Enumeration of all API call types supported by the Bistro system.
 * <p>
 * This enum defines the communication protocol between clients and the server,
 * categorized into the following functional areas:
 * </p>
 * <ul>
 *   <li><b>Order Management:</b> Creating, retrieving, updating, and completing orders</li>
 *   <li><b>Subscriber Management:</b> Subscriber login, registration, and profile management</li>
 *   <li><b>Worker Management:</b> Worker authentication and creation</li>
 *   <li><b>Table Management:</b> Restaurant table configuration and layout</li>
 *   <li><b>Waiting List:</b> Managing walk-in customers and queue</li>
 *   <li><b>Reports:</b> Generating schedules and subscriber activity reports</li>
 * </ul>
 * <p>
 * Each request type has corresponding RESPONSE and ERROR types for handling
 * successful responses and error conditions respectively.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see Message
 */
public enum APICallType {
    /**
     * <pre>
     *   /$$$$$$                  /$$
     *  /$$__  $$                | $$
     * | $$  \ $$  /$$$$$$   /$$$$$$$  /$$$$$$   /$$$$$$
     * | $$  | $$ /$$__  $$ /$$__  $$ /$$__  $$ /$$__  $$
     * | $$  | $$| $$  \__/| $$  | $$| $$$$$$$$| $$  \__/
     * | $$  | $$| $$      | $$  | $$| $$_____/| $$
     * |  $$$$$$/| $$      |  $$$$$$$|  $$$$$$$| $$
     *  \______/ |__/       \_______/ \_______/|__/
     * </pre>
     */
    CREATE_ORDER, // OrderRequest // done✅
    CREATE_ORDER_RESPONSE, // OrderResponse✅
    CREATE_ORDER_ERROR,//✅

    DELETE_ORDER, // UUID✅
    DELETE_ORDER_RESPONSE, // no required✅
    DELETE_ORDER_ERROR,//✅

    GET_ONE_ORDER, // UUID✅
    GET_ONE_ORDER_RESPONSE, // OrderResponse✅
    GET_ONE_ORDER_ERROR, // ✅

    ORDER_ARRIVED, // OrderRequest✅
    ORDER_ARRIVED_RESPONSE, // OrderResponse✅
    ORDER_ARRIVED_WAITING_RESPONSE, // WaitingResponse✅
    ORDER_ARRIVED_ERROR,

    ORDER_LOST_CONFORMATION_CODE, // TimeGetterRequest ✅
    ORDER_LOST_CONFORMATION_CODE_RESPONSE, // OrderResponse ✅
    ORDER_LOST_CONFORMATION_CODE_ERROR, //✅

    COMPLETE_ORDER, // UUID ✅
    COMPLETE_ORDER_RESPONSE, // File ✅
    COMPLETE_ORDER_ERROR, // ✅

    GET_ALL_TIMES_IN_DATE, // OrderRequest ✅
    GET_ALL_TIMES_IN_DATE_RESPONSE, // List<LocalTime> ✅
    GET_ALL_TIMES_IN_DATE_ERROR,// ✅


//    /$$$$$$            /$$                                     /$$ /$$
//   /$$__  $$          | $$                                    |__/| $$
//  | $$  \__/ /$$   /$$| $$$$$$$   /$$$$$$$  /$$$$$$$  /$$$$$$  /$$| $$$$$$$   /$$$$$$   /$$$$$$
//  |  $$$$$$ | $$  | $$| $$__  $$ /$$_____/ /$$_____/ /$$__  $$| $$| $$__  $$ /$$__  $$ /$$__  $$
//   \____  $$| $$  | $$| $$  \ $$|  $$$$$$ | $$      | $$  \__/| $$| $$  \ $$| $$$$$$$$| $$  \__/
//   /$$  \ $$| $$  | $$| $$  | $$ \____  $$| $$      | $$      | $$| $$  | $$| $$_____/| $$
//  |  $$$$$$/|  $$$$$$/| $$$$$$$/ /$$$$$$$/|  $$$$$$$| $$      | $$| $$$$$$$/|  $$$$$$$| $$
//   \______/  \______/ |_______/ |_______/  \_______/|__/      |__/|_______/  \_______/|__/


    GET_ALL_SUBSCRIBERS, // none ✅
    GET_ALL_SUBSCRIBERS_RESPONSE, // List<SubscriberResponse> ✅
    GET_ALL_SUBSCRIBERS_ERROR,// ✅

    SUBSCRIBER_LOGIN, // Integer ✅
    SUBSCRIBER_LOGIN_RESPONSE, // SubscriberResponse ✅
    SUBSCRIBER_LOGIN_ERROR, // ✅

    GET_SUBSCRIBER_ORDERS, // Integer ✅
    GET_SUBSCRIBER_ORDERS_RESPONSE, // List<OrderResponse> ✅
    GET_SUBSCRIBER_ORDERS_ERROR, // ✅

    UPDATE_SUBSCRIBER, // SubscriberRequest ✅
    UPDATE_SUBSCRIBER_RESPONSE, // SubscriberResponse ✅
    UPDATE_SUBSCRIBER_ERROR, // ✅


    //   /$$      /$$                     /$$
//  | $$ /$$$| $$  /$$$$$$   /$$$$$$ | $$   /$$  /$$$$$$   /$$$$$$
//  | $$/$$ $$ $$ /$$__  $$ /$$__  $$| $$  /$$/ /$$__  $$ /$$__  $$
//  | $$$$_  $$$$| $$  \ $$| $$  \__/| $$$$$$/ | $$$$$$$$| $$  \__/
//  | $$$/ \  $$$| $$  | $$| $$      | $$_  $$ | $$_____/| $$
//  | $$/   \  $$|  $$$$$$/| $$      | $$ \  $$|  $$$$$$$| $$
//  |__/     \__/ \______/ |__/      |__/  \__/ \_______/|__/
//
    WORKER_LOGIN, // WorkerAuth ✅
    WORKER_LOGIN_RESPONSE, // WorkerResponse ✅
    WORKER_LOGIN_ERROR, // ✅

    WORKER_CREATE, // WorkerNewRequest ✅
    WORKER_CREATE_RESPONSE, // WorkerResponse ✅
    WORKER_CREATE_ERROR, // ✅

    CREATE_SUBSCRIBER, // SubscriberRequest ✅
    CREATE_SUBSCRIBER_RESPONSE, // SubscriberResponse✅
    CREATE_SUBSCRIBER_ERROR,// ✅

    ADD_SPECIAL_DAY, // ✅
    ADD_SPECIAL_DAY_RESPONSE, // none ✅
    ADD_SPECIAL_DAY_ERROR, // ✅

    CHANGE_BISTRO_TIME, // BistroTimeRequest  ✅
    CHANGE_BISTRO_TIME_RESPONSE, // none  ✅
    CHANGE_BISTRO_TIME_ERROR, //  ✅

    GET_ALL_TABLES, // none  ✅
    GET_ALL_TABLES_RESPONSE, // List<TableResponse>  ✅
    GET_ALL_TABLES_ERROR, //  ✅

    EDIT_BISTRO_LAYOUT, // List<TableRequest>
    EDIT_BISTRO_LAYOUT_RESPONSE, // none
    EDIT_BISTRO_LAYOUT_ERROR,

    SCHEDULES_REPORT, // none
    SCHEDULES_REPORT_RESPONSE, // SchedulesReportResponse
    SCHEDULES_REPORT_ERROR,

    SUBSCRIBER_REPORT, // none
    SUBSCRIBER_REPORT_RESPONSE, // SubscriberReportResponse
    SUBSCRIBER_REPORT_ERROR,

    GET_WAITING_LIST, // none
    GET_WAITING_LIST_RESPONSE, // List<WaitingResponse>
    GET_WAITING_LIST_ERROR,

    GET_ALL_ACTIVE_ORDER, // none
    GET_ALL_ACTIVE_RESPONSE, // List<OrderResponse>
    GET_ALL_ACTIVE_ERROR,

    GET_ALL_ARRIVED_AND_NOT_COMPLETE, // none
    GET_ALL_ARRIVED_AND_NOT_COMPLETE_RESPONSE, // List<OrderResponse>
    GET_ALL_ARRIVED_AND_NOT_COMPLETE_ERROR,

    GET_REGULAR_OPEN_TIME,
    GET_REGULAR_OPEN_TIME_RESPONSE,
    GET_REGULAR_OPEN_TIME_ERROR,

    /**
     * /$$      /$$           /$$   /$$     /$$                     /$$       /$$             /$$
     * | $$  /$ | $$          |__/  | $$    |__/                    | $$      |__/            | $$
     * | $$ /$$$| $$  /$$$$$$  /$$ /$$$$$$   /$$ /$$$$$$$   /$$$$$$ | $$       /$$  /$$$$$$$ /$$$$$$
     * | $$/$$ $$ $$ |____  $$| $$|_  $$_/  | $$| $$__  $$ /$$__  $$| $$      | $$ /$$_____/|_  $$_/
     * | $$$$_  $$$$  /$$$$$$$| $$  | $$    | $$| $$  \ $$| $$  \ $$| $$      | $$|  $$$$$$   | $$
     * | $$$/ \  $$$ /$$__  $$| $$  | $$ /$$| $$| $$  | $$| $$  | $$| $$      | $$ \____  $$  | $$ /$$
     * | $$/   \  $$|  $$$$$$$| $$  |  $$$$/| $$| $$  | $$|  $$$$$$$| $$$$$$$$| $$ /$$$$$$$/  |  $$$$/
     * |__/     \__/ \_______/|__/   \___/  |__/|__/  |__/ \____  $$|________/|__/|_______/    \___/
     * /$$  \ $$
     * |  $$$$$$/
     * \______/
     */

    ADD_TO_WAITING_LIST, // WaitingListRequest
    ADD_TO_WAITING_LIST_RESPONSE, // waitingResponse
    ADD_TO_WAITING_LIST_ERROR,

    REMOVE_FROM_WAITING_LIST, // UUID
    REMOVE_FROM_WAITING_LIST_RESPONSE, // none
    REMOVE_FROM_WAITING_LIST_ERROR,

    ARRIVE_WAITING_LIST, // UUID
    ARRIVE_WAITING_LIST_RESPONSE, // none
    ARRIVE_WAITING_LIST_ERROR;

    /**
     * Returns the lowercase label representation of this API call type.
     * <p>
     * Useful for logging, debugging, and human-readable output.
     * </p>
     *
     * @return the lowercase string representation of this enum constant
     */
    public String label() {
        return name().toLowerCase();
    }
}