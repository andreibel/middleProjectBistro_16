package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object for special day configuration requests.
 * <p>
 * Used to define special operating hours for specific dates such as
 * holidays, events, or special occasions. Allows customizing the
 * restaurant's schedule for individual days.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecialDayRequest implements Serializable {

    /**
     * The specific date for the special schedule.
     */
    private LocalDate date;

    /**
     * Title or description of the special day.
     * Example: "Christmas Eve", "New Year's Day", "Private Event"
     */
    private String title;

    /**
     * Opening time for this special day.
     */
    private LocalTime startTime;

    /**
     * Closing time for this special day.
     */
    private LocalTime endTime;

    /**
     * Time slot interval in minutes for reservations on this special day.
     * Example: 30 for 30-minute slots, 60 for 1-hour slots.
     */
    private Integer interval;
}
