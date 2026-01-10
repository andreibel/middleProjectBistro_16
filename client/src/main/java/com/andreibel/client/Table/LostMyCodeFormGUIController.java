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

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LostMyCodeFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private ComboBox<String> comboBoxOrders;
    @FXML
    private Label lblOrders;
    @FXML
    private Button btnRetrieveCode;
    @FXML
    private Button btnGoBack;

    private boolean isFetchingOrders = true;
    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        lblOrders.setDisable(true);
        comboBoxOrders.setDisable(true);
        btnRetrieveCode.setText("Get Orders");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case ORDER_LOST_CONFORMATION_CODE_RESPONSE -> {
                addOrdersTimesToCombobox((List<OrderResponse>) message.getData());
                if (!comboBoxOrders.getItems().isEmpty()) {
                    isFetchingOrders = false;
                    btnRetrieveCode.setText("Retrieve Code");
                    lblOrders.setDisable(false);
                    comboBoxOrders.setDisable(false);
                }
            }
            case ORDER_LOST_CONFORMATION_CODE_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, we're unable to send you the confirmation code, please try again later"
            );
        }
    }

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
        }
        else {
            String selectedOrder = comboBoxOrders.getSelectionModel().getSelectedItem();
            if (selectedOrder != null) {
                controller.requestSendConfirmationCode(sendLogMessageToServer());
                clearForm();
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "We've sent you the confirmation code to your email/phone number, please check"
                );
            }
            else {
                BistroUtilities.showMessage("Bistro Restaurant", "Please select an order");
            }
        }
    }

    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Table/GetTableForm.fxml",
                "Bistro Restaurant - Confirm Arrival"
        );
    }

    private void addOrdersTimesToCombobox(List<OrderResponse> orders) {
        comboBoxOrders.getItems().clear(); // clear old entries
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (OrderResponse order : orders) {
            if (order.getOrderDateTime() != null) {
                comboBoxOrders.getItems().add(order.getOrderDateTime().format(formatter));
            }
        }
    }

    private String sendLogMessageToServer() {
        var subscriber = CustomerStateManager.getInstance().getSubscriber();
        if (subscriber != null) return "Sending confirmation code to " + subscriber.getEmail();
        if (!txtFieldEmail.getText().isEmpty()) return "Sending confirmation code to " + txtFieldEmail.getText();
        return "Sending confirmation code to " + txtFieldPhoneNumber.getText();
    }

    private void clearForm() {
        isFetchingOrders = true;
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        comboBoxOrders.getItems().clear();
        lblOrders.setDisable(true);
        comboBoxOrders.setDisable(true);
        btnRetrieveCode.setText("Get Orders");
    }
}
