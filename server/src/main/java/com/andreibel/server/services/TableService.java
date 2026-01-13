package com.andreibel.server.services;

import com.andreibel.message.DTO.TableRequest;
import com.andreibel.message.DTO.TableResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.TableRepository;
import com.andreibel.server.dbController.repository.WaitingListRepository;
import com.andreibel.server.entity.Order;
import com.andreibel.server.entity.Waiting;
import com.andreibel.server.utils.TableMapper;

import java.util.*;

public class TableService {
    private static TableService instance;
    private final TransactionManager tx;
    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;
    private final WaitingListRepository waitingListRepository;

    private TableService() {
        tx = TransactionManager.getInstance();
        tableRepository = TableRepository.getInstance();
        orderRepository = OrderRepository.getInstance();
        waitingListRepository = WaitingListRepository.getInstance();
    }

    public static TableService getInstance() {
        if (instance == null) {
            instance = new TableService();
        }
        return instance;
    }

    public TreeMap<Integer, Integer>  getAllAvailableTables() {
        List<TableResponse> tables = getAllTables();
        List<Integer> demands = tx.inTransaction(() -> {
            List<Integer> d = new ArrayList<>();
            List<Order> ordersSitNow =orderRepository.findActiveOrders();
                    List<Waiting> waitingSitNow =waitingListRepository.getAllWaitingSitNow();
            for (Order o : ordersSitNow) d.add(o.getNumberOfGuests());
            for (Waiting w : waitingSitNow) d.add(w.getNumberOfGuests());
            return d;
        });
        demands.sort(Comparator.reverseOrder());


        // capacity -> qty (sorted by capacity)
        TreeMap<Integer, Integer> available = new TreeMap<>();
        for (TableResponse t : tables) {
            available.put(t.getCapacity(), t.getQuantity());
        }


        // allocate larger groups first

        for (int guests : demands) {
            Map.Entry<Integer, Integer> fit = available.ceilingEntry(guests); // smallest table >= guests
            if (fit == null) {
                // no table can fit this group => skip / break (depends on your business rule)
                continue;
            }

            int cap = fit.getKey();
            int qty = fit.getValue() - 1;

            if (qty <= 0) available.remove(cap);
            else available.put(cap, qty);
        }
        return available;
    }

    public void editTables(List<TableRequest> tableRequest) {

        tx.inTransaction(() -> {
            tableRepository.syncTables(tableRequest);
            return null;
        });

    }

    public List<TableResponse> getAllTables() {
        return tx.inTransaction(tableRepository::findAll)
                .stream()
                .map(TableMapper::mapTableToResonance)
                .toList();
    }
}
