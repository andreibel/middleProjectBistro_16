package com.andreibel.client.Worker;

import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class RegisteredSubscribersFormGUIController implements IServerResponseListener {

    @FXML
    private Button btnGoBack;

    @FXML
    public void initialize() {

    }

    @Override
    public void onServerResponse(Message message) {

    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) {

    }
}
