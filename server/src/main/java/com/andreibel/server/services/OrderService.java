package com.andreibel.server.services;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.TableRepository;
import com.andreibel.server.entity.Order;
import com.andreibel.server.entity.Table;
import com.andreibel.server.utils.OrderMapper;

import java.time.LocalDateTime;
import java.util.*;

public class OrderService {

    private static OrderService instance;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final TransactionManager tx;

    private OrderService() {
        this.orderRepository = OrderRepository.getInstance();
        this.tableRepository = TableRepository.getInstance();
        this.tx = TransactionManager.getInstance();
    }

    public static OrderService getInstance() {
        if (instance == null) instance = new OrderService();
        return instance;
    }

    public List<OrderResponse> getAllOrders() {
        return tx.inTransaction(orderRepository::findAll).stream().map(OrderMapper::mapOrderToOrderResponse).toList();
    }

    public OrderResponse updateOrder(OrderRequest request) {
        return tx.inTransaction(() -> {
            orderRepository.update(request);
            var updated = orderRepository.findById(request.getOrderNumber());
            return OrderMapper.mapOrderToOrderResponse(updated);
        });
    }

    public OrderResponse createOrder(OrderRequest request) {
        return tx.inTransaction(() -> {
            List<Table> types = tableRepository.findAll();
            List<Order> collide = orderRepository.findOrdersCollideByDateTime(request.getOrderDateTime());
            Order newOrder = OrderMapper.mapNewOrderRequestToOrder(request);
            TreeMap<Integer, Integer> available = new TreeMap<>();
            for (Table t : types) {
                available.merge(t.getCapacity(), t.getQuantity(), Integer::sum);
            }

            Map<LocalDateTime, List<Order>> starts = new HashMap<>();
            for (Order o : collide) {
                starts.computeIfAbsent(o.getOrderDateTime(), k -> new ArrayList<>()).add(o);
            }
            Map<Integer, Integer> assignedCap = new HashMap<>();
            LocalDateTime from = request.getOrderDateTime().minusHours(2);
            LocalDateTime to = request.getOrderDateTime().plusHours(2);
            for (LocalDateTime t = from; t.isBefore(to); t.plusMinutes(30)) {
                // Free orders that ended now (started exactly 2h ago)
                LocalDateTime endedStart = t.minusHours(2);
                for (Order o : starts.getOrDefault(endedStart, List.of())) {
                    Integer cap = assignedCap.remove(o.getOrderNumber());
                    if (cap != null) available.put(cap, available.get(cap) + 1);
                }

                // Assign tables to orders starting now
                List<Order> startingNow = new ArrayList<>(starts.getOrDefault(t, List.of()));
                startingNow.sort(Comparator.comparingInt(Order::getNumberOfGuests).reversed());

                for (Order o : startingNow) {
                    int g = o.getNumberOfGuests();

                    Integer cap = available.ceilingKey(g);
                    while (cap != null && available.get(cap) == 0) {
                        cap = available.higherKey(cap);
                    }
                    if (cap == null) return null;

                    available.put(cap, available.get(cap) - 1);
                    assignedCap.put(o.getOrderNumber(), cap);
                }
            }
            return OrderMapper.mapOrderToOrderResponse(orderRepository.save(newOrder));
        });


    }


}