package com.andreibel.server.services;

import com.andreibel.message.DTO.TableRequest;
import com.andreibel.message.DTO.TableResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.TableRepository;
import com.andreibel.server.utils.TableMapper;

import java.util.List;

public class TableService {
    private static TableService instance;
    private final TransactionManager tx;
    private final TableRepository tableRepository;

    private TableService() {
        tx = TransactionManager.getInstance();
        tableRepository = TableRepository.getInstance();
    }

    public static TableService getInstance() {
        if (instance == null) {
            instance = new TableService();
        }
        return instance;
    }

    public void editTables(List<TableRequest> tableRequest) {
        for (TableRequest table : tableRequest) {
            tx.inTransaction(() -> {
                tableRepository.editTable(table.getCapacity(), table.getQuantity());
                return null;
            });
        }
    }
    public List<TableResponse> getAllTables() {
        return tx.inTransaction(tableRepository::findAll)
                .stream()
                .map(TableMapper::mapTableToResonance)
                .toList();
    }
}
