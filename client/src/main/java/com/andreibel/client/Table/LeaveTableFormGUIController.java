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
import javafx.scene.control.TextField;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.UUID;

public class LeaveTableFormGUIController implements IServerResponseListener {

    @FXML
    private TextField txtFieldConfirmationCode;
    @FXML
    private Button btnPay;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message) throws IOException {
        switch (message.getType()) {
            case COMPLETE_ORDER_RESPONSE -> {
                BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Thank you for dining at Bistro Restaurant, See you soon!"
                );
                BistroUtilities.switchScreen(
                        btnPay,
                        "/Main/MainForm.fxml",
                        "Bistro Restaurant"
                );
            }
            case COMPLETE_ORDER_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, we're unable to process your payment, please contact staff for help"
            );
        }
    }

    @FXML
    private void onButtonPayClicked(ActionEvent event) throws IOException {
        try { controller.requestCompleteOrder(UUID.fromString(txtFieldConfirmationCode.getText())); }
        catch (IllegalArgumentException e) {
            BistroUtilities.showMessage("Bistro Restaurant", "Confirmation code is not valid.");
        }
    }

    @FXML
    private void onGoBackButtonClick(ActionEvent event) throws IOException {
        txtFieldConfirmationCode.clear();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }
}
