package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

//TODO Finish onServerResponse

public class SubscriberRegisterFormGUIController implements IServerResponseListener {
    @FXML
    private Label lblName;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private TextField txtFieldName;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private Button btnRegister;
    @FXML
    private Button btnGoBack;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        clearForm();
    }
    //NEED TO ADD CREATE_SUBSCRIBER_ERROR APICallType
    @Override
    public void onServerResponse(Message message) {
        if (message.getType() != APICallType.CREATE_SUBSCRIBER_RESPONSE)
            return;
//        if (message.getType() != APICallType.CREATE_SUBSCRIBER_ERROR)
//            return;
        BistroUtilities.showMessage("Bistro Restaurant - Subscriber Registration", "Successfully registered new subscriber!");
    }

    @FXML
    private void onRegisterButtonClicked(ActionEvent event) {
        if (txtFieldName.getText().isEmpty()){
            BistroUtilities.showMessage("Bistro Restaurant - Subscriber Registration", "Please enter subscriber name.");
            return;
        }
        if (txtFieldEmail.getText().isEmpty()){
            BistroUtilities.showMessage("Bistro Restaurant - Subscriber Registration", "Please enter subscriber email.");
            return;
        }
        if (txtFieldPhoneNumber.getText().isEmpty()){
            BistroUtilities.showMessage("Bistro Restaurant - Subscriber Registration", "Please enter phone number.");
            return;
        }

        if (!BistroUtilities.isValidFullName(txtFieldName.getText())){
            BistroUtilities.showMessage("Bistro Restaurant - Subscriber Registration", "Please enter a valid subscriber name.");
            return;
        }
        if (!BistroUtilities.isValidEmail(txtFieldEmail.getText())){
            BistroUtilities.showMessage("Bistro Restaurant - Subscriber Registration", "Please enter a valid subscriber email.");
            return;
        }

        if (!BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())){
            BistroUtilities.showMessage("Bistro Restaurant - Subscriber Registration", "Please enter a valid phone number.");
            return;
        }
        controller.requestRegisterNewSubscriber(new SubscriberRequest(null, txtFieldEmail.getText(), txtFieldName.getText(), txtFieldPhoneNumber.getText()));

    }
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    private void clearForm(){
        txtFieldName.clear();
        txtFieldEmail.clear();
        lblName.setText("");
    }
}
