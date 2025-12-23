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
 * <b>PK</b> - workerName: {@code String}<br/>
 * <b>C</b> - workerPassword: {@code String}<br/>
 * <b>C</b> - workerEmail: {@code String}<br/>
 * <b>C</b> - isManager: {@code boolean}<br/>
 * <hr/>
 * <h3>DDL</h3>
 * <blockquote>
 * <pre>
 * create table Worker (
 *     workerName     varchar(30)          not null,
 *     workerPassword varchar(70)          not null,
 *     workerEmail    varchar(30)          not null,
 *     isManager      tinyint(1) default 0 not null
 * );
 * </blockquote>
 * <hr/>
 * @author andrei beloziorove
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Worker {

    // PK
    private String workerName;
    private String workerPassword;
    private String workerEmail;
    private boolean isManager;


    // Database Column Names
    public static final String WORKER_NAME = "workerName";
    public static final String WORKER_PASSWORD = "workerPassword";
    public static final String WORKER_EMAIL = "workerEmail";
    public static final String IS_MANAGER = "isManager";

}