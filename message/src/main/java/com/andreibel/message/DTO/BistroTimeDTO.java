package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BistroTimeDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer interval;
}
