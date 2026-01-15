package com.andreibel.server.services;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.Order;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.andreibel.server.utils.TUI.printOrderCancelled;

/**
 * Periodic scheduler that automatically cancels late orders.
 *
 * <p>
 * An order is considered late if the customer did not arrive within
 * the allowed grace period (currently 15 minutes from the start time).
 * Such orders are automatically cancelled.
 * </p>
 *
 * <p>
 * The scheduler runs every {@value #CHECK_INTERVAL_MIN} minute(s).
 * </p>
 *
 * <p>
 * Implemented as a Singleton.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class OrderTimeoutScheduler {

    private static final long CHECK_INTERVAL_MIN = 1;

    private static OrderTimeoutScheduler instance;
    private final OrderRepository orderRepository;
    private final ScheduledExecutorService scheduler;
    private final WaitingListRepository waitingListRepository;
    private final TransactionManager tx = TransactionManager.getInstance();

    /**
     * Private constructor for Singleton initialization.
     */
    private OrderTimeoutScheduler() {

        orderRepository = OrderRepository.getInstance();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        waitingListRepository = WaitingListRepository.getInstance();
    }

    /**
     * @return singleton instance of OrderTimeoutScheduler
     */
    public static OrderTimeoutScheduler getInstance() {
        if (instance == null) instance = new OrderTimeoutScheduler();
        return instance;
    }

    /**
     * Starts the scheduler.
     *
     * <p>
     * Executes the late-order cancellation task periodically.
     * </p>
     */
    public void start() {
        scheduler.scheduleAtFixedRate(
                this::cancelLateOrders,
                0,
                CHECK_INTERVAL_MIN,
                TimeUnit.MINUTES
        );
    }

    public void stop() { scheduler.shutdownNow(); }

    /**
     * Cancels orders that exceeded the allowed arrival grace period.
     *
     * <p>
     * Runs inside a transaction to ensure safe database access.
     * For each cancelled order, prints a notification.
     * </p>
     */
    private void cancelLateOrders() {
        tx.inTransaction(() -> {
            waitingListRepository.removeNotTodayWaitingList();

            List<Order> cancelledOrders = orderRepository.cancelLateOrders(15);
            for (Order order : cancelledOrders) {
                printOrderCancelled(order.getConformationCode(), order.getNumberOfGuests(), order.getOrderDateTime());
            }

            return null;
        });
    }
}