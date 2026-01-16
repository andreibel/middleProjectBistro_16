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
    CREATE_ORDER,
    CREATE_ORDER_RESPONSE,
    CREATE_ORDER_ERROR,

    DELETE_ORDER,
    DELETE_ORDER_RESPONSE,
    DELETE_ORDER_ERROR,

    ORDER_ARRIVED,
    ORDER_ARRIVED_RESPONSE,
    ORDER_ARRIVED_ERROR,

    ORDER_LOST_CONFORMATION_CODE,
    ORDER_LOST_CONFORMATION_CODE_RESPONSE,
    ORDER_LOST_CONFORMATION_CODE_ERROR,

    COMPLETE_ORDER,
    COMPLETE_ORDER_RESPONSE,
    COMPLETE_ORDER_ERROR,

    GET_ALL_TIMES_IN_DATE,
    GET_ALL_TIMES_IN_DATE_RESPONSE,
    GET_ALL_TIMES_IN_DATE_ERROR,

    GET_ALL_SUBSCRIBERS,
    GET_ALL_SUBSCRIBERS_RESPONSE,
    GET_ALL_SUBSCRIBERS_ERROR,

    SUBSCRIBER_LOGIN,
    SUBSCRIBER_LOGIN_RESPONSE,
    SUBSCRIBER_LOGIN_ERROR,

    GET_SUBSCRIBER_ORDERS,
    GET_SUBSCRIBER_ORDERS_RESPONSE,
    GET_SUBSCRIBER_ORDERS_ERROR,

    UPDATE_SUBSCRIBER,
    UPDATE_SUBSCRIBER_RESPONSE,
    UPDATE_SUBSCRIBER_ERROR,

    WORKER_LOGIN,
    WORKER_LOGIN_RESPONSE,
    WORKER_LOGIN_ERROR,

    WORKER_CREATE,
    WORKER_CREATE_RESPONSE,
    WORKER_CREATE_ERROR,

    CREATE_SUBSCRIBER,
    CREATE_SUBSCRIBER_RESPONSE,
    CREATE_SUBSCRIBER_ERROR,

    ADD_SPECIAL_DAY,
    ADD_SPECIAL_DAY_RESPONSE,
    ADD_SPECIAL_DAY_ERROR,

    CHANGE_BISTRO_TIME,
    CHANGE_BISTRO_TIME_RESPONSE,
    CHANGE_BISTRO_TIME_ERROR,

    GET_ALL_TABLES,
    GET_ALL_TABLES_RESPONSE,
    GET_ALL_TABLES_ERROR,

    EDIT_BISTRO_LAYOUT,
    EDIT_BISTRO_LAYOUT_RESPONSE,
    EDIT_BISTRO_LAYOUT_ERROR,

    SCHEDULES_REPORT,
    SCHEDULES_REPORT_RESPONSE,
    SCHEDULES_REPORT_ERROR,

    SUBSCRIBER_REPORT,
    SUBSCRIBER_REPORT_RESPONSE,
    SUBSCRIBER_REPORT_ERROR,

    GET_WAITING_LIST,
    GET_WAITING_LIST_RESPONSE,
    GET_WAITING_LIST_ERROR,

    GET_ALL_ACTIVE_ORDER,
    GET_ALL_ACTIVE_RESPONSE,
    GET_ALL_ACTIVE_ERROR,

    GET_ALL_ARRIVED_AND_NOT_COMPLETE,
    GET_ALL_ARRIVED_AND_NOT_COMPLETE_RESPONSE,
    GET_ALL_ARRIVED_AND_NOT_COMPLETE_ERROR,

    GET_REGULAR_OPEN_TIME,
    GET_REGULAR_OPEN_TIME_RESPONSE,
    GET_REGULAR_OPEN_TIME_ERROR,

    ADD_TO_WAITING_LIST,
    ADD_TO_WAITING_LIST_RESPONSE,
    ADD_TO_WAITING_LIST_ERROR;

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