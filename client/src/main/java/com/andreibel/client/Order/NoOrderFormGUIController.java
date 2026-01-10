package com.andreibel.client.Order;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.WaitingListRequest;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;

public class NoOrderFormGUIController implements IServerResponseListener {

    @FXML public VBox EmailPhoneVBOX;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private TextField txtFieldNumberOfPeople;

    private BistroClientController controller;

    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        adjustElementsBasedOnUserType();

    }

    @Override
    public void onServerResponse(Message message) {
        if (message.getType() == APICallType.ADD_TO_WAITING_LIST_RESPONSE) {
            WaitingListResponse response = (WaitingListResponse) message.getData();
            clearForm();
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Successfully added to waiting list!\n" +
                            "Your confirmation code is: " + response.getConformationCode() +
                            "\nWe'll notify you when a table becomes available."
            );
        } else if (message.getType() == APICallType.ADD_TO_WAITING_LIST_ERROR) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to a server error, we were unable to add you to the waiting list. Please try again."
            );
        }
    }

    @FXML
    private void onSubmitButtonClicked(ActionEvent event) {
        // Validate number of people
        String peopleText = txtFieldNumberOfPeople.getText();
        if (peopleText.isEmpty() || !BistroUtilities.isNumeric(peopleText)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid number of people.");
            return;
        }
        int numberOfPeople = Integer.parseInt(peopleText);
        if (numberOfPeople <= 0) {
            BistroUtilities.showMessage("Bistro Restaurant", "Number of people must be greater than zero.");
            return;
        }

        // Guest validation (subscribers bypass this)
        if (CustomerStateManager.getInstance().getSubscriber() == null) {
            boolean emailEmpty = txtFieldEmail.getText().isEmpty();
            boolean phoneEmpty = txtFieldPhoneNumber.getText().isEmpty();

            if (emailEmpty && phoneEmpty) {
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter either email or phone number.");
                return;
            }
            if (!emailEmpty && !BistroUtilities.isValidEmail(txtFieldEmail.getText())) {
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid email address.");
                return;
            }
            if (!phoneEmpty && !BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())) {
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid phone number.");
                return;
            }
        }

        // Send waiting list request
        controller.requestDiningWithoutOrder(new WaitingListRequest(
                null,
                numberOfPeople,
                CustomerStateManager.fillSubscriberIDDetails(),
                txtFieldEmail.getText(),
                txtFieldPhoneNumber.getText()
        ));
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Main/MainForm.fxml", "Bistro Restaurant");
    }

    private void clearForm() {
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        txtFieldNumberOfPeople.clear();
    }

    private void adjustElementsBasedOnUserType() {
        boolean isSubscriber = CustomerStateManager.getInstance().getSubscriber() != null;

        EmailPhoneVBOX.setVisible(!isSubscriber);
        EmailPhoneVBOX.setManaged(!isSubscriber);
    }


}
