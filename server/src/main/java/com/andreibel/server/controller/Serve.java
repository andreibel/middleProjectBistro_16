package com.andreibel.server.controller;

import com.andreibel.server.entity.Order;
import com.andreibel.server.message.APICallType;
import com.andreibel.server.message.Message;
import com.andreibel.server.services.OrderService;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class Serve extends AbstractServer {
    public static final int PORT = 8080;
    private OrderService orderService;

    /**
     * Constructs a new server.
     *
     * @param port the port number on which to listen.
     */
    public Serve(int port) {
        super(port);
        this.orderService = OrderService.getInstance();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (!(msg instanceof Message)) {
                client.sendToClient("Invalid message format");
                return;
            }

            Message message = (Message) msg;
            APICallType callType = message.getType();
            Object response = null;

            switch (callType) {
                case GET_ORDERS:
                    response = orderService.getAllOrders();
                    break;

                case UPDATE_ORDER:
                    Order order = (Order) message.getData();
                    orderService.updateOrder(order);
                    response = "Order updated successfully";
                    break;

                default:
                    response = "Unknown request type";
            }

            client.sendToClient(response);

        } catch (ClassCastException e) {
            System.err.println("Error casting message data: " + e.getMessage());
            try {
            	client.sendToClient("Error: Invalid data format");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            try {
            	client.sendToClient("Server error: " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}