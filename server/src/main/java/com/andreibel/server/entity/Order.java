package com.andreibel.server.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Order {

    private int orderId;
    private int numberOfGuests;
    private LocalDateTime orderDateTime;
    private LocalDateTime placedOrderDateTime;
    private int conformationCode;
    private boolean orderCancelled;
    private boolean orderCompleted;
    private boolean orderPaid;
    private UUID subscriberId; // optional
    private String email; // optional
    private String phoneNumber; // optional

}
