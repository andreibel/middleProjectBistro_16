package com.andreibel.client.message.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderRequest implements Serializable {
    private int orderNumber;
    private int numberOfGuests;
    private LocalDateTime orderDateTime;
}
