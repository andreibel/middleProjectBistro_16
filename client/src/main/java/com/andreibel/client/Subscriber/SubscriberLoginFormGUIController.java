package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller for the subscriber login form.
 *
 * <p>This form allows a subscriber to log in using their numeric
 * subscriber ID. Upon successful login, the subscriber data is
 * stored in {@link CustomerStateManager}, and the main form
 * screen is displayed.</p>
 */
public class SubscriberLoginFormGUIController implements IServerResponseListener {

    /** Input field for subscriber ID */
    @FXML private TextField txtFieldSubscriberId;

    /** Action buttons */
    @FXML private Button btnLogin;
    @FXML private Button btnGoBack;

    private BistroClientController controller;

    /**
     * Initializes the controller, registers it as a server listener,
     * and clears the subscriber ID input field.
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        txtFieldSubscriberId.clear();
    }

    /**
     * Handles server responses related to subscriber login attempts.
     *
     * @param message server response message
     * @throws IOException if screen switching fails
     */
    @Override
    public void onServerResponse(Message message) throws IOException {
        switch (message.getType()) {
            case SUBSCRIBER_LOGIN_RESPONSE -> {
                SubscriberResponse subscriber = (SubscriberResponse) message.getData();
                if (subscriber != null) {
                    CustomerStateManager.getInstance().setSubscriber(subscriber);
                    txtFieldSubscriberId.clear();
                    BistroUtilities.switchScreen(
                            btnLogin,
                            "/Main/MainForm.fxml",
                            "Bistro Restaurant"
                    );
                }
            }
            case SUBSCRIBER_LOGIN_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Subscriber ID is incorrect. Please try again."
            );
        }
    }

    /**
     * Validates subscriber ID input and sends a login request.
     *
     * @param event the action event triggered by clicking the login button
     */
    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        String input = txtFieldSubscriberId.getText();
        if (input == null || input.isBlank()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter your subscriber ID."
            );
            return;
        }

        if (!BistroUtilities.isNumeric(input)) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Subscriber ID must be numeric."
            );
            return;
        }

        controller.requestSubscriberLogin(Integer.parseInt(input));
    }

    /**
     * Returns to the main screen without logging in.
     *
     * @param event the action event triggered by clicking the go back button
     * @throws IOException if screen switching fails
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        txtFieldSubscriberId.clear();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }
}