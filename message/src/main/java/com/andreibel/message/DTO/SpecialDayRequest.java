package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecialDayRequest implements Serializable {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer interval;
}
