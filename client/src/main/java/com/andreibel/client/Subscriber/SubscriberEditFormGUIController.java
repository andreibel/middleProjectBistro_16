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
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

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
    @FXML
    private AnchorPane rootPane;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        SubscriberResponse currentSubscriber = CustomerStateManager.getInstance().getSubscriber();
        if (currentSubscriber != null) {
            updateFields(currentSubscriber);
        }

        requestSubscriberDetailsWhenSceneIsShown();
    }

    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case UPDATE_SUBSCRIBER_RESPONSE -> {
                SubscriberResponse updated = (SubscriberResponse) message.getData();
                if (updated != null) {
                    CustomerStateManager.getInstance().setSubscriber(updated);
                    updateFields(updated);
                    BistroUtilities.showMessage("Bistro Restaurant", "Your information has been updated.");
                }
            }
            case UPDATE_SUBSCRIBER_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Failed to update subscriber info due to server error."
            );
        }
    }

    @FXML
    private void onButtonApplyChangesClicked(ActionEvent event) {
        String name = txtFieldName.getText();
        String email = txtFieldEmail.getText();
        String phone = txtFieldPhoneNumber.getText();

        if (name == null || name.isBlank()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a full name.");
            return;
        }
        if (email == null || email.isBlank()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter an email.");
            return;
        }
        if (phone == null || phone.isBlank()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a phone number.");
            return;
        }
        if (!BistroUtilities.isValidFullName(name)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid full name.");
            return;
        }
        if (!BistroUtilities.isValidEmail(email)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid email.");
            return;
        }
        if (!BistroUtilities.isValidPhoneNumber(phone)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid phone number.");
            return;
        }

        SubscriberResponse currentSubscriber = CustomerStateManager.getInstance().getSubscriber();
        if (currentSubscriber != null) {
            controller.requestSubscriberUpdateDetails(
                    new SubscriberRequest(currentSubscriber.getSubscriberId(), email, name, phone)
            );
        }
    }

    @FXML
    private void onBtnGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Subscriber/SubscriberZoneForm.fxml",
                "Bistro Restaurant - Subscriber Area"
        );
    }

    private void requestSubscriberDetailsWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            SubscriberResponse subscriber = CustomerStateManager.getInstance().getSubscriber();
                            if (subscriber != null) updateFields(subscriber);
                        });
                    }
                });
            }
        });
    }

    private void clearForm() {
        txtFieldName.clear();
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
    }

    private void updateFields(SubscriberResponse response) {
        if (response == null) return;
        txtFieldName.setText(response.getName() != null ? response.getName() : "");
        txtFieldEmail.setText(response.getEmail() != null ? response.getEmail() : "");
        txtFieldPhoneNumber.setText(response.getPhoneNumber() != null ? response.getPhoneNumber() : "");
    }
}
