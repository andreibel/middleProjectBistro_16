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
 * <h3>DDL</h3>
 * <blockquote> <pre>
 * create table `Order`(
 *     orderNumber         int auto_increment
 *         primary key,
 *     numberOfGuests      int                        not null,
 *     conformationCode    char(36)                   not null,
 *     orderDateTime       datetime                   not null,
 *     placedOrderDateTime datetime   default (now()) not null,
 *     orderCancelled      tinyint(1) default 0       not null,
 *     orderCompleted      tinyint(1) default 0       not null,
 *     orderPaid           int        default 0       not null,
 *     subscriberId        int                        null,
 *     email               varchar(30)                null,
 *     phoneNumber         varchar(10)                null,
 *     constraint Order_Subscriber_subscriberId_fk
 *         foreign key (subscriberId) references Subscriber (subscriberId)
 *             on delete cascade
 * );
 * </pre></blockquote>
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



    //columns name in the database
    public static final String ORDER_NUMBER = "orderNumber";
    public static final String NUMBER_OF_GUESTS = "numberOfGuests";
    public static final String CONFIRMATION_CODE = "conformationCode";
    public static final String ORDER_DATE_TIME = "orderDateTime";
    public static final String PLACED_ORDER_DATE_TIME = "placedOrderDateTime";
    public static final String ORDER_CANCELLED = "orderCancelled";
    public static final String ORDER_COMPLETED = "orderCompleted";
    public static final String ORDER_PAID = "orderPaid";
    public static final String SUBSCRIBER_ID = "subscriberId";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phoneNumber";
}

