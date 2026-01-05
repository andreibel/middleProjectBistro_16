package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SubscriberLoginFormGUIController implements IServerResponseListener {

    @FXML
    private TextField txtFieldSubscriberId;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnGoBack;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        txtFieldSubscriberId.clear();
    }

    @Override
    public void onServerResponse(Message message) throws IOException {
        switch (message.getType()) {
            case SUBSCRIBER_LOGIN_RESPONSE -> {
                SubscriberResponse subscriber = (SubscriberResponse) message.getData();
                if (subscriber != null) {
                    CustomerStateManager.getInstance().setSubscriber(subscriber);
                    txtFieldSubscriberId.clear();
                    BistroUtilities.switchScreen(btnLogin, "/Main/MainForm.fxml", "Bistro Restaurant");
                }
            }
            case SUBSCRIBER_LOGIN_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Subscriber ID is incorrect. Please try again."
            );
        }
    }

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        String input = txtFieldSubscriberId.getText();
        if (input == null || input.isBlank()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter your subscriber ID.");
            return;
        }

        if (!BistroUtilities.isNumeric(input)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Subscriber ID must be numeric.");
            return;
        }

        controller.requestSubscriberLogin(Integer.parseInt(input));
    }

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
