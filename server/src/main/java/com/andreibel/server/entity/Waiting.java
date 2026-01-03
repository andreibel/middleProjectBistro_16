package com.andreibel.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * <h1>Waiting entity class.</h1>
 * <hr/>
 * this class is used to represent the waiting entity in the database. <br/>
 * <h3>Attributes:</h3>
 * <b>PK</b> - waitingNumber: {@code int}<br/>
 * <b>C</b> - waitingDateTime: {@code LocalDateTime}<br/>
 * <b>C</b> - isCurrentlyWaiting: {@code boolean}<br/>
 * <b>C</b> - conformationCode: {@code UUID}<br/>
 * <b>FK</b> - orderNumber: {@code int} (Optional)<br/>
 * <b>FK</b> - subscriberId: {@code int} (Optional)<br/>
 * <b>C</b> - email: {@code String} (Optional)<br/>
 * <b>C</b> - phoneNumber: {@code String} (Optional)<br/>
 * <hr/>
 * <h3>DDL</h3>
 * <blockquote>
 *     <pre>
 * create table Waiting
 * (
 *     waitingNumber         int auto_increment
 *         primary key,
 *     numberOfGuests        int                                  not null,
 *     waitingDateTime       datetime   default CURRENT_TIMESTAMP null,
 *     isCurrentlyWaiting    tinyint(1) default 1                 null,
 *     conformationCode      varchar(36)                          not null,
 *     waitingArriveDateTime datetime                             null,
 *     orderNumber           int                                  null,
 *     subscriberId          int                                  null,
 *     email                 varchar(30)                          null,
 *     phoneNumber           varchar(10)                          null,
 *     constraint Waiting_Order_orderNumber_fk
 *         foreign key (orderNumber) references `Order` (orderNumber),
 *     constraint Waiting_Subscriber_subscriberId_fk
 *         foreign key (subscriberId) references Subscriber (subscriberId)
 * );
 * </pre>
 * </blockquote>
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
    private int numberOfGuests;
    private LocalDateTime waitingDateTime;
    private boolean isCurrentlyWaiting;
    private UUID conformationCode;
    private LocalDateTime waitingArriveDateTime;
    // FK
    private Integer orderNumber; // Optional
    // FK
    private Integer subscriberId; // Optional
    private String email; // Optional
    private String phoneNumber; // Optional


    public static final String WAITING_NUMBER = "waitingNumber";
    public static final String NUMBER_OF_GUESTS = "numberOfGuests";
    public static final String WAITING_DATE_TIME = "waitingDateTime";
    public static final String IS_CURRENTLY_WAITING = "isCurrentlyWaiting";
    public static final String CONFIRMATION_CODE = "conformationCode";
    public static final String WAITING_ARRIVE_DATE_TIME = "waitingArriveDateTime";
    public static final String ORDER_NUMBER = "orderNumber";
    public static final String SUBSCRIBER_ID = "subscriberId";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phoneNumber";

}
