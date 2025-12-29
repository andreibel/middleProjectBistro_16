package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class GetTableFormGUIController implements IServerResponseListener{

    @FXML
    private Label lblSubscriberInfo;
    @FXML
    private Label lblWhoAmI;
    @FXML
    private RadioButton radioBtnGuest;
    @FXML
    private RadioButton radioBtnSubscriber;
    @FXML
    private TextField txtFieldConfirmation;
    @FXML
    private Label lblOR;
    @FXML
    private Label lblSubscriberID;
    @FXML
    private TextField txtFieldSubscriberID;
    @FXML
    private Button btnConfrimArrival;
    @FXML
    private Button btnGoBack;
    @FXML
    private Button btnLostMyCode;

    BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message) {

    }
    @FXML
    private void onButtonConfirmArrivalClicked(ActionEvent event) {}
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) {}

}
