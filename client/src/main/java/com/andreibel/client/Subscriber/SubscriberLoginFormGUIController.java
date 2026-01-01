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

//TODO: Finish onServerResponse

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
        if (message.getType() == APICallType.SUBSCRIBER_LOGIN_RESPONSE){
            txtFieldSubscriberId.clear();
            CustomerStateManager.getInstance().setSubscriber(((SubscriberResponse)message.getData()));
            BistroUtilities.switchScreen(btnLogin, "/Main/MainForm.fxml", "Bistro Restaurant");
        }
        else if (message.getType() == APICallType.SUBSCRIBER_LOGIN_ERROR)
            BistroUtilities.showMessage("Bistro Restaurant", "Either the username or password is incorrect.");
    }

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        if (txtFieldSubscriberId.getText().isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid username.");
            return;
        }
        if (!BistroUtilities.isNumeric(txtFieldSubscriberId.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid username.");
            return;
        }
        controller.requestSubscriberLogin(Integer.parseInt(txtFieldSubscriberId.getText()));
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
