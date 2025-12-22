
package com.andreibel.server.utils;

import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.DTO.WorkerRequest;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.server.entity.Subscriber;
import com.andreibel.server.entity.Table;
import com.andreibel.server.entity.Worker;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Mapper {


    public static Subscriber mapRelToSubscriber(ResultSet rs) throws SQLException {
        return Subscriber.builder()
                .subscriberId(rs.getInt(Subscriber.SUBSCRIBER_ID))
                .email(rs.getString(Subscriber.EMAIL))
                .name(rs.getString(Subscriber.NAME))
                .phoneNumber(rs.getString(Subscriber.PHONE_NUMBER))
                .build();
    }

    public static SubscriberResponse mapSubscriberToSubscriberResponse(Subscriber subscriber) {
        return new SubscriberResponse(subscriber.getSubscriberId(), subscriber.getEmail(), subscriber.getName(), subscriber.getPhoneNumber());
    }

    //Worker
    public static Worker mapRelToWorker(ResultSet rs) throws SQLException {
        return Worker.builder()
                .workerName(rs.getString(Worker.WORKER_NAME))
                .workerPassword(rs.getString(Worker.WORKER_PASSWORD))
                .workerEmail(rs.getString(Worker.WORKER_EMAIL))
                .isManager(rs.getBoolean(Worker.IS_MANAGER))
                .build();
    }

    public static WorkerResponse mapWorkerToWorkerResponse(Worker worker) {
        if (worker == null) return null;

        return WorkerResponse.builder()
                .workerName(worker.getWorkerName())
                .workerEmail(worker.getWorkerEmail())
                .isManager(worker.isManager())
                .build();
    }

    public static Worker mapWorkerRequestToWorker(WorkerRequest request) {
        if (request == null) return null;

        return Worker.builder()
                .workerName(request.getWorkerName())
                .workerPassword(request.getWorkerPassword())
                .workerEmail(request.getWorkerEmail())
                .isManager(request.isManager())
                .build();
    }


    public static Table mapRelToTable(ResultSet rs) throws SQLException {
        return Table.builder().tableId(rs.getInt(Table.TABLE_ID))
                .capacity(rs.getInt(Table.CAPACITY))
                .quantity(rs.getInt(Table.QUANTITY))
                .build();
    }
}
