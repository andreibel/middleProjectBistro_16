package com.andreibel.server.controller;

import com.andreibel.message.Message;
import com.andreibel.server.BistroServerGUIController;
import com.lloseng.ocsf.server.AbstractServer;
import com.lloseng.ocsf.server.ConnectionToClient;

import java.io.IOException;

public class Serve extends AbstractServer {
    OrderController orderController;
    SubscriberController subscriberController;
    private BistroServerGUIController controller;

    public Serve(int port) {
        super(port);
        orderController = OrderController.getInstance();
        subscriberController = SubscriberController.getInstance();

    }

    public void setGUIController(BistroServerGUIController controller) {
        this.controller = controller;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (!(msg instanceof Message message)) throw new IllegalArgumentException("Invalid message type");
            Message response = switch (message.getType()) {
                // orders calls
                case GET_ALL_ORDERS -> orderController.getAllOrders();
                case UPDATE_ORDER -> orderController.updateOrder(message);
                case CREATE_ORDER -> orderController.createOrder(message);
                case GET_ONE_ORDER -> orderController.getOrder(message);
                case DELETE_ORDER -> orderController.deleteOrder(message);
                // workers calls
                case LOGIN_WORKER -> null; //TODO: implement login for workers
                // subscribers calls
                case GET_ALL_SUBSCRIBERS -> subscriberController.getAllSub();
                case GET_ONE_SUBSCRIBER -> subscriberController.getSub(message);
                case GET_SUBSCRIBER_ORDERS -> subscriberController.getSubOrders(message);
                case CREATE_SUBSCRIBER -> subscriberController.createSub(message);

                default -> null;
            };

            client.sendToClient(response);


        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        controller.addNewConnection(client);
    }

    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        controller.editConnection(client);
    }

    @Override
    protected void clientException(ConnectionToClient client, Throwable exception) {
        System.out.println("Client exception: " + client.getId() + " - " + exception.getMessage());
        controller.editConnection(client);
    }

}