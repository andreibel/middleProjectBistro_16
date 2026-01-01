package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

//TODO: Finish onServerResponse

public class SubscriberEditFormGUIController implements IServerResponseListener {
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private TextField txtFieldName;
    @FXML
    private Button btnApplyChanges;
    @FXML
    private Button btnGoBack;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        clearForm();
    }

    @Override
    public void onServerResponse(Message message) {
        if (message.getType() == APICallType.UPDATE_SUBSCRIBER_RESPONSE){
            updateFields((SubscriberResponse)message.getData());
            BistroUtilities.showMessage("Bistro Restaurant", "Your information has been updated.");
        }

        else if (message.getType() == APICallType.UPDATE_SUBSCRIBER_ERROR)
            BistroUtilities.showMessage("Bistro Restaurant", "Failed to update subscriber info due to server error");
    }
    @FXML
    private void onButtonApplyChangesClicked(ActionEvent event) {
        if (txtFieldName.getText().isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a full name");
            return;
        }
        if (txtFieldEmail.getText().isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter an email");
            return;
        }
        if (txtFieldPhoneNumber.getText().isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a phone number");
            return;
        }
        if (!BistroUtilities.isValidFullName(txtFieldName.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid full name");
            return;
        }
        if (!BistroUtilities.isValidEmail(txtFieldEmail.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid email");
            return;
        }
        if (!BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid phone number");
            return;
        }
        controller.requestSubscriberUpdateDetails(new SubscriberRequest(CustomerStateManager.getInstance().getSubscriber().getSubscriberId(), txtFieldEmail.getText(), txtFieldName.getText(), txtFieldPhoneNumber.getText()));
    }

    @FXML
    private void onBtnGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Subscriber/SubscriberZoneForm.fxml", "Bistro Restaurant - Subscriber Area");
    }

    private void clearForm(){
        txtFieldName.clear();
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
    }

    private void updateFields(SubscriberResponse response){
        txtFieldName.setText(response.getName());
        txtFieldEmail.setText(response.getEmail());
        txtFieldPhoneNumber.setText(response.getPhoneNumber());
    }

}
