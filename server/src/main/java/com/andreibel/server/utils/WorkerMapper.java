package com.andreibel.server.utils;

import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.server.entity.Worker;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkerMapper {
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

        return WorkerResponse.builder()
                .workerName(worker.getWorkerName())
                .isManager(worker.isManager())
                .build();
    }

    public static Worker mapWorkerRequestToWorker(WorkerAuth request) {
        return Worker.builder()
                .workerName(request.getWorkerName())
                .workerPassword(request.getWorkerPassword())
                .build();
    }

}
