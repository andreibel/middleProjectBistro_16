package com.andreibel.message;

public enum APICallType {

    // order-related calls client -> server
    // already authenticated in the server the behavior
    CREATE_ORDER,
    UPDATE_ORDER,
    DELETE_ORDER,
    GET_ALL_ORDERS,
    GET_ONE_ORDER,
    ORDER_ARRIVED,
    COMPLETE_ORDER,
    GET_ALL_TIMES_IN_DATE,
    // order-related calls server -> client
    // already authenticated in the server the behavior
    GET_ALL_ORDERS_RESPONSE,
    GET_ONE_ORDER_RESPONSE,
    UPDATE_ORDER_RESPONSE,
    DELETE_ORDER_RESPONSE,
    CREATE_ORDER_RESPONSE,
    ORDER_ARRIVED_RESPONSE,
    COMPLETE_ORDER_RESPONSE,
    GET_ALL_TIMES_IN_DATE_RESPONSE,

    // subscriber-related calls client -> server
    GET_ALL_SUBSCRIBERS,
    GET_ONE_SUBSCRIBER,
    GET_SUBSCRIBER_ORDERS,
    CREATE_SUBSCRIBER,
    UPDATE_SUBSCRIBER,
    // subscriber-related calls server -> client
    GET_ALL_SUBSCRIBERS_RESPONSE,
    GET_ONE_SUBSCRIBER_RESPONSE,
    GET_SUBSCRIBER_ORDERS_RESPONSE,
    CREATE_SUBSCRIBER_RESPONSE,
    UPDATE_SUBSCRIBER_RESPONSE,

    // worker-related calls client -> server
    LOGIN_WORKER,

    // worker-related calls server -> client
    LOGIN_WORKER_RESPONSE,

    ERROR
}
