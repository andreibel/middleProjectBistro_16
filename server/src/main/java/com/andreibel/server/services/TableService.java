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

/**
 * Service responsible for restaurant table management and availability.
 * <p>
 * Handles table configuration, availability calculations, and table layout updates.
 * Implements a greedy allocation algorithm for determining available tables.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
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

    /**
     * Returns the singleton instance of {@link TableService}.
     *
     * @return the singleton TableService instance
     */
    public static TableService getInstance() {
        if (instance == null) instance = new TableService();
        return instance;
    }

    /**
     * Calculates currently available tables after accounting for active orders and waiting customers.
     * <p>
     * Uses a greedy allocation algorithm: larger groups are allocated first to the smallest
     * table that can accommodate them.
     *
     * @return a TreeMap of available tables (capacity to quantity), sorted by capacity
     */
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
            Map.Entry<Integer, Integer> fit = available.ceilingEntry(guests);
            if (fit == null) {
                continue;
            }

            int cap = fit.getKey();
            int qty = fit.getValue() - 1;

            if (qty <= 0) available.remove(cap);
            else available.put(cap, qty);
        }
        return available;
    }

    /**
     * Updates the restaurant table layout configuration.
     *
     * @param tableRequest list of table configurations to apply
     */
    public void editTables(List<TableRequest> tableRequest) {

        tx.inTransaction(() -> {
            tableRepository.syncTables(tableRequest);
            return null;
        });

    }

    /**
     * Retrieves all table configurations from the database.
     *
     * @return list of all tables as response DTOs
     */
    public List<TableResponse> getAllTables() {
        return tx.inTransaction(tableRepository::findAll)
                .stream()
                .map(TableMapper::mapTableToResonance)
                .toList();
    }
}
