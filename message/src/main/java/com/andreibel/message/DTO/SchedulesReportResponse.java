package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * Response DTO for Schedules/Orders Report.
 *
 * Contains comprehensive statistics about customer arrivals, delays, and lateness.
 *
 * @author Aviv
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SchedulesReportResponse implements Serializable {

    /**
     * Customer arrival times grouped by date and time.
     * Map<LocalDate, Map<LocalTime, Integer>>
     *
     * Example:
     * 2025-12-20 -> {
     *     14:30 -> 2 customers arrived at 14:30,
     *     14:45 -> 3 customers arrived at 14:45,
     *     15:00 -> 1 customer arrived at 15:00
     * }
     */
    private Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture;

    /**
     * Count of late customers by date.
     * Customers who arrived after their reservation time.
     *
     * Calculation: If waitingArriveDateTime > orderDateTime
     *
     * Example:
     * 2025-12-20 -> 2 customers were late
     */
    private Map<LocalDate, Integer> customerLate;

    /**
     * Total delay minutes by date.
     * Time from physical arrival to being seated at restaurant.
     *
     * Calculation: orderArriveDateTime - waitingArriveDateTime (in minutes)
     *
     * Example:
     * 2025-12-20 -> 45 total delay minutes
     * (Customer 1 waited 15 min, Customer 2 waited 30 min)
     */
    private Map<LocalDate, Integer> customerDelay;

    /**
     * Restaurant opening time.
     * Default: 11:00 (can be configured)
     */
    private LocalTime openingTime;

    /**
     * Restaurant closing time.
     *x Default: 23:00 (can be configured)
     */
    private LocalTime closingTime;

    /**
     * Time slot interval for scheduling (in minutes).
     * Default: 30 minutes
     * Used for creating time-slot based reports and analyzing peak hours.
     */
    private Integer interval;
}