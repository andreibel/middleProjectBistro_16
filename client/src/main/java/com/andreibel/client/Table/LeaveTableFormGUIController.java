package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.UUID;

public class LeaveTableFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;
    @FXML
    private Button btnPay;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case COMPLETE_ORDER_RESPONSE -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Thank you for dining at Bistro Restaurant, See you soon!"
            );
            case COMPLETE_ORDER_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, we're unable to process your payment, please contact staff for help"
            );
        }
    }

    @FXML
    private void onButtonPayClicked(ActionEvent event) throws IOException {
        Integer confirmationCode = CustomerStateManager.getInstance().getConfirmationCode();
        if (confirmationCode != null) {
            controller.requestCompleteOrder(UUID.fromString(confirmationCode.toString()));
            BistroUtilities.switchScreen(
                    (Node) event.getSource(),
                    "/Main/MainForm.fxml",
                    "Bistro Restaurant"
            );
        } else {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "No confirmation code found. Please confirm your order first."
            );
        }
    }
}
