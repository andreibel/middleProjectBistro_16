package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GUI controller for the "Lost My Code" screen.
 *
 * <p>This controller allows a subscriber or guest to retrieve their lost
 * order confirmation code. Users can input either email, phone number,
 * or use their subscriber account to fetch orders and request the confirmation code.</p>
 */
public class LostMyCodeFormGUIController implements IServerResponseListener {

    /** Text field for entering the user's email address. */
    @FXML
    private TextField txtFieldEmail;

    /** Text field for entering the user's phone number. */
    @FXML
    private TextField txtFieldPhoneNumber;

    /** Combo box displaying the list of orders available for confirmation code retrieval. */
    @FXML
    private ComboBox<String> comboBoxOrders;
    /** Vbox contains the guests elements in the form (Email and phone Number) **/
    @FXML
    private VBox vBoxGuest;

    @FXML
    private Label lblInfo;

    /** Button used to fetch orders or retrieve the confirmation code. */
    @FXML
    private Button btnRetrieveCode;
    /** Root Pane **/
    @FXML
    private AnchorPane rootPane;
    /** Tracks whether the controller is currently fetching orders (true) or retrieving code (false). */
    private boolean isFetchingOrders = true;

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /**
     * Initializes the controller after the FXML is loaded.
     *
     * <p>Disables the orders combo box initially and sets the retrieve button text.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        adjustFormBasedOnUserType();
        comboBoxOrders.setDisable(true);
        btnRetrieveCode.setText("Get Orders");
    }

    /**
     * Handles server responses.
     *
     * <p>Populates the orders combo box on a successful fetch and shows messages
     * for errors.</p>
     *
     * @param message the message received from the server
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case ORDER_LOST_CONFORMATION_CODE_RESPONSE -> {
                addOrdersTimesToCombobox((List<OrderResponse>) message.getData());
                if (!comboBoxOrders.getItems().isEmpty()) {
                    isFetchingOrders = false;
                    btnRetrieveCode.setText("Retrieve Code");
                    comboBoxOrders.setDisable(false);
                }
            }
            case ORDER_LOST_CONFORMATION_CODE_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, we're unable to send you the confirmation code, please try again later"
            );
        }
    }

    /**
     * Handles clicks on the "Retrieve Code" button.
     *
     * <p>Fetches orders if in fetching mode, or retrieves the confirmation code if
     * an order is selected.</p>
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    private void onButtonRetrieveCodeClicked(ActionEvent event) {
        if (isFetchingOrders) {
            if (CustomerStateManager.getInstance().getSubscriber() == null &&
                    txtFieldEmail.getText().isEmpty() && txtFieldPhoneNumber.getText().isEmpty()) {
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter either email address or phone number");
                return;
            }

            if (CustomerStateManager.getInstance().getSubscriber() == null) {
                if (!txtFieldEmail.getText().isEmpty() && !BistroUtilities.isValidEmail(txtFieldEmail.getText())) {
                    BistroUtilities.showMessage("Bistro Restaurant", "Please enter valid email address");
                    return;
                }
                if (!txtFieldPhoneNumber.getText().isEmpty() && !BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())) {
                    BistroUtilities.showMessage("Bistro Restaurant", "Please enter valid phone number");
                    return;
                }
            }

            controller.requestAllOrdersForCustomer(new OrderRequest(
                    null, null, null,
                    CustomerStateManager.fillSubscriberIDDetails(),
                    txtFieldEmail.getText(),
                    txtFieldPhoneNumber.getText()
            ));
        } else {
            String selectedOrder = comboBoxOrders.getSelectionModel().getSelectedItem();
            if (selectedOrder != null) {
                controller.requestSendConfirmationCode(sendLogMessageToServer());
                clearForm();
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "We've sent you the confirmation code to your email/phone number, please check"
                );
            } else {
                BistroUtilities.showMessage("Bistro Restaurant", "Please select an order");
            }
        }
    }

    /**
     * Handles the "Go Back" button click.
     *
     * <p>Clears the form and navigates back to the "Confirm Arrival" screen.</p>
     *
     * @param event the action event triggered by clicking the button
     * @throws IOException if the FXML cannot be loaded
     */
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Table/GetTableForm.fxml",
                "Bistro Restaurant - Confirm Arrival"
        );
    }

    /**
     * Populates the orders combo box with order times.
     *
     * @param orders the list of orders retrieved from the server
     */
    private void addOrdersTimesToCombobox(List<OrderResponse> orders) {
        comboBoxOrders.getItems().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (OrderResponse order : orders) {
            if (order.getOrderDateTime() != null) {
                comboBoxOrders.getItems().add(order.getOrderDateTime().format(formatter));
            }
        }
    }

    /**
     * Builds a log message for sending confirmation code to the appropriate contact.
     *
     * @return the log message describing where the code is being sent
     */
    private String sendLogMessageToServer() {
        var subscriber = CustomerStateManager.getInstance().getSubscriber();
        if (subscriber != null) return "Sending confirmation code to " + subscriber.getEmail();
        if (!txtFieldEmail.getText().isEmpty()) return "Sending confirmation code to " + txtFieldEmail.getText();
        return "Sending confirmation code to " + txtFieldPhoneNumber.getText();
    }

    /**
     * Clears the form fields and resets the controller state.
     *
     * <p>Resets the fetching mode, clears text fields, empties the combo box,
     * disables the combo box, and sets the button text to "Get Orders".</p>
     */

    private void adjustFormBasedOnUserType(){
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o, ow, nw) -> {
                    if (nw != null) {
                        if (CustomerStateManager.hasSubscriberLoggedIn()) {
                            lblInfo.setText("You are logged in as subscriber, click on get orders to select the order you wish to retrieve it's confirmation code:");
                            vBoxGuest.setVisible(false);
                            vBoxGuest.setManaged(false);
                        }
                        else{
                            lblInfo.setText("If you lost your confirmation code, please enter either email or phone number, pick the order for the confirmation code  we'll send it to you:");
                            vBoxGuest.setVisible(true);
                            vBoxGuest.setManaged(true);
                        }
                    }
                });
            }
        });
    }
    private void clearForm() {
        isFetchingOrders = true;
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        comboBoxOrders.getItems().clear();
        comboBoxOrders.setDisable(true);
        btnRetrieveCode.setText("Get Orders");
    }
}