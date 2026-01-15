package com.andreibel.server.services;

import com.andreibel.message.DTO.SchedulesReportResponse;
import com.andreibel.message.DTO.SubscriberReportResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OpenTimeRepository;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.OpenTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

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
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());
        return tx.inTransaction(() -> {
            OpenTime timeRestaurant = openTimeRepository.findRegular();
            Map<LocalDate, Map<LocalTime, Integer>> orderPartReport = orderRepository.getCountInThisMonthByTime();
            System.out.println(orderPartReport);
            Map<LocalDate, Map<LocalTime, Integer>> waitingPartReport = waitingListRepository.getCountInThisMonthByTime();
            System.out.println(waitingPartReport);
            Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture;
            customerArriveDeparture = normalizeAndMerge(orderPartReport,waitingPartReport,
                    timeRestaurant.getOpenTime().toLocalTime(),
                    timeRestaurant.getCloseTime().toLocalTime(),
                    timeRestaurant.getInterval());
            customerArriveDeparture = padMissingDays(customerArriveDeparture, start, end);
            System.out.println(customerArriveDeparture);
            Map<LocalDate, Integer> lateResult = orderRepository.getLateOrders();
            for (var entry : lateResult.keySet()) {
                customerLate.merge(entry, lateResult.get(entry),Integer::sum);
            }

            Map<LocalDate, Integer> delayOrders = waitingListRepository.getDelaysOrders();
            for (var entry : delayOrders.keySet()) {
                customerDelay.merge(entry, delayOrders.get(entry),Integer::sum);
            }
            SchedulesReportResponse  response = new SchedulesReportResponse(customerArriveDeparture,
                    lateResult,
                    delayOrders,
                    timeRestaurant.getOpenTime().toLocalTime(),
                    timeRestaurant.getCloseTime().toLocalTime(),
                    timeRestaurant.getInterval());
            System.out.println(response);

            return response;

        });
    }
    public static Map<LocalDate, Map<LocalTime, Integer>> padMissingDays(
            Map<LocalDate, Map<LocalTime, Integer>> data,
            LocalDate startInclusive,
            LocalDate endInclusive
    ) {
        Map<LocalDate, Map<LocalTime, Integer>> out = new HashMap<>(data);

        for (LocalDate d = startInclusive; !d.isAfter(endInclusive); d = d.plusDays(1)) {
            out.computeIfAbsent(d, __ -> new HashMap<>());
        }
        return out;
    }
    private static LocalTime bucketFloor(LocalTime t, LocalTime opening, int intervalMinutes) {
        // clamp below opening
        if (t.isBefore(opening)) return opening;

        int minutesFromOpen = (int) java.time.Duration.between(opening, t).toMinutes();
        int bucketIndex = minutesFromOpen / intervalMinutes; // floor
        return opening.plusMinutes((long) bucketIndex * intervalMinutes);
    }

    public static Map<LocalDate, Map<LocalTime, Integer>> normalizeAndMerge(
            Map<LocalDate, Map<LocalTime, Integer>> orders,
            Map<LocalDate, Map<LocalTime, Integer>> waiting,
            LocalTime opening,
            LocalTime closing,
            int intervalMinutes
    ) {
        Map<LocalDate, Map<LocalTime, Integer>> out = new TreeMap<>();

        java.util.function.BiConsumer<LocalDate, Map<LocalTime,Integer>> addDay = (date, times) -> {
            Map<LocalTime, Integer> dayOut = out.computeIfAbsent(date, d -> new TreeMap<>());

            for (var e : times.entrySet()) {
                LocalTime raw = e.getKey();
                int count = e.getValue();

                LocalTime slot = bucketFloor(raw, opening, intervalMinutes);

                // optional: drop anything after closing (or closing-interval)
                if (slot.isAfter(closing)) continue;

                dayOut.merge(slot, count, Integer::sum);
            }
        };

        orders.forEach(addDay);
        waiting.forEach(addDay);

        return out;
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
