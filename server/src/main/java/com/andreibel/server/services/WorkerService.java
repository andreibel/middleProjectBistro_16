package com.andreibel.server.services;

import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.DTO.WorkerNewRequest;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.WorkerRepository;
import com.andreibel.server.entity.Worker;
import com.andreibel.server.utils.HmacUtil;
import com.andreibel.server.utils.WorkerMapper;

import java.nio.charset.StandardCharsets;

public class WorkerService {

    private static WorkerService instance;
    private final WorkerRepository workerRepository;
    private final TransactionManager tx;

    // TODO: move to config or ENV file
    private static final String SECRET =
            "8b6de9f7c15fa54fd4fb30e5fc583fa237e0b39bc24264c4c93426a1a4b585ab";

    private WorkerService() {
        this.workerRepository = WorkerRepository.getInstance();
        this.tx = TransactionManager.getInstance();
    }

    public static WorkerService getInstance() {
        if (instance == null) instance = new WorkerService();
        return instance;
    }

    public WorkerResponse authWorker(WorkerAuth request) {
        Worker worker = tx.inTransaction(() ->
                workerRepository.findByWorkerName(request.getWorkerName())
        );

        if (worker == null) return null;

        if (verifyPassword(request.getWorkerPassword(), worker.getWorkerPassword())) {
            return WorkerMapper.mapWorkerToWorkerResponse(worker);
        }

        return null;
    }

    public WorkerResponse createWorker(WorkerNewRequest request) {

        return tx.inTransaction(() -> {
            // prevent duplicates
            Worker existing = workerRepository.findByWorkerName(request.getName());
            if (existing != null) return null;

            String passwordHmacHex = HmacUtil.hmacSha256Hex(
                    request.getPassword().getBytes(StandardCharsets.UTF_8),
                    SECRET
            );

            Worker toInsert = Worker.builder()
                    .workerName(request.getName())
                    .workerPassword(passwordHmacHex)
                    .isManager(request.isManager())
                    .build();
            Worker saved = workerRepository.addWorker(toInsert);
            return WorkerMapper.mapWorkerToWorkerResponse(saved);
        });
    }

    private static boolean verifyPassword(String password, String hashedPasswordHex) {
        if (password == null || hashedPasswordHex == null) return false;

        String expectedHex = HmacUtil.hmacSha256Hex(
                password.getBytes(StandardCharsets.UTF_8),
                SECRET
        );

        return HmacUtil.constantTimeEqualsHex(expectedHex, hashedPasswordHex);
    }
}