package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse implements Serializable {
    private int orderNumber;
    private int numberOfGuests;
    private UUID conformationCode;
    private int subscriberId; // optional
    private String email; // optional
    private String phoneNumber; // optionals
    private LocalDateTime orderDateTime;
    private LocalDateTime placedOrderDateTime;
}
