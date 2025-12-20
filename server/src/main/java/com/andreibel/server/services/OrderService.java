package com.andreibel.server.services;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.dbController.repository.TableRepository;
import com.andreibel.server.entity.Order;
import com.andreibel.server.entity.Table;
import com.andreibel.server.utils.Mapper;

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

        return tx.inTransaction(orderRepository::findAll).stream().map(Mapper::mapOrderToOrderResponse).toList();

    }

    public OrderResponse updateOrder(OrderRequest request) {

        return tx.inTransaction(() -> {
            orderRepository.update(request);
            var updated = orderRepository.findById(request.getOrderNumber());
            return Mapper.mapOrderToOrderResponse(updated);
        });

    }

    public OrderResponse createOrder(OrderRequest request) {

        return tx.inTransaction(() -> {
            List<Table> types = tableRepository.findAll();
            List<Order> collide = orderRepository.findOrdersCollideByDateTime(request.getOrderDateTime());
            Order newOrder = Order.builder()
                    .numberOfGuests(request.getNumberOfGuests())
                    .orderDateTime(request.getOrderDateTime())
                    .orderCancelled(false)
                    .orderCompleted(false)
                    .build();
            collide.add(newOrder);

            TreeMap<Integer, Integer> available = new TreeMap<>();
            for (Table t : types) {
                available.merge(t.getCapacity(), t.getQuantity(), Integer::sum);
            }

            // TODO : hash map for orders collide in the same time
            Map<LocalDateTime, List<Order>> starts = new HashMap<>();
            for (Order o : collide) {
                starts.computeIfAbsent(o.getOrderDateTime(), k -> new ArrayList<>()).add(o);
            }
            Map<Integer, Integer> assignedCap = new HashMap<>();
            LocalDateTime from = request.getOrderDateTime().minusHours(2);
            LocalDateTime to   = request.getOrderDateTime().plusHours(2);
            return null;
        });


    }


}