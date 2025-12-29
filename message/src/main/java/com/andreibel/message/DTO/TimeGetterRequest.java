package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeGetterRequest {
    private LocalDate date;
    private int capacity;
}
