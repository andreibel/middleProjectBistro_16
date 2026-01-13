package com.andreibel.server.services;

import com.andreibel.message.DTO.WaitingListRequest;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.SubscriberRepository;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.Waiting;
import com.andreibel.server.utils.WaitingListMapper;

import java.util.List;

/**
 * Service layer for Waiting List operations.
 * <p>
 * Handles business logic for managing customer waiting lists with transaction support.
 * Coordinates between DTOs and repository layer.
 */
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
     * Auto-generates confirmation code, waiting number, and timestamp.
     * If customer has a reservation (orderNumber), validates that the order exists and is not cancelled.
     * </p>
     *
     * @param request the waiting list request with customer details
     * @return the created WaitingListResponse with confirmation code and position
     */
    public WaitingListResponse addNewWaiting(WaitingListRequest request) {
        Waiting waiting = WaitingListMapper.mapWaitingRequestToWaiting(request);
        Waiting saved = tx.inTransaction(() -> waitingRepository.addWaiting(waiting));
        return WaitingListMapper.mapWaitingToWaitingResponse(saved);
    }

    /**
     * Retrieves all currently active waiting customers with smart priority sorting.
     *
     * <p>
     * Returns customers still waiting, sorted by priority:
     * - Priority 1: Customers with reservations, sorted by reservation time (earliest first)
     * - Priority 2: Walk-in customers, sorted by arrival time (FIFO)
     * </p>
     *
     * @return list of active WaitingListResponse objects in priority order
     */
    public List<WaitingListResponse> getCurrentWaitingActive() {
        return tx.inTransaction(waitingRepository::getCurrentWaitingActive)
                .stream()
                .map(WaitingListMapper::mapWaitingToWaitingResponse)
                .toList();
    }
}