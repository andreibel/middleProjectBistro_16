package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LeaveTableFormGUIController implements IServerResponseListener {
    @FXML
    private Label lblTitle;
    @FXML
    private Button btnPay;

    BistroClientController controller;
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message) {

    }
    @FXML
    private void onButtonPayClicked(ActionEvent event) {}
}

//Message if 2 hours has passed: Your time is up! Thank you for dining at Bistro Restaurant!