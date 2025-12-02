package com.andreibel.server.message.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderResponse  implements Serializable {
    private int orderNumber;
    private int numberOfGuests;
    private int conformationCode;
    private int subscriberId; // optional
    private LocalDateTime orderDateTime;
    private LocalDateTime placedOrderDateTime;

}
