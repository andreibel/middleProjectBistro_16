package com.andreibel.client.Client;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.Message;
import com.lloseng.ocsf.client.AbstractClient;
import lombok.Setter;

import java.io.IOException;

@Setter
public class BistroClient extends AbstractClient {

    private BistroClientController controller;

    public BistroClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (controller == null) {
            return;
        }
        if (msg instanceof Message m) {
            try {
                controller.handleServerResponse(m);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            BistroUtilities.showMessage("Error", "Unknown message from server: " + msg);
        }
    }

    public void send(Object msg) {
        try {
            sendToServer(msg);
        } catch (IOException e) {
            if (controller != null) {
                BistroUtilities.showMessage("Error", "Failed to send to server: " + e.getMessage());
            }
        }
    }

    public void connectToServer() {
        try {
            openConnection();
        } catch (IOException e) {
            if (controller != null) {
                BistroUtilities.showMessage("Error", "Failed to connect to server: " + e.getMessage());
            }
        }
    }
}