package com.andreibel.server.services;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.Waiting;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WaitingListArriveScheduler {
    private static final long CHECK_INTERVAL_MIN = 5;

    private static WaitingListArriveScheduler instance;
    private final WaitingListRepository waitingListRepository;
    private final ScheduledExecutorService scheduler;
    private final TableService tableService;
    private final TransactionManager tx = TransactionManager.getInstance();

    /**
     * Private constructor for Singleton initialization.
     */
    private WaitingListArriveScheduler() {

        scheduler = Executors.newSingleThreadScheduledExecutor();
        tableService = TableService.getInstance();
        waitingListRepository = WaitingListRepository.getInstance();

    }

    /**
     * @return singleton instance of OrderTimeoutScheduler
     */
    public static WaitingListArriveScheduler getInstance() {
        if (instance == null) instance = new WaitingListArriveScheduler();
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

    public void stop() {
        scheduler.shutdownNow();
    }

    /**
     * Cancels orders that exceeded the allowed arrival grace period.
     *
     * <p>
     * Runs inside a transaction to ensure safe database access.
     * </p>
     */
    private void cancelLateOrders() {
        tx.inTransaction(() -> {
            List<Waiting> currentWaitingList = waitingListRepository.getCurrentWaitingActive();
            TreeMap<Integer, Integer> available = tableService.getAllAvailableTables();
            for (Waiting waiting : currentWaitingList) {
                Map.Entry<Integer, Integer> integerIntegerEntry = available.ceilingEntry(waiting.getWaitingNumber());
                if(integerIntegerEntry != null  && integerIntegerEntry.getValue() != 0){
                    available.merge(integerIntegerEntry.getKey(), -1, Integer::sum);
                    waitingListRepository.waitingArriveToTable(waiting.getConformationCode());
                }
            }
            return null;
        });
    }

}
