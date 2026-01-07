package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

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
    @FXML private AnchorPane rootPane;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        clearForm();
    }

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

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    private void clearForm() {
        txtFieldName.clear();
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
    }
}
