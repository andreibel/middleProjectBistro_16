package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

//TODO: Need to finish OnServerResponse

import java.io.IOException;

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
        if (message.getType() == APICallType.UPDATE_ORDER_RESPONSE){
            BistroUtilities.showMessage("Bistro Restaurant - Leave Table", "Thank you for dining at Bistro Restaurant, See you soon!");
            return;
        }
//        if (message.getType() == APICallType.TABLE_TIMES_UP_RESPONSE){
//            BistroUtilities.showMessage("Bistro Restaurant", "Hey! your times is up, please pay");
//        }

    }
    @FXML
    private void onButtonPayClicked(ActionEvent event) throws IOException {
        controller.requestUpdateOrderStatus(new OrderRequest());
        BistroUtilities.switchScreen((Node) event.getSource(),"/Main/MainForm.fxml","Bistro Restaurant");
    }
}

//Message if 2 hours has passed: Your time is up! Thank you for dining at Bistro Restaurant!