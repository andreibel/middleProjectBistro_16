package com.andreibel.client.Client;

import com.andreibel.message.Message;
import com.lloseng.ocsf.client.AbstractClient;

import java.io.IOException;

public class BistroClient extends AbstractClient {

    private BistroClientController controller;

    public BistroClient(String host, int port) {
        super(host, port);
    }

    public void setController(BistroClientController controller) {
        this.controller = controller;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (controller == null) {
            return;
        }

        if (msg instanceof Message m) {
            controller.handleServerResponse(m);
        } else {
            controller.showError("Unknown message from server: " + msg);
        }
    }

    public void send(Object msg) {
        try {
            sendToServer(msg);
        } catch (IOException e) {
            if (controller != null) {
                controller.showError("Failed to send to server: " + e.getMessage());
            }
        }
    }

    public void connectToServer() {
        try {
            openConnection();
        } catch (IOException e) {
            if (controller != null) {
                controller.showError("Failed to connect to server: " + e.getMessage());
            }
        }
    }
}