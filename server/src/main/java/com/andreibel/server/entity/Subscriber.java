package com.andreibel.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * <h1>Subscriber entity class.</h1>
 * <hr/>
 * this class is used to represent the subscriber entity in the database. <br/>
 * <h3>Attributes:</h3>
 * <b>PK</b> - subscriberId: {@code int}<br/>
 * <b>C</b> - email: {@code String}<br/>
 * <b>C</b> - name: {@code String}<br/>
 * <b>C</b> - phoneNumber: {@code String}<br/>
 * <hr/>
 * lists of orders and waiting are not part of the table, but they are used to get all the orders and waiting
 * orders of a subscriber.
 * @see Order
 * @see Waiting
 * @author Andrei Beloziyorove
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Subscriber {
    // PK
    private int subscriberId;
    private String email;
    private String name;
    private String phoneNumber;

    // not part of the table
    // if you want to get all the orders of a subscriber, use the order list
    private List<Order> orders;
    // if you want to get all the waiting orders of a subscriber, use the waiting list
    private List<Waiting> waiting;
}
