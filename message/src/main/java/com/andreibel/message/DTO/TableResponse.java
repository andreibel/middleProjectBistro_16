package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableResponse implements Serializable {
    private Integer capacity;
    private Integer quantity;
}
