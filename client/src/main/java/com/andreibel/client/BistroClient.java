package com.andreibel.client;
import com.andreibel.client.message.Message;
import ocsf.client.AbstractClient;

public class BistroClient extends AbstractClient {

    private BistroClientController mainUI;

    public BistroClient(String host, int port, BistroClientController mainUI) {
        super(host, port);
        this.mainUI = mainUI;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof Message) {
            //mainUI.handleServerResponse((Message) msg);
        }
    }

    public void send(Object msg) {
        try {
            sendToServer(msg);
        } catch (Exception e) {
            mainUI.showError("Failed to send message: " + e.getMessage());
        }
    }

    public void connectToServer() {
        try {
            openConnection();
        } catch (Exception e) {
            mainUI.showError("Could not connect to server.");
        }
    }
}