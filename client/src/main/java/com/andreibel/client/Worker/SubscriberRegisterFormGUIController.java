package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * GUI controller for registering new subscribers in the Bistro system.
 *
 * <p>This form allows staff to input subscriber details (name, email, phone number),
 * validate them, and submit a request to the server. Feedback messages are displayed
 * upon success or failure.</p>
 */
public class SubscriberRegisterFormGUIController implements IServerResponseListener {

    /** TextField for entering subscriber name. */
    @FXML
    private TextField txtFieldName;

    /** TextField for entering subscriber email. */
    @FXML
    private TextField txtFieldEmail;

    /** TextField for entering subscriber phone number. */
    @FXML
    private TextField txtFieldPhoneNumber;

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /**
     * Initializes the controller after FXML is loaded.
     *
     * <p>Registers this controller as a server listener and clears the input form.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        clearForm();
    }

    /**
     * Handles responses from the server related to subscriber creation.
     *
     * @param message the server message
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case CREATE_SUBSCRIBER_RESPONSE -> {
                clearForm();
                BistroUtilities.showMessage("Bistro Restaurant", "Successfully registered new subscriber!");
            }
            case CREATE_SUBSCRIBER_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to register new subscriber.");
        }
    }

    /**
     * Handles the Register button click.
     *
     * <p>Validates the input fields and sends a registration request to the server
     * if all inputs are valid.</p>
     *
     * @param event the action event
     */
    @FXML
    private void onRegisterButtonClicked(ActionEvent event) {
        String name = txtFieldName.getText();
        String email = txtFieldEmail.getText();
        String phone = txtFieldPhoneNumber.getText();

        if (name.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter subscriber name.");
            return;
        }
        if (email.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter subscriber email.");
            return;
        }
        if (phone.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter phone number.");
            return;
        }

        if (!BistroUtilities.isValidFullName(name)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid subscriber name.");
            return;
        }
        if (!BistroUtilities.isValidEmail(email)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid subscriber email.");
            return;
        }
        if (!BistroUtilities.isValidPhoneNumber(phone)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid phone number.");
            return;
        }

        controller.requestRegisterNewSubscriber(new SubscriberRequest(null, email, name, phone));
    }

    /**
     * Handles the Go Back button click.
     *
     * <p>Clears the form and navigates back to the staff main screen.</p>
     *
     * @param event the action event
     * @throws IOException if FXML cannot be loaded
     */
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearForm() {
        txtFieldName.clear();
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
    }
}