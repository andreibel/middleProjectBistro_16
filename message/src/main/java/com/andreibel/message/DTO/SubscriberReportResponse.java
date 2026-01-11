package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

/**
 * Response DTO for Subscriber Report.
 * Contains subscriber activity statistics grouped by date.
 *
 * @author Aviv
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubscriberReportResponse implements Serializable {

    /**
     * Count of orders placed by subscribers by date.
     * Map<LocalDate, Integer>
     *
     * xExample:
     * 2025-12-20 -> 5 orders from subscribers
     * 2025-12-21 -> 3 orders from subscribers
     */
    private Map<LocalDate, Integer> SubscriberOrdersCount;

    /**
     * Count of subscribers in waiting list by date.
     * Map<LocalDate, Integer>
     *
     * Example:
     * 2025-12-20 -> 2 subscribers waiting
     * 2025-12-21 -> 4 subscribers waiting
     */
    private Map<LocalDate, Integer> SubscriberWaitingListCount;
}