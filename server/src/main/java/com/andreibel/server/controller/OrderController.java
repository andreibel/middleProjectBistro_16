package com.andreibel.server.controller;

import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.Message;
import com.andreibel.server.services.OrderService;

public class OrderController {

    private static OrderController instance;
    private final OrderService orderService;

    private OrderController() {
        orderService = OrderService.getInstance();
    }

    public static OrderController getInstance() {
        if (instance == null) instance = new OrderController();
        return instance;
    }


    public Message createOrder(Message message) {
        return new Message(APICallType.CREATE_ORDER_RESPONSE,orderService.createOrder((OrderRequest) message.getData()));
    }
    public Message getOrder(Message message) {
        return new Message(APICallType.GET_ONE_ORDER_RESPONSE,
                orderService.getOrderByConformationCode(((OrderRequest)message.getData())));
    }

    public Message getAllOrders() {
        return new Message(APICallType.GET_ALL_ORDERS_RESPONSE, orderService.getAllOrders());
    }
    public Message updateOrder(Message message) {
        return new Message(APICallType.UPDATE_ORDER_RESPONSE, orderService.updateOrder((OrderRequest) message.getData()));
    }


    public Message deleteOrder(Message message) {
        orderService.deleteOrder((OrderRequest)message.getData());
        return new Message(APICallType.DELETE_ORDER_RESPONSE, "Order deleted!");
    }
}
