package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
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
import java.util.UUID;

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
        if (message.getType() == APICallType.COMPLETE_ORDER_RESPONSE)
            BistroUtilities.showMessage("Bistro Restaurant", "Thank you for dining at Bistro Restaurant, See you soon!");
        else if (message.getType() == APICallType.COMPLETE_ORDER_ERROR)
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, we're unable to proccess your payment, please contact staff for help");

    }
    @FXML
    private void onButtonPayClicked(ActionEvent event) throws IOException {
        controller.requestCompleteOrder(UUID.fromString(CustomerStateManager.getInstance().getConfirmationCode().toString()));
        BistroUtilities.switchScreen((Node) event.getSource(),"/Main/MainForm.fxml","Bistro Restaurant");
    }
}