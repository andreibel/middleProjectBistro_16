package com.andreibel.server.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>Worker entity class.</h1>
 * <hr/>
 * this class is used to represent the worker entity in the database. <br/>
 * <h3>Attributes:</h3>
 * <b>PK</b> - tableId: {@code int}<br/>
 * <b>C</b> - capacity: {@code int}<br/>
 * <b>C</b> - quantity: {@code int}<br/>
 * <hr/>
 * <h3>DDL</h3>
 * <blockquote>
 * <pre>
 * create table `Table`
 * (
 *     tableId  int auto_increment
 *         primary key,
 *     capacity int not null,
 *     quantity int null
 * );
 * </pre>
 * </blockquote>
 * <hr/>
 *
 * @author andrei beloziorove
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Table {
    //PK
    private int tableId;
    private int capacity;
    private int quantity;

    public static final String TABLE_ID = "tableId";
    public static final String CAPACITY = "capacity";
    public static final String QUANTITY = "quantity";
}
