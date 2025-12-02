package com.andreibel.client.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest implements Serializable {
    private int orderNumber;
    private int numberOfGuests;
    private LocalDateTime orderDateTime;
}
