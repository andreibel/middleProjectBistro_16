package com.andreibel.server.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <h1>Order entity class.</h1>
 * <hr/>
 * this class is used to represent the order entity in the database. <br/>
 * <h3>Attributes:</h3>
 * <b>PK</b> - orderNumber: {@code int}<br/>
 * <b>C</b> - numberOfGuests: {@code int}<br/>
 * <b>C</b> - conformationCode: {@code UUID}<br/>
 * <b>C</b> - orderDateTime: {@code LocalDateTime}<br/>
 * <b>C</b> - placedOrderDateTime: {@code LocalDateTime}<br/>
 * <b>C</b> - orderCancelled: {@code boolean}<br/>
 * <b>C</b> - orderCompleted: {@code boolean}<br/>
 * <b>C</b> - orderPaid: {@code boolean}<br/>
 * <b>FK</b> - subscriberId: {@code int} (Optional)<br/>
 * <b>C</b> - email: {@code String} (Optional)<br/>
 * <b>C</b> - phoneNumber: {@code String} (Optional)<br/>
 * <hr/>
 * the columns email and phoneNumber are optional because it related to order by regular customer (then only one necessary)
 * if the order is placed by a subscriber, then both columns are null, and the subscriberId is not null.
 * @see Subscriber
 * @author Andrei Beloziyorove
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Order {
    // PK
    private int orderNumber;
    private int numberOfGuests;
    private UUID conformationCode;
    private LocalDateTime orderDateTime;
    private LocalDateTime placedOrderDateTime;
    private boolean orderCancelled;
    private boolean orderCompleted;
    private boolean orderPaid;
    // FK
    private Integer subscriberId; // optional
    private String email; // optional
    private String phoneNumber; // optional
}

