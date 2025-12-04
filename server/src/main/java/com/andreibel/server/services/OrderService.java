package com.andreibel.server.services;

import com.andreibel.server.dbController.JDBCConnector;
import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.entity.Order;
import com.andreibel.server.utils.Mapper;
import message.DTO.OrderRequest;
import message.DTO.OrderResponse;

import java.util.List;

public class OrderService {
    private static OrderService instance;
    private final OrderRepository orderRepository;
    private final JDBCConnector connector;

    private OrderService() {
        this.orderRepository = OrderRepository.getInstance();
        this.connector = JDBCConnector.getInstance();
    }

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public List<OrderResponse> getAllOrders() {
    	var orders = orderRepository.getAllOrders();
        return orders.stream().map(Mapper::mapOrderToOrderResponse).toList();
    }

    public OrderResponse updateOrder(OrderRequest orderRequest) {
        var SavedOrder = orderRepository.editOrder(orderRequest);
        return Mapper.mapOrderToOrderResponse(SavedOrder);
    }
}