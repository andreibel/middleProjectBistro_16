package com.andreibel.server.services;

import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.Waiting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.andreibel.server.utils.TUI.printPrice;
import static com.andreibel.server.utils.TUI.printWaitingListSeated;

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
     * Assigns tables to waiting customers and closes completed waiting entries.
     *
     * <p>
     * Runs inside a transaction to ensure safe database access.
     * Also closes waiting entries that have been seated for 2 hours and prints invoices.
     * </p>
     */
    private void cancelLateOrders() {
        tx.inTransaction(() -> {
            // Assign tables to waiting customers
            List<Waiting> currentWaitingList = waitingListRepository.getCurrentWaitingActive();
            TreeMap<Integer, Integer> available = tableService.getAllAvailableTables();
            for (Waiting waiting : currentWaitingList) {
                Map.Entry<Integer, Integer> tableEntry = available.ceilingEntry(waiting.getNumberOfGuests());
                if (tableEntry != null && tableEntry.getValue() != 0) {
                    available.merge(tableEntry.getKey(), -1, Integer::sum);
                    waitingListRepository.waitingArriveToTable(waiting.getConformationCode());
                    printWaitingListSeated(
                            waiting.getConformationCode(),
                            waiting.getNumberOfGuests(),
                            tableEntry.getKey()
                    );
                }
            }
            // Close waiting entries that have been seated for 2 hours
            List<Waiting> closedWaiting = waitingListRepository.findWaitingDueToClose(LocalDateTime.now());
            for (Waiting waiting : closedWaiting) {
                boolean isSubscriber = waiting.getSubscriberId() != null;
                printPrice(waiting.getConformationCode(), waiting.getNumberOfGuests(), isSubscriber);
            }

            return null;
        });
    }

}
