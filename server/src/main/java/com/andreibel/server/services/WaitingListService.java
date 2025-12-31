package com.andreibel.server.services;

import com.andreibel.message.DTO.WaitingListRequest;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.Waiting;
import com.andreibel.server.utils.WaitingListMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class WaitingListService {
    private final WaitingListRepository waitingRepository;
    private final TransactionManager tx;
    private static WaitingListService instance;

    /**
     * Private constructor - Singleton pattern.
     * Initializes repository dependencies.
     */
    private WaitingListService() {
        this.waitingRepository = WaitingListRepository.getInstance();
        this.tx = TransactionManager.getInstance();
    }

    /**
     * Gets singleton instance of WaitingService.
     *
     * @return singleton instance
     */
    public static WaitingListService getInstance() {
        if (instance == null) instance = new WaitingListService();
        return instance;
    }

    /**
     * Adds a new customer to the waiting list.
     *
     * <p>
     * Creates a waiting list entry for either a registered subscriber or guest.
     * Auto-generates confirmation code and timestamp.
     * </p>
     */
    public WaitingListResponse addNewWaiting(WaitingListRequest request) {
        return tx.inTransaction(() -> {
            Waiting waiting = WaitingListMapper.mapWaitingRequestToWaiting(request);
            Waiting saved = waitingRepository.addWaiting(waiting);
            return WaitingListMapper.mapWaitingToWaitingResponse(saved);
        });
    }

    /**
     * Retrieves all currently active waiting customers with position numbers.
     *
     * <p>
     * Returns customers still waiting, sorted by priority:
     * </p>
     */
    public List<WaitingListResponse> getCurrentWaitingActive() {
        return tx.inTransaction(waitingRepository::getCurrentWaitingActive)
                .stream()
                .map(WaitingListMapper::mapWaitingToWaitingResponse)
                .toList();
    }


    /**
     * Updates waiting status of a customer.
     *
     * <p>
     * Changes isCurrentlyWaiting status (typically true → false when seated).
     * Called when customer is called to their table or cancels.
     * </p>
     */
    public void updateWaitingState(UUID conformationCode, boolean isWaiting) {
        tx.inTransaction(() -> {
            waitingRepository.updateWaitingState(conformationCode, isWaiting);
            return null;
        });
    }

    /**
     * Retrieves waiting list entries for a specific month and year.
     *
     * <p>
     * Filters waiting records by month for reporting and analytics.
     * Useful for monthly reviews and statistics.
     * </p>
     */
    public List<WaitingListResponse> getWaitingByMonth(LocalDate date) {
        return tx.inTransaction(() -> waitingRepository.getWaitingByMonth(date.getMonthValue(), date.getYear()))
                .stream()
                .map(WaitingListMapper::mapWaitingToWaitingResponse)
                .toList();
    }

    /**
     * Counts number of currently active waiting customers.
     *
     * <p>
     * Returns count of customers still in queue (isCurrentlyWaiting = true).
     * Lightweight operation useful for queue display and metrics.
     * </p>
     */
    public int countNumberOfActive() {
        return tx.inTransaction(waitingRepository::countNumberOfActive);
    }
}
