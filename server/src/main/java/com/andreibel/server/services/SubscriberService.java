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

/**
 * Service responsible for subscriber management operations.
 * <p>
 * Handles subscriber CRUD operations and retrieval of subscriber order history.
 * Implemented as a singleton to ensure consistent data access.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
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

    /**
     * Returns the singleton instance of {@link SubscriberService}.
     *
     * @return the singleton SubscriberService instance
     */
    public static SubscriberService getInstance() {
        if (instance == null) instance = new SubscriberService();
        return instance;
    }

    /**
     * Creates a new subscriber.
     *
     * @param subscriberRequest the subscriber creation request
     * @return the created subscriber as a response DTO
     */
    public SubscriberResponse createSubscriber(SubscriberRequest subscriberRequest) {
        return tx.inTransaction(() -> {
            Subscriber sub = subscriberRepository.addSubscriber(subscriberRequest);
            return SubscriberMapper.mapSubscriberToSubscriberResponse(sub);
        });
    }

    /**
     * Retrieves all subscribers from the system.
     *
     * @return list of all subscribers as response DTOs
     */
    public List<SubscriberResponse> getAllSubscribers() {
        return tx.inTransaction(subscriberRepository::findAll)
                .stream()
                .map(SubscriberMapper::mapSubscriberToSubscriberResponse)
                .toList();
    }

    /**
     * Retrieves a subscriber by their ID.
     *
     * @param data the subscriber ID
     * @return the subscriber as a response DTO, or null if not found
     */
    public SubscriberResponse getSubscriber(Integer data) {
        return tx.inTransaction(() -> {
            Subscriber subscriber = subscriberRepository.findById(data);
            if (subscriber == null) return null;
            return SubscriberMapper.mapSubscriberToSubscriberResponse(subscriber);
        });
    }

    /**
     * Retrieves a subscriber along with their order history.
     *
     * @param data the subscriber ID
     * @return the subscriber with orders as a response DTO
     */
    public SubscriberResponse getSubscriberAndOrders(Integer data) {
        return tx.inTransaction(() -> {
            SubscriberResponse subscriber = getSubscriber(data);
            subscriber.setOrders(orderRepository.findBySubscriberId(subscriber.getSubscriberId())
                    .stream()
                    .map(OrderMapper::mapOrderToOrderResponse)
                    .toList());
            return subscriber;
        });
    }


    /**
     * Updates an existing subscriber's information.
     *
     * @param data the subscriber update request containing updated fields
     * @return the updated subscriber as a response DTO
     */
    public SubscriberResponse updateSub(SubscriberRequest data) {
        return tx.inTransaction(() -> {
            subscriberRepository.updateBySubID(data);
            return getSubscriber(data.getSubscriberId());
        });
    }
}
