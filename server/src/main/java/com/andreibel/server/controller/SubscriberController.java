package com.andreibel.server.controller;

import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.Message;
import com.andreibel.server.services.SubscriberService;

public class SubscriberController {

    private static SubscriberController instance;
    private static SubscriberService subscriberService;

    private SubscriberController() {
        subscriberService = SubscriberService.getInstance();
    }

    public static SubscriberController getInstance() {
        if (instance == null) instance = new SubscriberController();
        return instance;
    }


    public Message getAllSub() {
        return new Message(APICallType.GET_ALL_SUBSCRIBERS_RESPONSE,
                subscriberService.getAllSubscribers());
    }

    public Message getSub(Message message) {
        return new Message(
            APICallType.GET_ONE_SUBSCRIBER_RESPONSE,
            subscriberService.getSubscriber((SubscriberRequest)message.getData())
        );
    }

    public Message getSubOrders(Message message) {
        return new Message(
                APICallType.GET_SUBSCRIBER_ORDERS_RESPONSE,
                subscriberService.getSubscriberAndOrders((SubscriberRequest)message.getData())
        );
    }

    public Message createSub(Message message) {
        return new Message(
                APICallType.CREATE_SUBSCRIBER_RESPONSE,
                subscriberService.createSubscriber((SubscriberRequest)message.getData())
        );
    }
}
