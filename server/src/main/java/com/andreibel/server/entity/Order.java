package com.andreibel.server.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Order {

    private int orderNumber;
    private int numberOfGuests;
    private int conformationCode;
    private int subscriberId; // optional
    private LocalDateTime orderDateTime;
    private LocalDateTime placedOrderDateTime;
    // private boolean orderCancelled;
    // private boolean orderCompleted;
    // private boolean orderPaid;
    // private String email; // optional
    // private String phoneNumber; // optional

}
