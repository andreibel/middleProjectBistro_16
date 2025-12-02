package com.andreibel.server.services;

import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.entity.Order;

import java.util.List;

public class OrderService {
    private static OrderService instance;
    private final OrderRepository orderRepository;

    private OrderService() {
        this.orderRepository = OrderRepository.getInstance();
    }

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public List<OrderResponse> getAllOrders() {
    	List<Order> orders = orderRepository.getAllOrders();
        return orders.stream().map((Order order)->new
        		OrderResponse(order.getOrderNumber()
        				,order.))
    }

    public void updateOrder(Order order) {
        orderRepository.editOrder(order);
    }
}