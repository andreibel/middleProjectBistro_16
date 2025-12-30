package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse implements Serializable {
    private Integer numberOfGuests;
    private UUID conformationCode;
    private Integer subscriberId; // optional
    private String email; // optional
    private String phoneNumber; // optionals
    private LocalDateTime orderDateTime;
    private LocalDateTime placedOrderDateTime;
}

