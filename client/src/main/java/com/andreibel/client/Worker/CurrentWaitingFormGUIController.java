package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.Message;
import javafx.fxml.FXML;

public class CurrentWaitingFormGUIController implements IServerResponseListener {

    private BistroClientController controller;
    @FXML
    public void intialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message) {}
}
