package com.andreibel.server;

import com.andreibel.server.dbController.repository.OrderRepository;
import com.andreibel.server.entity.Order;
import javafx.application.Application;

import java.util.List;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        OrderRepository orderRepository = OrderRepository.getInstance();
        List<Order> orders = orderRepository.getAllOrders();
        orders.forEach(System.out::println);
        orders.forEach((order) -> order.setNumberOfGuests(order.getNumberOfGuests() + 1));
        orders.forEach(orderRepository::editOrder);
        Application.launch(HelloApplication.class, args);
    }
}
