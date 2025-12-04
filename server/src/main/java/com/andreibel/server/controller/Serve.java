package com.andreibel.server.controller;

import com.andreibel.server.BistroServerGUIController;
import com.andreibel.server.services.OrderService;
import message.APICallType;
import message.DTO.OrderRequest;
import message.Message;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;

public class Serve extends AbstractServer {
    OrderService instance;
    private BistroServerGUIController controller;

    public Serve(int port) {
        super(port);
        instance = OrderService.getInstance();
    }

    public void setGUIController(BistroServerGUIController controller) {
        this.controller = controller;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            System.out.println(msg);
            if (!(msg instanceof Message message)) throw new IllegalArgumentException("Invalid message type");
            APICallType responseType = APICallType.ERROR;
            Object response = switch (message.getType()) {
                case GET_ORDERS -> {
                    responseType = APICallType.GET_ORDER_RESPONSE;
                    yield instance.getAllOrders();
                }
                case UPDATE_ORDER -> {
                    System.out.println("Update order");
                    responseType = APICallType.UPDATE_ORDER_RESPONSE;
                    yield instance.updateOrder((OrderRequest) message.getData());
                }
                default -> null;
            };
            System.out.println(response);
            Thread.sleep(2000);

            client.sendToClient(new Message(responseType, response));


        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void clientConnected(ConnectionToClient client){
        controller.addNewConnection(client);
    }

    @Override
    protected void clientDisconnected(ConnectionToClient client){
        controller.editConnection(client);
    }
}