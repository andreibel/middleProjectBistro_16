package com.andreibel.server.services;

import com.andreibel.message.DTO.SchedulesReportResponse;
import com.andreibel.message.DTO.SubscriberReportResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OpenTimeRepository;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.Order;
import com.andreibel.server.entity.Waiting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    private static ReportService instance;

    private final OrderRepository orderRepository;
    private final WaitingListRepository waitingListRepository;
    private final OpenTimeRepository openTimeRepository;
    private final TransactionManager tx;

    private ReportService() {
        openTimeRepository = OpenTimeRepository.getInstance();
        orderRepository = OrderRepository.getInstance();
        waitingListRepository = WaitingListRepository.getInstance();
        tx = TransactionManager.getInstance();
    }


    public static ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    public SchedulesReportResponse getSchedulesReport() {
        // Map to store arrive times: Date -> (Time -> Count of customers)
        Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture = new LinkedHashMap<>();
        // Map to store late customers count by date (arrived after reservation time)
        Map<LocalDate, Integer> customerLate = new HashMap<>();

        // Map to store total delay minutes by date (from arrival to being seated)
        Map<LocalDate, Integer> customerDelay = new HashMap<>();

        return tx.inTransaction(() -> {

            // Get all waiting list entries for analysis
            List<Waiting> allWaiting = waitingListRepository.getAllWaitingSitNow();

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

    public SubscriberReportResponse getSubscriberReport() {
        return tx.inTransaction(() -> {
            // Map to store subscriber order counts by date
            Map<LocalDate, Integer> subscriberOrdersCount = new HashMap<>();

            // Map to store subscriber waiting list counts by date
            Map<LocalDate, Integer> subscriberWaitingListCount = new HashMap<>();

            // Get all waiting list entries and count subscribers by date
            List<Waiting> allWaiting = waitingListRepository.getAllWaitingSitNow();
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
