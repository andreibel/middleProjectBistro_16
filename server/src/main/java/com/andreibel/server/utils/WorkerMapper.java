package com.andreibel.server.utils;

import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.server.entity.Worker;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility mapper for converting between {@link Worker},
 * database result sets, and worker-related DTOs.
 *
 * <p>
 * Contains only static mapping methods and holds no state.
 * </p>
 * @author Aviv peer
 */
public class WorkerMapper {

    /**
     * Maps a database {@link ResultSet} row to a {@link Worker} entity.
     *
     * @param rs result set positioned at the current row
     * @return mapped Worker entity
     * @throws SQLException if a column access error occurs
     */
    public static Worker mapRelToWorker(ResultSet rs) throws SQLException {
        return Worker.builder()
                .workerName(rs.getString(Worker.WORKER_NAME))
                .workerPassword(rs.getString(Worker.WORKER_PASSWORD))
                .isManager(rs.getBoolean(Worker.IS_MANAGER))
                .build();
    }

    /**
     * Maps a {@link Worker} entity to a {@link WorkerResponse} DTO.
     *
     * <p>
     * Sensitive fields such as passwords are intentionally excluded.
     * </p>
     *
     * @param worker worker entity
     * @return worker response DTO
     */
    public static WorkerResponse mapWorkerToWorkerResponse(Worker worker) {
        return WorkerResponse.builder()
                .workerName(worker.getWorkerName())
                .isManager(worker.isManager())
                .build();
    }

    /**
     * Maps a worker authentication request to a {@link Worker} entity.
     *
     * @param request worker authentication request
     * @return Worker entity containing authentication data
     */
    public static Worker mapWorkerRequestToWorker(WorkerAuth request) {
        return Worker.builder()
                .workerName(request.getWorkerName())
                .workerPassword(request.getWorkerPassword())
                .build();
    }
}
