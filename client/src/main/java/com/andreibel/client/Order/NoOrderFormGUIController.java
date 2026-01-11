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
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * GUI controller for the "Dine-In without Order" form.
 *
 * <p>This controller allows guests or subscribers to join the waiting list
 * without placing an order in advance.</p>
 *
 * <p>Subscribers bypass email and phone input, while guests must provide
 * at least one valid contact method.</p>
 *
 * <p>The controller listens for server responses related to waiting list
 * operations and displays appropriate feedback to the user.</p>
 */
public class NoOrderFormGUIController implements IServerResponseListener {

    /**
     * Container holding email and phone input fields (visible for guests only).
     */
    @FXML
    private VBox EmailPhoneVBOX;

    /**
     * Text field for entering the guest's email address.
     */
    @FXML
    private TextField txtFieldEmail;

    /**
     * Text field for entering the guest's phone number.
     */
    @FXML
    private TextField txtFieldPhoneNumber;

    /**
     * Text field for entering the number of people.
     */
    @FXML
    private TextField txtFieldNumberOfPeople;

    /**
     * Singleton controller responsible for client-server communication.
     */
    private BistroClientController controller;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>This method registers the controller as a server response listener
     * and adjusts the form fields based on whether a subscriber is logged in.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        adjustElementsBasedOnUserType();
    }

    /**
     * Handles server responses related to waiting list operations.
     *
     * <p>On successful addition, the form is cleared and a confirmation
     * message containing the confirmation code is displayed.</p>
     *
     * <p>On failure, an error message is shown.</p>
     *
     * @param message the message received from the server
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {

            case ADD_TO_WAITING_LIST_RESPONSE -> {
                WaitingListResponse response =
                        (WaitingListResponse) message.getData();
                clearForm();
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Successfully added to waiting list!\n" +
                                "Your confirmation code is: " +
                                response.getConformationCode() +
                                "\nWe'll notify you when a table becomes available."
                );
            }

            case ADD_TO_WAITING_LIST_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to a server error, we were unable to add you to the waiting list. Please try again."
            );
        }
    }

    /**
     * Handles submission of the waiting list request.
     *
     * <p>This method validates user input, ensuring a valid number of people
     * and, for guests, a valid email or phone number.</p>
     *
     * <p>Upon successful validation, a waiting list request is sent to the server.</p>
     *
     * @param event the action event triggered by clicking the submit button
     */
    @FXML
    private void onSubmitButtonClicked(ActionEvent event) {

        // Validate number of people
        String peopleText = txtFieldNumberOfPeople.getText();
        if (peopleText.isEmpty() || !BistroUtilities.isNumeric(peopleText)) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter a valid number of people."
            );
            return;
        }

        int numberOfPeople = Integer.parseInt(peopleText);
        if (numberOfPeople <= 0) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Number of people must be greater than zero."
            );
            return;
        }

        // Guest validation (subscribers bypass this)
        if (CustomerStateManager.getInstance().getSubscriber() == null) {

            boolean emailEmpty = txtFieldEmail.getText().isEmpty();
            boolean phoneEmpty = txtFieldPhoneNumber.getText().isEmpty();

            if (emailEmpty && phoneEmpty) {
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Please enter either email or phone number."
                );
                return;
            }

            if (!emailEmpty &&
                    !BistroUtilities.isValidEmail(txtFieldEmail.getText())) {
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Please enter a valid email address."
                );
                return;
            }

            if (!phoneEmpty &&
                    !BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())) {
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Please enter a valid phone number."
                );
                return;
            }
        }

        // Send waiting list request
        controller.requestDiningWithoutOrder(
                new WaitingListRequest(
                        null,
                        numberOfPeople,
                        CustomerStateManager.fillSubscriberIDDetails(),
                        txtFieldEmail.getText(),
                        txtFieldPhoneNumber.getText()
                )
        );
    }

    /**
     * Navigates back to the main form.
     *
     * <p>The form is cleared before leaving the screen.</p>
     *
     * @param event the action event triggered by clicking the back button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearForm() {
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        txtFieldNumberOfPeople.clear();
    }

    /**
     * Adjusts the visibility of input fields based on user type.
     *
     * <p>Email and phone fields are hidden for logged-in subscribers
     * and shown for guests.</p>
     */
    private void adjustElementsBasedOnUserType() {
        boolean isSubscriber =
                CustomerStateManager.getInstance().getSubscriber() != null;

        EmailPhoneVBOX.setVisible(!isSubscriber);
        EmailPhoneVBOX.setManaged(!isSubscriber);
    }
}