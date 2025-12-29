package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class WorkerRegisterFormGUIController implements IServerResponseListener {

    @FXML
    private TextField txtFieldWorkerName;
    @FXML
    private TextField txtFieldPassword;
    @FXML
    private CheckBox chkBoxManager;
    @FXML
    private Button btnRegisterWorker;
    @FXML
    private Button btnGoBack;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message) {

    }
    @FXML
    private void onButtonRegisterWorkerClicked(ActionEvent event) {

    }
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) {

    }
}
