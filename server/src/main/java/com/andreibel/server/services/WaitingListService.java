package com.andreibel.server.services;

import com.andreibel.message.DTO.SchedulesReportResponse;
import com.andreibel.message.DTO.SubscriberReportResponse;
import com.andreibel.message.DTO.WaitingListRequest;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.SubscriberRepository;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.Order;
import com.andreibel.server.entity.Waiting;
import com.andreibel.server.utils.WaitingListMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service layer for Waiting List operations.
 *
 * Handles business logic for managing customer waiting lists with transaction support.
 * Coordinates between DTOs and repository layer.
 */
public class WaitingListService {
    private final WaitingListRepository waitingRepository;
    private final OrderRepository orderRepository;
    private final SubscriberRepository subscriberRepository;
    private final TransactionManager tx;
    private static WaitingListService instance;

    /**
     * Private constructor - Singleton pattern.
     * Initializes repository dependencies.
     */
    private WaitingListService() {
        this.waitingRepository = WaitingListRepository.getInstance();
        this.orderRepository = OrderRepository.getInstance();
        this.subscriberRepository = SubscriberRepository.getInstance();
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
        return tx.inTransaction(() -> {
            Waiting waiting = WaitingListMapper.mapWaitingRequestToWaiting(request);
            // Set current timestamp for waitingDateTime
            waiting.setWaitingDateTime(LocalDateTime.now());
            Waiting saved = waitingRepository.addWaiting(waiting);
            return WaitingListMapper.mapWaitingToWaitingResponse(saved);
        });
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

    /**
     * Updates waiting status of a customer.
     *
     * <p>
     * Changes isCurrentlyWaiting status (typically true → false when seated or cancelled).
     * Called when customer is called to their table or leaves the waiting list.
     * </p>
     *
     * @param conformationCode the customer's confirmation code
     * @param isWaiting true if customer is still waiting, false if seated/left
     */
    public void updateWaitingState(UUID conformationCode, boolean isWaiting) {
        tx.inTransaction(() -> {
            waitingRepository.updateWaitingState(conformationCode, isWaiting);
            return null;
        });
    }

    /**
     * Removes a customer from the waiting list.
     *
     * <p>
     * If the customer has a reservation (orderNumber), the associated order will be cancelled.
     * If the customer is a walk-in (no orderNumber), they are simply removed from waiting.
     * The entry is marked as isCurrentlyWaiting = 0 (soft delete to preserve history).
     * </p>
     *
     * @param conformationCode the customer's confirmation code
     * @return true if successfully removed, false if not found
     */
    public boolean removeFromWaitingList(UUID conformationCode) {
        return tx.inTransaction(() -> waitingRepository.removeFromWaitingList(conformationCode));
    }

    /**
     * Removes a customer from the waiting list by waiting number.
     *
     * <p>
     * Alternative removal method using waitingNumber instead of confirmation code.
     * If the customer has a reservation, the order will be cancelled.
     * If the customer is a walk-in, they are simply removed.
     * </p>
     *
     * @param waitingNumber the waiting list entry ID
     * @return true if successfully removed, false if not found
     */
    public boolean removeFromWaitingListById(int waitingNumber) {
        return tx.inTransaction(() -> waitingRepository.removeFromWaitingListById(waitingNumber));
    }

    /**
     * Retrieves waiting list entries for a specific month and year.
     *
     * <p>
     * Filters waiting records by month for reporting and analytics.
     * Results are ordered by waitingDateTime descending (most recent first).
     * Useful for monthly reviews and statistics.
     * </p>
     *
     * @param date the date to extract month and year from
     * @return list of WaitingListResponse objects for that month
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
     * Returns count of customers still in queue (isCurrentlyWaiting = 1).
     * Lightweight operation useful for queue display and metrics.
     * </p>
     *
     * @return count of active waiting customers
     */
    public int countNumberOfActive() {
        return tx.inTransaction(waitingRepository::countNumberOfActive);
    }

    /**
     * Updates the arrive timestamp for a waiting customer.
     *
     * <p>
     * Sets waitingArriveDateTime to current timestamp when customer physically arrives.
     * Called when customer checks in at the restaurant.
     * </p>
     *
     * @param conformationCode the customer's confirmation code
     */
    public void arriveWaitingList(UUID conformationCode) {
        tx.inTransaction(() -> {
            waitingRepository.updateWaitingArriveDateTime(conformationCode);
            return null;
        });
    }

    /**
     * Retrieves the complete waiting list (active and inactive).
     *
     * <p>
     * Returns all waiting list entries for display and reporting purposes.
     * Includes both current waiting customers and historical entries.
     * </p>
     *
     * @return list of all WaitingListResponse objects
     */
    public List<WaitingListResponse> getWaitingList() {
        return tx.inTransaction(waitingRepository::getAllWaitingEntries)
                .stream()
                .map(WaitingListMapper::mapWaitingToWaitingResponse)
                .toList();
    }

    /**
     * Generates a  es/orders report.
     *
     * <p>
     * Provides comprehensive statistics about customer arrivals, delays, and lateness:
     * - Customer arrival times by date and time
     * - Customer delay = time from physical arrival to being seated (waitingArriveDateTime to orderArriveDateTime)
     * - Late customers = those who arrived after their reservation time (waitingArriveDateTime > orderDateTime)
     * - Operating hours and time intervals
     * </p>
     *
     * @return SchedulesReportResponse with arrival, delay and late customer statistics
     */
    public SchedulesReportResponse getSchedulesReport() {
        return tx.inTransaction(() -> {
            // Map to store arrive times: Date -> (Time -> Count of customers)
            Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture = new LinkedHashMap<>();

            // Map to store late customers count by date (arrived after reservation time)
            Map<LocalDate, Integer> customerLate = new HashMap<>();

            // Map to store total delay minutes by date (from arrival to being seated)
            Map<LocalDate, Integer> customerDelay = new HashMap<>();

            // Get all waiting list entries for analysis
            List<Waiting> allWaiting = waitingRepository.getAllWaitingEntries();

            // Process waiting list data
            for (Waiting w : allWaiting) {
                // Track customer arrivals at restaurant
                if (w.getWaitingArriveDateTime() != null) {
                    LocalDate arrivalDate = w.getWaitingArriveDateTime().toLocalDate();
                    LocalTime arrivalTime = w.getWaitingArriveDateTime().toLocalTime();

                    // Add to arrive/departure map (how many customers arrived at this time)
                    customerArriveDeparture.computeIfAbsent(arrivalDate, k -> new LinkedHashMap<>())
                            .merge(arrivalTime, 1, Integer::sum);
                }

                // Calculate delays and late status for customers with reservations (orders)
                if (w.getOrderNumber() != null && w.getWaitingArriveDateTime() != null) {
                    LocalDate date = w.getWaitingArriveDateTime().toLocalDate();

                    try {
                        // Fetch the order to get orderDateTime and orderArriveDateTime
                        Order order = orderRepository.findById(w.getOrderNumber());

                        if (order != null) {
                            // 1. Calculate delay (from arrival at restaurant to being seated)
                            if (order.getOrderArriveDateTime() != null) {
                                long delayMinutes = ChronoUnit.MINUTES.between(
                                        w.getWaitingArriveDateTime(),
                                        order.getOrderArriveDateTime()
                                );

                                // Only count positive delays (customer waited after arriving)
                                if (delayMinutes > 0) {
                                    customerDelay.merge(date, (int) delayMinutes, Integer::sum);
                                }
                            }

                            // 2. Check if customer arrived late (after reservation time)
                            if (order.getOrderDateTime() != null) {
                                if (w.getWaitingArriveDateTime().isAfter(order.getOrderDateTime())) {
                                    customerLate.merge(date, 1, Integer::sum);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Log error but continue processing other entries
                        System.err.println("Error processing order " + w.getOrderNumber() + ": " + e.getMessage());
                    }
                }
            }

            // Set default opening/closing times (can be made configurable from bistro settings)
            LocalTime openingTime = LocalTime.of(11, 0);
            LocalTime closingTime = LocalTime.of(23, 0);

            return SchedulesReportResponse.builder()
                    .customerArriveDeparture(customerArriveDeparture)
                    .customerLate(customerLate)
                    .customerDelay(customerDelay)
                    .openingTime(openingTime)
                    .closingTime(closingTime)
                    .interval(30) // 30-minute intervals (configurable)
                    .build();
        });
    }

    /**
     * Generates a subscribers report.
     *
     * <p>
     * Provides statistics about subscriber activity:
     * - Orders placed by subscribers grouped by date
     * - Subscribers in waiting list grouped by date
     * </p>
     *
     * @return SubscriberReportResponse with subscriber activity statistics
     */
    public SubscriberReportResponse getSubscriberReport() {
        return tx.inTransaction(() -> {
            // Map to store subscriber order counts by date
            Map<LocalDate, Integer> subscriberOrdersCount = new HashMap<>();

            // Map to store subscriber waiting list counts by date
            Map<LocalDate, Integer> subscriberWaitingListCount = new HashMap<>();

            // Get all waiting list entries and count subscribers by date
            List<Waiting> allWaiting = waitingRepository.getAllWaitingEntries();
            for (Waiting w : allWaiting) {
                // Count only if this is a subscriber (subscriberId not null)
                if (w.getSubscriberId() != null && w.getWaitingDateTime() != null) {
                    LocalDate date = w.getWaitingDateTime().toLocalDate();
                    subscriberWaitingListCount.merge(date, 1, Integer::sum);
                }
            }

            // Get all orders and count subscriber orders by date
            try {
                List<Order> allOrders = orderRepository.findAllSubscribersOrders();
                if (allOrders != null) {
                    for (Order order : allOrders) {
                        // Count only if this is a subscriber order (subscriberId not null)
                        if (order.getSubscriberId() != null && order.getOrderDateTime() != null) {
                            LocalDate date = order.getOrderDateTime().toLocalDate();
                            subscriberOrdersCount.merge(date, 1, Integer::sum);
                        }
                    }
                }
            } catch (Exception e) {
                // Log error but continue - waiting list data is still available
                System.err.println("Error fetching orders for subscriber report: " + e.getMessage());
            }

            return SubscriberReportResponse.builder()
                    .SubscriberOrdersCount(subscriberOrdersCount)
                    .SubscriberWaitingListCount(subscriberWaitingListCount)
                    .build();
        });
    }
}