package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Data Transfer Object for requesting available time slots.
 * <p>
 * Used to query available reservation times for a specific date
 * and party size (capacity). Also used for lost confirmation code recovery.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeGetterRequest implements Serializable {

    /**
     * The date for which to retrieve available time slots.
     */
    private LocalDate date;

    /**
     * The required seating capacity (number of guests).
     * Used to filter available times based on table availability.
     */
    private int capacity;
}
