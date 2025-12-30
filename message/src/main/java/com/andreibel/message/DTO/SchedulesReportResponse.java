package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SchedulesReportResponse implements Serializable {
    private Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture;
    private Map<LocalDate,Integer> CustomerLate;
    private Map<LocalDate,Integer> CustomerDelay;
}
