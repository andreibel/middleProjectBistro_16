package com.andreibel.server.services;

import com.andreibel.message.DTO.WorkerRequest;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.WorkerRepository;
import com.andreibel.server.utils.Mapper;

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
        return tx.inTransaction(() -> {
            workerRepository.addWorker(request);
            var created = workerRepository.findByWorkerName(request.getWorkerName());
            return Mapper.mapWorkerToWorkerResponse(created);
        });
    }

    public WorkerResponse getWorker(String workerName) {
        return tx.inTransaction(() -> {
            var worker = workerRepository.findByWorkerName(workerName);
            return worker != null ? Mapper.mapWorkerToWorkerResponse(worker) : null;
        });
    }

    public List<WorkerResponse> getAllWorkers() {
        return tx.inTransaction(workerRepository::findAll)
                .stream()
                .map(Mapper::mapWorkerToWorkerResponse)
                .toList();
    }

    public List<WorkerResponse> getManagers() {
        return tx.inTransaction(workerRepository::findManagers)
                .stream()
                .map(Mapper::mapWorkerToWorkerResponse)
                .toList();
    }

    public List<WorkerResponse> getNonManagers() {

        return tx.inTransaction(workerRepository::findNonManagers)
                .stream()
                .map(Mapper::mapWorkerToWorkerResponse)
                .toList();

    }

    public WorkerResponse updateWorker(WorkerRequest request) {
        return tx.inTransaction(() -> {
            workerRepository.update(request);
            var updated = workerRepository.findByWorkerName(request.getWorkerName());
            return Mapper.mapWorkerToWorkerResponse(updated);
        });

    }

    public void deleteWorker(String workerName) {
        tx.inTransaction(() -> {
            workerRepository.delete(workerName);
            return null;
        });
    }
}
