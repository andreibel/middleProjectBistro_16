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
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
        // Map to store late customers count by date (arrived after reservation time)
        Map<LocalDate, Integer> customerLate = getDaysOfCurrentMonthMap();

        // Map to store total delay minutes by date (from arrival to being seated)
        Map<LocalDate, Integer> customerDelay = getDaysOfCurrentMonthMap();

        return tx.inTransaction(() -> {
            Map<LocalDate, Map<LocalTime, Integer>> orderPartReport = orderRepository.getCountInThisMonthByTime();
            Map<LocalDate, Map<LocalTime, Integer>> waitingPartReport = waitingListRepository.getCountInThisMonthByTime();
            Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture  = sumReports(orderPartReport, waitingPartReport);

//
//
//
//            // Get all waiting list entries for analysis
//            List<Waiting> allWaiting = waitingListRepository.getAllWaitingSitNow();
//
//            // Process waiting list data
//            for (Waiting w : allWaiting) {
//                // Track customer arrivals at restaurant
//                if (w.getWaitingArriveDateTime() != null) {
//                    LocalDate arrivalDate = w.getWaitingArriveDateTime().toLocalDate();
//                    LocalTime arrivalTime = w.getWaitingArriveDateTime().toLocalTime();
//
//                    // Add to arrive/departure map (how many customers arrived at this time)
//                    customerArriveDeparture.get().computeIfAbsent(arrivalDate, k -> new LinkedHashMap<>())
//                            .merge(arrivalTime, 1, Integer::sum);
//                }
//
//                // Calculate delays and late status for customers with reservations (orders)
//                if (w.getOrderNumber() != null && w.getWaitingArriveDateTime() != null) {
//                    LocalDate date = w.getWaitingArriveDateTime().toLocalDate();
//
//                    try {
//                        // Fetch the order to get orderDateTime and orderArriveDateTime
//                        Order order = orderRepository.findById(w.getOrderNumber());
//
//                        if (order != null) {
//                            // 1. Calculate delay (from arrival at restaurant to being seated)
//                            if (order.getOrderArriveDateTime() != null) {
//                                long delayMinutes = ChronoUnit.MINUTES.between(
//                                        w.getWaitingArriveDateTime(),
//                                        order.getOrderArriveDateTime()
//                                );
//
//                                // Only count positive delays (customer waited after arriving)
//                                if (delayMinutes > 0) {
//                                    customerDelay.merge(date, (int) delayMinutes, Integer::sum);
//                                }
//                            }
//
//                            // 2. Check if customer arrived late (after reservation time)
//                            if (order.getOrderDateTime() != null) {
//                                if (w.getWaitingArriveDateTime().isAfter(order.getOrderDateTime())) {
//                                    customerLate.merge(date, 1, Integer::sum);
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        // Log error but continue processing other entries
//                        System.err.println("Error processing order " + w.getOrderNumber() + ": " + e.getMessage());
//                    }
//                }
//            }
//
//            // Set default opening/closing times (can be made configurable from bistro settings)
//            LocalTime openingTime = LocalTime.of(11, 0);
//            LocalTime closingTime = LocalTime.of(23, 0);
//            return SchedulesReportResponse.builder()
//                    .customerArriveDeparture(customerArriveDeparture.get())
//                    .customerLate(customerLate)
//                    .customerDelay(customerDelay)
//                    .openingTime(openingTime)
//                    .closingTime(closingTime)
//                    .interval(30) // 30-minute intervals (configurable)
//                    .build();
            return null;
        });
    }
    public static Map<LocalDate, Map<LocalTime, Integer>> sumReports(
            Map<LocalDate, Map<LocalTime, Integer>> a,
            Map<LocalDate, Map<LocalTime, Integer>> b
    ) {
        Map<LocalDate, Map<LocalTime, Integer>> total = new TreeMap<>();

        // helper to merge one report into total
        java.util.function.Consumer<Map<LocalDate, Map<LocalTime, Integer>>> mergeOne = report -> {
            for (var dateEntry : report.entrySet()) {
                LocalDate date = dateEntry.getKey();
                Map<LocalTime, Integer> times = dateEntry.getValue();

                Map<LocalTime, Integer> totalTimes =
                        total.computeIfAbsent(date, d -> new TreeMap<>());

                for (var timeEntry : times.entrySet()) {
                    LocalTime time = timeEntry.getKey();
                    int count = timeEntry.getValue() == null ? 0 : timeEntry.getValue();

                    totalTimes.merge(time, count, Integer::sum);
                }
            }
        };

        mergeOne.accept(a);
        mergeOne.accept(b);

        return total;
    }
    public static Map<LocalDate, Integer> getDaysOfCurrentMonthMap() {
        Map<LocalDate, Integer> result = new TreeMap<>();

        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);

        for (int i = 0; i < currentMonth.lengthOfMonth(); i++) {
            result.put(startOfMonth.plusDays(i), 0);
        }

        return result;
    }
    public static Map<LocalDate, Map<LocalTime,Integer>> getDaysOfCurrentMonthMapMap() {
        Map<LocalDate, Map<LocalTime,Integer>> result = new TreeMap<>();

        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);

        for (int i = 0; i < currentMonth.lengthOfMonth(); i++) {
            result.put(startOfMonth.plusDays(i), null);
        }

        return result;
    }


    public SubscriberReportResponse getSubscriberReport() {
        // Map to store subscriber order counts by date
        Map<LocalDate, Integer> subscriberOrdersCount = getDaysOfCurrentMonthMap();
        Map<LocalDate, Integer> subscriberWaitingListCount = getDaysOfCurrentMonthMap();

        // Map to store subscriber waiting list counts by date
        return tx.inTransaction(() -> {
            // Get all waiting list entries and count subscribers by date
            Map<LocalDate, Integer> countSubWaited = waitingListRepository.getCountInThisMount();
            for (var entry : countSubWaited.keySet()) {
                subscriberWaitingListCount.merge(entry, countSubWaited.get(entry),Integer::sum);
            }

            Map<LocalDate, Integer> countSubLate = orderRepository.getCountInThisMount();
            for (var entry : countSubLate.keySet()) {
                subscriberOrdersCount.merge(entry, countSubLate.get(entry),Integer::sum);
            }

            return SubscriberReportResponse.builder()
                    .SubscriberOrdersCount(subscriberOrdersCount)
                    .SubscriberWaitingListCount(subscriberWaitingListCount)
                    .build();
        });
    }
}
