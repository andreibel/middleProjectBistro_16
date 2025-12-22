package com.andreibel.server.controller;

import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.Message;
import com.andreibel.server.BistroServerGUIController;
import com.andreibel.server.services.OrderService;
import com.lloseng.ocsf.server.AbstractServer;
import com.lloseng.ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.time.LocalDateTime;

public class Serve extends AbstractServer {
    OrderService orderService;
    private BistroServerGUIController controller;

    public Serve(int port) {
        super(port);
        orderService = OrderService.getInstance();


    }

    public void setGUIController(BistroServerGUIController controller) {
        this.controller = controller;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (!(msg instanceof Message message)) throw new IllegalArgumentException("Invalid message type");
            APICallType responseType = APICallType.ERROR;
            Object response = switch (message.getType()) {
                case GET_ORDERS -> {
                    responseType = APICallType.GET_ORDER_RESPONSE;
                    yield orderService.getAllOrders();
                }
                case UPDATE_ORDER -> {
                    System.out.println("Update order");
                    responseType = APICallType.UPDATE_ORDER_RESPONSE;
                    yield orderService.updateOrder((OrderRequest) message.getData());
                }
                default -> null;
            };

            client.sendToClient(new Message(responseType, response));


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