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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.UUID;

public class GetTableFormGUIController implements IServerResponseListener {
    @FXML
    private TextField txtFieldConfirmation;
    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case ORDER_ARRIVED_RESPONSE -> {
                CustomerStateManager.getInstance().setArrivedToTable(true);
                String codeText = txtFieldConfirmation.getText().trim();
                if (!codeText.isEmpty() && BistroUtilities.isNumeric(codeText)) {
                    CustomerStateManager.getInstance().setConfirmationCode(Integer.parseInt(codeText));
                }
                txtFieldConfirmation.clear();
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Thank you for confirming your arrival, please head to your table"
                );
            }
            case ORDER_ARRIVED_WAITING_RESPONSE -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Thank you for confirming your arrival. Unfortunately, we currently don't have a table ready for you.\n" +
                            "You'll be entering a waiting list and we'll let you know when your table is ready. We apologize for the inconvenience."
            );
            case ORDER_ARRIVED_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, it was unable to confirm your arrival. Please contact staff for help."
            );
        }
    }

    @FXML
    private void onButtonConfirmArrivalClicked(ActionEvent event) {
        boolean isGuest = CustomerStateManager.getInstance().getSubscriber() == null;

        if (isGuest && txtFieldConfirmation.getText().isBlank()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Dear Guest, please enter confirmation code");
            return;
        }

        if (isGuest && !BistroUtilities.isNumeric(txtFieldConfirmation.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid confirmation code");
            return;
        }

        UUID confirmationUUID = null;
        if (!txtFieldConfirmation.getText().isBlank()) {
            try {
                confirmationUUID = UUID.fromString(txtFieldConfirmation.getText().trim());
            }
            catch (IllegalArgumentException e) {
                BistroUtilities.showMessage("Bistro Restaurant", "Invalid confirmation code format");
                return;
            }
        }

        controller.requestArrivalConfirmation(new OrderRequest(
                confirmationUUID,
                null,
                null,
                CustomerStateManager.fillSubscriberIDDetails(),
                null,
                null
        ));
    }

    @FXML
    private void onLostMyCodeButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Table/LostMyCodeForm.fxml",
                "Bistro Restaurant - Lost My Code"
        );
    }

    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        txtFieldConfirmation.clear();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }
}
