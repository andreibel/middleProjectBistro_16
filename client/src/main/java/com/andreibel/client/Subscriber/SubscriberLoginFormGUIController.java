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
    }

    @Override
    public void onServerResponse(Message message) throws IOException {
        if (message.getType() != APICallType.GET_ONE_SUBSCRIBER_RESPONSE)
            return;
        //NEED TO ADD NEW APICallType SUBSCRIBER_LOGIN_ERROR
        else if (message.getType() == APICallType.ERROR){
            BistroUtilities.showMessage("Login Error", "Either the username or password is incorrect.");
            return;
        }
        CustomerStateManager.getInstance().setSubscriber(((SubscriberResponse)message.getData()));
        BistroUtilities.switchScreen(btnLogin, "/Subscriber/SubscriberZoneForm.fxml", "Bistro - Subscriber Zone");
    }

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        controller.requestSubscriberLogin(Integer.parseInt(txtFieldSubscriberId.getText()));
    }
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) {}

}
