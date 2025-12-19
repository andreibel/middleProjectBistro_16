package com.andreibel.server.services;

import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.SubscriberRepository;

public class SubscriberService {
    private final OrderRepository orderRepository;
    private final TransactionManager tx;
    private final SubscriberRepository subscriberRepository;
    private static SubscriberService instance;

    private SubscriberService() {
        this.orderRepository = OrderRepository.getInstance();
        this.tx = TransactionManager.getInstance();
        this.subscriberRepository = SubscriberRepository.getInstance();
    }

    public static SubscriberService getInstance() {
        if (instance == null) instance = new SubscriberService();
        return instance;
    }

    public SubscriberResponse createSubscriber() {
        return null;
    }

}
