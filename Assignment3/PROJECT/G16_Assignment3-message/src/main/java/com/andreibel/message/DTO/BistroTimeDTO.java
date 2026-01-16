package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Data Transfer Object for Bistro operating hours configuration.
 * <p>
 * Used to configure and retrieve the regular operating schedule
 * of the restaurant, including opening/closing times and reservation
 * time slot intervals.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BistroTimeDTO implements Serializable {

    /**
     * Restaurant opening time.
     * Time when the restaurant begins accepting reservations.
     */
    private LocalTime startTime;

    /**
     * Restaurant closing time.
     * Time when the restaurant stops accepting reservations.
     */
    private LocalTime endTime;

    /**
     * Time slot interval in minutes for reservations.
     * Defines the granularity of available reservation times.
     * Example: 30 for 30-minute slots (11:00, 11:30, 12:00, etc.)
     */
    private Integer interval;
}
