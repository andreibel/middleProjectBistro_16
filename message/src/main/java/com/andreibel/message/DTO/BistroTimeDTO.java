package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BistroTimeDTO implements Serializable {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer interval;
}
