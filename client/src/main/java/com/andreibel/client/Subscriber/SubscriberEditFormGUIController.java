package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
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

/**
 * Controller for editing subscriber personal information.
 *
 * <p>This screen allows a subscriber to update their name, email,
 * and phone number. The controller validates input and sends update
 * requests to the server.</p>
 */
public class SubscriberEditFormGUIController
        implements IServerResponseListener {

    /** Subscriber input fields */
    @FXML private TextField txtFieldEmail;
    @FXML private TextField txtFieldPhoneNumber;
    @FXML private TextField txtFieldName;

    /** Action buttons */
    @FXML private Button btnApplyChanges;
    @FXML private Button btnGoBack;

    /** Root pane for scene detection */
    @FXML private AnchorPane rootPane;

    private BistroClientController controller;

    /**
     * Initializes the controller, registers it as a server listener,
     * and loads subscriber data when the scene is shown.
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        requestSubscriberDetailsWhenSceneIsShown();
    }

    /**
     * Handles server responses related to subscriber updates.
     *
     * @param message server response message
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case UPDATE_SUBSCRIBER_RESPONSE -> {
                SubscriberResponse updated =
                        (SubscriberResponse) message.getData();
                if (updated != null) {
                    CustomerStateManager.getInstance()
                            .setSubscriber(updated);
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            "Your information has been updated."
                    );
                }
            }
            case UPDATE_SUBSCRIBER_ERROR ->
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            "Failed to update subscriber info due to server error."
                    );
        }
    }

    /**
     * Validates user input and sends a subscriber update request.
     */
    @FXML
    private void onButtonApplyChangesClicked(ActionEvent event) {
        String name = txtFieldName.getText();
        String email = txtFieldEmail.getText();
        String phone = txtFieldPhoneNumber.getText();

        if (name == null || name.isBlank()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter a full name."
            );
            return;
        }
        if (email == null || email.isBlank()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter an email."
            );
            return;
        }
        if (phone == null || phone.isBlank()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter a phone number."
            );
            return;
        }
        if (!BistroUtilities.isValidFullName(name)) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter a valid full name."
            );
            return;
        }
        if (!BistroUtilities.isValidEmail(email)) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter a valid email."
            );
            return;
        }
        if (!BistroUtilities.isValidPhoneNumber(phone)) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter a valid phone number."
            );
            return;
        }

        SubscriberResponse currentSubscriber =
                CustomerStateManager.getInstance().getSubscriber();

        if (currentSubscriber != null) {
            controller.requestSubscriberUpdateDetails(
                    new SubscriberRequest(
                            currentSubscriber.getSubscriberId(),
                            email,
                            name,
                            phone
                    )
            );
        }
    }

    /**
     * Returns to the subscriber main area without saving changes.
     */
    @FXML
    private void onBtnGoBackClicked(ActionEvent event)
            throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Subscriber/SubscriberZoneForm.fxml",
                "Bistro Restaurant - Subscriber Area"
        );
    }

    /**
     * Loads subscriber details when the scene becomes visible.
     */
    private void requestSubscriberDetailsWhenSceneIsShown() {
        rootPane.sceneProperty().addListener(
                (observable, oldScene, newScene) -> {
                    if (newScene != null) {
                        newScene.windowProperty().addListener(
                                (obs, oldWindow, newWindow) -> {
                                    if (newWindow != null) {
                                        SubscriberResponse subscriber =
                                                CustomerStateManager
                                                        .getInstance()
                                                        .getSubscriber();
                                        if (subscriber != null) {
                                            updateFields(subscriber);
                                        }
                                    }
                                }
                        );
                    }
                }
        );
    }

    /**
     * Clears all input fields.
     */
    private void clearForm() {
        txtFieldName.clear();
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
    }

    /**
     * Populates input fields with subscriber data.
     *
     * @param response subscriber details
     */
    private void updateFields(SubscriberResponse response) {
        if (response == null) return;
        txtFieldName.setText(
                response.getName() != null ? response.getName() : ""
        );
        txtFieldEmail.setText(
                response.getEmail() != null ? response.getEmail() : ""
        );
        txtFieldPhoneNumber.setText(
                response.getPhoneNumber() != null
                        ? response.getPhoneNumber()
                        : ""
        );
    }
}