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

/**
 * Service responsible for worker authentication and management.
 * <p>
 * Handles worker login authentication and new worker creation
 * with secure password hashing using HMAC-SHA256.
 * Implemented as a singleton.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
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

    /**
     * Returns the singleton instance of {@link WorkerService}.
     *
     * @return the singleton WorkerService instance
     */
    public static WorkerService getInstance() {
        if (instance == null) instance = new WorkerService();
        return instance;
    }

    /**
     * Authenticates a worker with the provided credentials.
     *
     * @param request the authentication request containing username and password
     * @return the worker response DTO if authentication succeeds, null if worker not found,
     *         or empty response if password is incorrect
     */
    public WorkerResponse authWorker(WorkerAuth request) {
        Worker worker = tx.inTransaction(() ->
                workerRepository.findByWorkerName(request.getWorkerName())
        );
        if (worker == null) return null;

        if (verifyPassword(request.getWorkerPassword(), worker.getWorkerPassword())) {
            return WorkerMapper.mapWorkerToWorkerResponse(worker);
        }

        return new WorkerResponse();
    }

    /**
     * Creates a new worker account.
     * <p>
     * The password is hashed using HMAC-SHA256 before storage.
     * Returns null if a worker with the same name already exists.
     *
     * @param request the new worker request containing name, password, and manager flag
     * @return the created worker response DTO, or null if worker already exists
     */
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

    /**
     * Verifies a password against its stored HMAC hash.
     *
     * @param password          the plaintext password to verify
     * @param hashedPasswordHex the stored HMAC hash in hexadecimal format
     * @return true if the password matches, false otherwise
     */
    private static boolean verifyPassword(String password, String hashedPasswordHex) {
        if (password == null || hashedPasswordHex == null) return false;

        String expectedHex = HmacUtil.hmacSha256Hex(
                password.getBytes(StandardCharsets.UTF_8),
                SECRET
        );
        return HmacUtil.constantTimeEqualsHex(expectedHex, hashedPasswordHex);
    }
}