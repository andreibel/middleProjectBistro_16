package com.andreibel.server.services;

import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.server.dbController.TransactionManager;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.utils.Mapper;

import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private static OrderService instance;
    private final OrderRepository orderRepository;
    private final TransactionManager tx;

    private OrderService() {
        this.orderRepository = OrderRepository.getInstance();
        this.tx = TransactionManager.getInstance();
    }

    public static OrderService getInstance() {
        if (instance == null) instance = new OrderService();
        return instance;
    }

    public List<OrderResponse> getAllOrders() {
        try {
            return tx.inTransaction(orderRepository::findAll).stream().map(Mapper::mapOrderToOrderResponse).toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderResponse updateOrder(OrderRequest request) {
        try {
            return tx.inTransaction(() -> {
                orderRepository.update(request);
                var updated = orderRepository.findById(request.getOrderNumber());
                return Mapper.mapOrderToOrderResponse(updated);
            });
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order", e);
        }
    }
}