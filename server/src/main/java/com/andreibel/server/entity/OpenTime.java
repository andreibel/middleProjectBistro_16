package com.andreibel.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Time;
import java.sql.Date;

/**
 * <h1>OpenTime entity class.</h1>
 * <hr/>
 * this class is used to represent the order entity in the database. <br/>
 * <h3>Attributes:</h3>
 * <b>PK</b> - id: {@code int}<br/>
 * <b>C</b> - SpatialDate: {@code Date}<br/>
 * <b>C</b> - SpatialDate: {@code Time}<br/>
 * <b>C</b> - closeTime: {@code Time}<br/>
 * <b>C</b> - interval: {@code int}<br/>
 * <hr/>
 * <h3>DDL</h3>
 * <blockquote> <pre>
 *     create table OpenTime
 * (
 *     id          int auto_increment
 *         primary key,
 *     SpatialDate date null,
 *     openTime    time null,
 *     closeTime   time null,
 *     `interval`  int  null
 * );
 * </pre></blockquote>
 * <hr/>
 * @author Andrei Beloziyorove
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OpenTime {
    private int id;
    private Date SpatialDate; // null for the regular day
    private Time openTime;
    private Time closeTime;
    private int interval;

    public static final String ID = "id";
    public static final String SPATIAL_DATE = "SpatialDate";
    public static final String OPEN_TIME = "openTime";
    public static final String CLOSE_TIME = "closeTime";
    public static final String INTERVAL = "interval";
}
