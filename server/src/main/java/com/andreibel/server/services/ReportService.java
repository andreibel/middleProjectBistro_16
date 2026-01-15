package com.andreibel.server.services;

import com.andreibel.message.DTO.SchedulesReportResponse;
import com.andreibel.message.DTO.SubscriberReportResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OpenTimeRepository;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.OpenTime;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

/**
 * Service responsible for generating various reports related to restaurant operations.
 * <p>
 * This service provides reports for:
 * <ul>
 *     <li>Customer schedules (arrivals, late customers, delays)</li>
 *     <li>Subscriber activity (orders and waiting list entries)</li>
 * </ul>
 * <p>
 * Implemented as a singleton to ensure consistent report generation across the application.
 *
 * @author andreibel
 */
public class ReportService {
    private static ReportService instance;

    private final OrderRepository orderRepository;
    private final WaitingListRepository waitingListRepository;
    private final OpenTimeRepository openTimeRepository;
    private final TransactionManager tx;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes all required repositories and the transaction manager.
     */
    private ReportService() {
        openTimeRepository = OpenTimeRepository.getInstance();
        orderRepository = OrderRepository.getInstance();
        waitingListRepository = WaitingListRepository.getInstance();
        tx = TransactionManager.getInstance();
    }

    /**
     * Returns the singleton instance of ReportService.
     *
     * @return the singleton ReportService instance
     */
    public static ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    // ==================== Public Methods ====================

    /**
     * Generates a comprehensive schedules report for the current month.
     * <p>
     * The report includes:
     * <ul>
     *     <li>Customer arrival/departure counts grouped by date and time slots</li>
     *     <li>Count of late arrivals per day</li>
     *     <li>Delay statistics from waiting list</li>
     * </ul>
     * <p>
     * Time slots are bucketed based on the restaurant's configured interval.
     *
     * @return {@link SchedulesReportResponse} containing all schedule-related metrics
     */
    public SchedulesReportResponse getSchedulesReport() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return tx.inTransaction(() -> {
            OpenTime timeRestaurant = openTimeRepository.findRegular();
            LocalTime openTime = timeRestaurant.getOpenTime().toLocalTime();
            LocalTime closeTime = timeRestaurant.getCloseTime().toLocalTime();
            int interval = timeRestaurant.getInterval();

            Map<LocalDate, Map<LocalTime, Integer>> orderPartReport = orderRepository.getCountInThisMonthByTime();
            Map<LocalDate, Map<LocalTime, Integer>> waitingPartReport = waitingListRepository.getCountInThisMonthByTime();

            Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture = mergeTimeReports(
                    openTime, closeTime, interval, orderPartReport, waitingPartReport);
            customerArriveDeparture = padMissingDays(customerArriveDeparture, start, end);

            Map<LocalDate, Integer> lateResult = orderRepository.getLateOrders();
            Map<LocalDate, Integer> delayOrders = waitingListRepository.getDelaysOrders();

            return new SchedulesReportResponse(
                    customerArriveDeparture,
                    lateResult,
                    delayOrders,
                    openTime,
                    closeTime,
                    interval);
        });
    }

    /**
     * Generates a subscriber activity report for the current month.
     * <p>
     * The report tracks daily counts of:
     * <ul>
     *     <li>Orders placed by subscribers</li>
     *     <li>Waiting list entries by subscribers</li>
     * </ul>
     *
     * @return {@link SubscriberReportResponse} containing subscriber activity metrics
     */
    public SubscriberReportResponse getSubscriberReport() {
        Map<LocalDate, Integer> subscriberOrdersCount = createMonthMap();
        Map<LocalDate, Integer> subscriberWaitingListCount = createMonthMap();

        return tx.inTransaction(() -> {
            mergeInto(subscriberWaitingListCount, waitingListRepository.getCountInThisMount());
            mergeInto(subscriberOrdersCount, orderRepository.getCountInThisMount());

            return SubscriberReportResponse.builder()
                    .SubscriberOrdersCount(subscriberOrdersCount)
                    .SubscriberWaitingListCount(subscriberWaitingListCount)
                    .build();
        });
    }

    // ==================== Utility Methods ====================

    /**
     * Creates a map with all days of the current month initialized to zero.
     *
     * @return a {@link TreeMap} with each day of the current month mapped to 0
     */
    private static Map<LocalDate, Integer> createMonthMap() {
        Map<LocalDate, Integer> result = new TreeMap<>();
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        for (int i = 0; i < currentMonth.lengthOfMonth(); i++) {
            result.put(start.plusDays(i), 0);
        }
        return result;
    }

    /**
     * Merges values from a source map into a target map by summing counts.
     *
     * @param target the map to merge values into
     * @param source the map containing values to merge
     */
    private static void mergeInto(Map<LocalDate, Integer> target, Map<LocalDate, Integer> source) {
        source.forEach((date, count) -> target.merge(date, count, Integer::sum));
    }

    /**
     * Ensures all dates within a range exist in the map with at least an empty nested map.
     *
     * @param data  the original data map
     * @param start the start date (inclusive)
     * @param end   the end date (inclusive)
     * @return a new map with all dates in range guaranteed to have entries
     */
    private static Map<LocalDate, Map<LocalTime, Integer>> padMissingDays(
            Map<LocalDate, Map<LocalTime, Integer>> data,
            LocalDate start,
            LocalDate end) {
        Map<LocalDate, Map<LocalTime, Integer>> out = new HashMap<>(data);
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            out.computeIfAbsent(d, __ -> new HashMap<>());
        }
        return out;
    }

    /**
     * Rounds a time down to the nearest bucket based on opening time and interval.
     * <p>
     * Times before opening are clamped to the opening time.
     *
     * @param time            the time to bucket
     * @param opening         the restaurant opening time
     * @param intervalMinutes the bucket interval in minutes
     * @return the floored time slot
     */
    private static LocalTime bucketFloor(LocalTime time, LocalTime opening, int intervalMinutes) {
        if (time.isBefore(opening)) return opening;
        int minutesFromOpen = (int) Duration.between(opening, time).toMinutes();
        int bucketIndex = minutesFromOpen / intervalMinutes;
        return opening.plusMinutes((long) bucketIndex * intervalMinutes);
    }

    /**
     * Merges multiple time-based reports into unified time buckets.
     * <p>
     * Each report's entries are bucketed according to the restaurant's time interval,
     * and counts are summed across all reports. Entries after closing time are excluded.
     *
     * @param opening         the restaurant opening time
     * @param closing         the restaurant closing time
     * @param intervalMinutes the time bucket interval in minutes
     * @param reports         one or more reports to merge
     * @return a merged map of date to (time-slot to customer count)
     */
    @SafeVarargs
    private static Map<LocalDate, Map<LocalTime, Integer>> mergeTimeReports(
            LocalTime opening,
            LocalTime closing,
            int intervalMinutes,
            Map<LocalDate, Map<LocalTime, Integer>>... reports) {
        Map<LocalDate, Map<LocalTime, Integer>> result = new TreeMap<>();

        for (Map<LocalDate, Map<LocalTime, Integer>> report : reports) {
            report.forEach((date, times) -> {
                Map<LocalTime, Integer> dayBuckets = result.computeIfAbsent(date, d -> new TreeMap<>());
                times.forEach((time, count) -> {
                    LocalTime slot = bucketFloor(time, opening, intervalMinutes);
                    if (!slot.isAfter(closing)) {
                        dayBuckets.merge(slot, count, Integer::sum);
                    }
                });
            });
        }
        return result;
    }
}