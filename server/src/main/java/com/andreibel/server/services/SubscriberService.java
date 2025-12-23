package com.andreibel.server.services;

import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.SubscriberRepository;
import com.andreibel.server.entity.Subscriber;
import com.andreibel.server.utils.OrderMapper;
import com.andreibel.server.utils.SubscriberMapper;

import java.util.List;

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

    public SubscriberResponse createSubscriber(SubscriberRequest subscriberRequest) {
        return tx.inTransaction( () -> {
            Subscriber sub = subscriberRepository.addSubscriber(subscriberRequest);
            return SubscriberMapper.mapSubscriberToSubscriberResponse(sub);
        });
    }

    public List<SubscriberResponse> getAllSubscribers() {
        return tx.inTransaction(subscriberRepository::findAll)
                .stream()
                .map(SubscriberMapper::mapSubscriberToSubscriberResponse)
                .toList();
    }

    public SubscriberResponse getSubscriber(SubscriberRequest data) {
        return tx.inTransaction(
                () -> {
                    Subscriber subscriber;
                    if (data.getPhoneNumber() != null)
                        subscriber = subscriberRepository.getSubscriberByPhone(data.getPhoneNumber());
                    else if(data.getEmail() != null)
                        subscriber = subscriberRepository.getSubscriberByEmail(data.getEmail());
                    else
                        subscriber =  subscriberRepository.getSubscriberById(data.getSubscriberId());
                    return SubscriberMapper.mapSubscriberToSubscriberResponse(subscriber);
                }
        );
    }

    public SubscriberResponse getSubscriberAndOrders(SubscriberRequest data) {
        return tx.inTransaction(() -> {
            SubscriberResponse subscriber = getSubscriber(data);
            subscriber.setOrders(
                    orderRepository.findBySubscriberId(subscriber.getSubscriberId())
                            .stream()
                            .map(OrderMapper::mapOrderToOrderResponse)
                            .toList()
            );
            return subscriber;
        });
    }
}
