package com.andreibel.client.Order;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.UUID;

/**
 * GUI controller for the order cancellation form.
 *
 * <p>This controller allows users to cancel an existing order by providing
 * a confirmation code.</p>
 *
 * <p>The controller sends a cancellation request to the server via
 * {@link BistroClientController} and listens for
 * {@link APICallType#DELETE_ORDER_RESPONSE} messages.</p>
 *
 * <p>Upon receiving a successful cancellation response, the form is cleared
 * and a confirmation message is displayed to the user.</p>
 */
public class CancelOrderFormGUIController implements IServerResponseListener {

    /**
     * Text field for entering the order confirmation code.
     */
    @FXML
    private TextField txtFieldConfirmationCode;

    /**
     * Singleton controller responsible for client-server communication.
     */
    private BistroClientController controller;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>This method retrieves the singleton instance of
     * {@link BistroClientController} and registers this controller
     * as a server response listener.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    /**
     * Handles server responses related to order cancellation requests.
     *
     * <p>This method processes only messages related to order cancellation,
     * specifically {@link APICallType#DELETE_ORDER_RESPONSE} and
     * {@link APICallType#DELETE_ORDER_ERROR}. All other message types are ignored.</p>
     *
     * <p>If the cancellation is successful, the confirmation code input field
     * is cleared and a success message is displayed to the user.</p>
     *
     * <p>If the cancellation fails due to a server-side error, an error message
     * is shown informing the user that the order could not be canceled.</p>
     *
     * @param message the message received from the server
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case DELETE_ORDER_RESPONSE ->{
                txtFieldConfirmationCode.clear();
                BistroUtilities.showMessage(
                        "Bistro Restaurant - Order Cancellation",
                        "Your order has been successfully canceled."
                );
            }
            case DELETE_ORDER_ERROR ->
                BistroUtilities.showMessage(
                        "Bistro Restaurant - Cancellation Failed",
                        "Due to a server error, your order could not be canceled. Please contact the staff for assistance."
                );
        }
    }


    /**
     * Sends an order cancellation request to the server.
     *
     * <p>The confirmation code must be provided and contain only numeric
     * characters.</p>
     *
     * @param event the action event triggered by clicking the cancel button
     */
    @FXML
    private void onCancelOrderButtonClicked(ActionEvent event) {
        if (txtFieldConfirmationCode.getText().isEmpty()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant - Order Cancellation Failed",
                    "No confirmation code provided."
            );
            return;
        }

        if (!BistroUtilities.isNumeric(txtFieldConfirmationCode.getText())) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant - Order Cancellation Failed",
                    "The confirmation code provided is not a valid number."
            );
            return;
        }

        controller.requestOrderCancel(
                UUID.fromString(txtFieldConfirmationCode.getText())
        );
    }

    /**
     * Navigates back to the main form.
     *
     * @param event the action event triggered by clicking the back button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }
}
