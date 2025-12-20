package com.andreibel.server.services;

import com.andreibel.message.DTO.WorkerRequest;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.WorkerRepository;
import com.andreibel.server.utils.Mapper;

import java.sql.SQLException;
import java.util.List;

public class WorkerService {

    private static WorkerService instance;
    private final WorkerRepository workerRepository;
    private final TransactionManager tx;

    private WorkerService() {
        this.workerRepository = WorkerRepository.getInstance();
        this.tx = TransactionManager.getInstance();
    }

    public static WorkerService getInstance() {
        if (instance == null) instance = new WorkerService();
        return instance;
    }

    public WorkerResponse createWorker(WorkerRequest request) {
        try {
            return tx.inTransaction(() -> {
                workerRepository.addWorker(request);
                var created = workerRepository.findByWorkerName(request.getWorkerName());
                return Mapper.mapWorkerToWorkerResponse(created);
            });
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create worker", e);
        }
    }

    public WorkerResponse getWorker(String workerName) {
        try {
            return tx.inTransaction(() -> {
                var worker = workerRepository.findByWorkerName(workerName);
                return worker != null ? Mapper.mapWorkerToWorkerResponse(worker) : null;
            });
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch worker", e);
        }
    }

    public List<WorkerResponse> getAllWorkers() {
        try {
            return tx.inTransaction(workerRepository::findAll)
                    .stream()
                    .map(Mapper::mapWorkerToWorkerResponse)
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch workers", e);
        }
    }

    public List<WorkerResponse> getManagers() {
        try {
            return tx.inTransaction(workerRepository::findManagers)
                    .stream()
                    .map(Mapper::mapWorkerToWorkerResponse)
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch managers", e);
        }
    }

    public List<WorkerResponse> getNonManagers() {
        try {
            return tx.inTransaction(workerRepository::findNonManagers)
                    .stream()
                    .map(Mapper::mapWorkerToWorkerResponse)
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch non-managers", e);
        }
    }

    public WorkerResponse updateWorker(WorkerRequest request) {
        try {
            return tx.inTransaction(() -> {
                workerRepository.update(request);
                var updated = workerRepository.findByWorkerName(request.getWorkerName());
                return Mapper.mapWorkerToWorkerResponse(updated);
            });
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update worker", e);
        }
    }

    public void deleteWorker(String workerName) {
        try {
            tx.inTransaction(() -> {
                workerRepository.delete(workerName);
                return null;
            });
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete worker", e);
        }
    }
}
