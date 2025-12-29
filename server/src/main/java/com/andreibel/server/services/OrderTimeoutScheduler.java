package com.andreibel.server.services;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OrderTimeoutScheduler {
    private static final long CHECK_INTERVAL_MIN = 1;

    private static OrderTimeoutScheduler instance;
    private final OrderRepository orderRepository;
    private final TransactionManager tx = TransactionManager.getInstance();

    private OrderTimeoutScheduler() {
        orderRepository = OrderRepository.getInstance();
    }

    public static OrderTimeoutScheduler getInstance() {
        if (instance == null) instance = new OrderTimeoutScheduler();
        return instance;
    }

    public void start() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
                this::cancelLateOrders,
                0,
                CHECK_INTERVAL_MIN,
                TimeUnit.MINUTES
        );
    }

    private void cancelLateOrders() {
        try {
            tx.inTransaction(() -> orderRepository.cancelLateOrders(15));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
