package com.andreibel.server.services;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Periodic scheduler that detects orders which should be closed.
 *
 * <p>
 * Runs a background task every minute that searches for orders which:
 * <ul>
 *   <li>are not cancelled</li>
 *   <li>are not completed</li>
 *   <li>have arrived</li>
 *   <li>started at least 2 hours ago</li>
 * </ul>
 * </p>
 *
 * <p>
 * For each such order, a notification is printed to the console.
 * </p>
 *
 * <p>
 * Implemented as a Singleton.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class OrderClosingScheduler {

    private static OrderClosingScheduler instance;

    private final OrderRepository orderRepository;
    private final TransactionManager tx;
    private final ScheduledExecutorService scheduler;

    /**
     * Private constructor for Singleton initialization.
     */
    private OrderClosingScheduler() {
        orderRepository = OrderRepository.getInstance();
        tx = TransactionManager.getInstance();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * @return singleton instance of OrderClosingScheduler
     */
    public static OrderClosingScheduler getInstance() {
        if (instance == null) instance = new OrderClosingScheduler();
        return instance;
    }

    /**
     * Starts the scheduler.
     *
     * <p>
     * Executes {@link #scanAndNotify()} every 1 minute.
     * </p>
     */
    public void start() {
        scheduler.scheduleAtFixedRate(this::scanAndNotify, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Scans for orders that should be closed and prints a notification.
     *
     * <p>
     * Runs inside a transaction to ensure safe database access.
     * </p>
     */
    private void scanAndNotify() {

        tx.inTransaction(() -> {
            orderRepository.findOrdersDueToClose(LocalDateTime.now());
            return null;
        });

    }
}