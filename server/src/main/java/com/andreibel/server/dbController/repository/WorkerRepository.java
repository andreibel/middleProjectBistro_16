package com.andreibel.server.dbController.repository;

import com.andreibel.message.DTO.WorkerRequest;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.entity.Worker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.andreibel.server.utils.Mapper.mapRelToWorker;

public class WorkerRepository {

    private final TransactionManager tx;
    private static WorkerRepository instance;

    private WorkerRepository() {
        this.tx = TransactionManager.getInstance();
    }

    public static WorkerRepository getInstance() {
        if (instance == null) instance = new WorkerRepository();
        return instance;
    }

    public void addWorker(WorkerRequest workerRequest) throws SQLException {
        String sql = "INSERT INTO bistro.worker ({0}, {1}, {2}, {3}) VALUES (?,?,?,?);";
        sql = String.format(sql, Worker.WORKER_NAME, Worker.WORKER_PASSWORD, Worker.WORKER_EMAIL, Worker.IS_MANAGER);
        
        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, workerRequest.getWorkerName());
            stmt.setString(2, workerRequest.getWorkerPassword());
            stmt.setString(3, workerRequest.getWorkerEmail());
            stmt.setBoolean(4, workerRequest.isManager());
            stmt.executeUpdate();
        }
    }

    public Worker findByWorkerName(String workerName) throws SQLException {
        String sql = "SELECT * FROM bistro.worker WHERE {0} = ?;";
        sql = String.format(sql, Worker.WORKER_NAME);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, workerName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRelToWorker(rs) : null;
            }
        }
    }

    public List<Worker> findAll() throws SQLException {
        String sql = "SELECT * FROM bistro.worker;";
        List<Worker> workers = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                workers.add(mapRelToWorker(rs));
            }
        }
        return workers;
    }

    public List<Worker> findManagers() throws SQLException {
        String sql = "SELECT * FROM bistro.worker WHERE {0} = true;";
        sql = String.format(sql, Worker.IS_MANAGER);
        List<Worker> workers = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                workers.add(mapRelToWorker(rs));
            }
        }
        return workers;
    }

    public List<Worker> findNonManagers() throws SQLException {
        String sql = "SELECT * FROM bistro.worker WHERE {0} = false;";
        sql = String.format(sql, Worker.IS_MANAGER);
        List<Worker> workers = new ArrayList<>();

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                workers.add(mapRelToWorker(rs));
            }
        }
        return workers;
    }

    public void update(WorkerRequest workerRequest) throws SQLException {
        String sql = "UPDATE bistro.worker SET {0} = ?, {1} = ?, {2} = ? WHERE {3} = ?;";
        sql = String.format(sql, Worker.WORKER_PASSWORD, Worker.WORKER_EMAIL, Worker.IS_MANAGER, Worker.WORKER_NAME);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, workerRequest.getWorkerPassword());
            stmt.setString(2, workerRequest.getWorkerEmail());
            stmt.setBoolean(3, workerRequest.isManager());
            stmt.setString(4, workerRequest.getWorkerName());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Worker not found: " + workerRequest.getWorkerName());
            }
        }
    }

    public void delete(String workerName) throws SQLException {
        String sql = "DELETE FROM bistro.worker WHERE {0} = ?;";
        sql = String.format(sql, Worker.WORKER_NAME);

        try (PreparedStatement stmt = tx.currentConnection().prepareStatement(sql)) {
            stmt.setString(1, workerName);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Worker not found: " + workerName);
            }
        }
    }
}
