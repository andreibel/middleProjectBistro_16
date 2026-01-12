package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.UUID;

/**
 * Controller for the "Leave Table / Pay Order" form.
 *
 * <p>This form allows guests or subscribers to pay for their order by entering
 * a confirmation code. The controller handles server communication for completing
 * the order and navigates back to the main screen upon success or cancellation.</p>
 */
public class LeaveTableFormGUIController implements IServerResponseListener {

    /** Text field for entering the confirmation code */
    @FXML
    private TextField txtFieldConfirmationCode;

    /** Button to complete the payment */
    @FXML
    private Button btnPay;

    /** Singleton client controller for server communication */
    private BistroClientController controller;

    /** Initializes the controller after FXML has been loaded */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    /**
     * Handles server responses related to completing the order.
     *
     * @param message the server message
     * @throws IOException if switching screens fails
     */
    @Override
    public void onServerResponse(Message message) throws IOException {
        switch (message.getType()) {
            case COMPLETE_ORDER_RESPONSE -> {
                txtFieldConfirmationCode.clear();
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Thank you for dining at Bistro Restaurant. See you soon!\n"
                        + (String)message.getData()
                );
                BistroUtilities.switchScreen(
                        btnPay,
                        "/Main/MainForm.fxml",
                        "Bistro Restaurant"
                );
            }
            case COMPLETE_ORDER_ERROR -> {
                if (message.getData() == null)
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            "Due to server error, we're unable to process your payment. Please contact staff for help."
                    );
                else
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            (String)message.getData()
                    );
            }
        }
    }

    /**
     * Handles the "Pay" button click.
     * Validates the confirmation code and requests order completion from the server.
     *
     * @param event the button click event
     */
    @FXML
    private void onButtonPayClicked(ActionEvent event) {
        try {
            controller.requestCompleteOrder(UUID.fromString(txtFieldConfirmationCode.getText()));
        } catch (IllegalArgumentException e) {
            BistroUtilities.showMessage("Bistro Restaurant", "Confirmation code is not valid.");
        }
    }

    /**
     * Navigates back to the main form and clears the confirmation field.
     *
     * @param event the button click event
     * @throws IOException if the FXML file cannot be loaded
     */
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