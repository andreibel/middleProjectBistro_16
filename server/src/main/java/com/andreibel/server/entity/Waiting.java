package com.andreibel.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * <h1>Waiting entity class.</h1>
 * <hr/>
 * this class is used to represent the waiting entity in the database. <br/>
 * <h3>Attributes:</h3>
 * <b>PK</b> - waitingNumber: {@code int}<br/>
 * <b>C</b> - waitingDateTime: {@code LocalDateTime}<br/>
 * <b>C</b> - isCurrentlyWaiting: {@code boolean}<br/>
 * <b>FK</b> - orderNumber: {@code int} (Optional)<br/>
 * <b>FK</b> - subscriberId: {@code int} (Optional)<br/>
 * <b>C</b> - email: {@code String} (Optional)<br/>
 * <b>C</b> - phoneNumber: {@code String} (Optional)<br/>
 * <hr/>
 * the columns email and phoneNumber are optional. It related to a waiting list by regular customer (then only one
 * necessary)
 * if the order is placed by a subscriber, then both columns are null, and the subscriberId is not null.
 * also it is possible that if someone is order a table but the restaurant is full, he/she can be added to the
 * waiting list with the order number.
 * @see Order
 * @see Subscriber
 * @author andrei beloziorove
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Waiting {
    // PK
    private int waitingNumber;
    private LocalDateTime waitingDateTime;
    private boolean isCurrentlyWaiting;
    // FK
    private Integer orderNumber; // Optional
    // FK
    private Integer subscriberId; // Optional
    private String email; // Optional
    private String phoneNumber; // Optional

}
