package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.UUID;

/**
 * Controller for the "Get Table" form.
 *
 * <p>This form allows guests or subscribers to confirm their arrival at the restaurant
 * by entering a confirmation code. The controller also handles navigation to the
 * "Lost My Code" form and back to the main screen.</p>
 */
public class GetTableFormGUIController implements IServerResponseListener {

    /** Text field for entering confirmation code */
    @FXML
    private TextField txtFieldConfirmation;

    /** Singleton client controller for server communication */
    private BistroClientController controller;

    /** Initializes the controller after FXML has been loaded */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    /**
     * Handles responses from the server regarding arrival confirmation.
     *
     * @param message the server message
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case ORDER_ARRIVED_RESPONSE -> {
                CustomerStateManager.getInstance().setArrivedToTable(true);

                // Save confirmation code for guest if entered
                String codeText = txtFieldConfirmation.getText().trim();
                if (!codeText.isEmpty() && BistroUtilities.isNumeric(codeText)) {
                    CustomerStateManager.getInstance().setConfirmationCode(Integer.parseInt(codeText));
                }
                txtFieldConfirmation.clear();
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Thank you for confirming your arrival, please head to your table."
                );
            }
            case ORDER_ARRIVED_ERROR -> {
                if (message.getData() == null)
                    BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Due to server error, it was unable to confirm your arrival. Please contact staff for help."
                    );
                else
                    BistroUtilities.showMessage("Bistro Restaurant", (String)message.getData());
            }
        }
    }

    /**
     * Handles the "Confirm Arrival" button click.
     * Validates guest input and sends confirmation request to the server.
     *
     * @param event the button click event
     */
    @FXML
    private void onButtonConfirmArrivalClicked(ActionEvent event) {
        boolean isGuest = CustomerStateManager.getInstance().getSubscriber() == null;

        if (isGuest && txtFieldConfirmation.getText().isBlank()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Dear Guest, please enter confirmation code.");
            return;
        }

        UUID confirmationUUID = null;
        if (!txtFieldConfirmation.getText().isBlank()) {
            try {
                confirmationUUID = UUID.fromString(txtFieldConfirmation.getText().trim());
            } catch (IllegalArgumentException e) {
                BistroUtilities.showMessage("Bistro Restaurant", "Invalid confirmation code format.");
                return;
            }
        }

        controller.requestArrivalConfirmation(confirmationUUID);
    }

    /**
     * Navigates to the "Lost My Code" form.
     *
     * @param event the button click event
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onLostMyCodeButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Table/LostMyCodeForm.fxml",
                "Bistro Restaurant - Lost My Code"
        );
    }

    /**
     * Navigates back to the main form and clears the confirmation field.
     *
     * @param event the button click event
     * @throws IOException if the FXML file cannot be loaded
     */
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