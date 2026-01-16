package com.andreibel.server.utils;

import com.andreibel.message.DTO.WorkerNewRequest;
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
 *
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
     * Maps a {@link WorkerNewRequest} entity to a {@link Worker}
     *
     * <p>
     * Sensitive fields such as passwords are intentionally excluded.
     * </p>
     *
     * @param request        worker entity
     * @param hashedPassword string
     * @return worker response DTO
     */
    public static Worker newWorker(WorkerNewRequest request, String hashedPassword) {
        return Worker.builder()
                .workerName(request.getName())
                .workerPassword(hashedPassword)
                .isManager(request.isManager())
                .build();
    }

}
