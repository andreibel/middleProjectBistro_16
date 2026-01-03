package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//TODO: Finish onServerResponse

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

    private String email, phoneNumber;
    private boolean isFetchingOrders = true;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }
    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message) {
        if (message.getType() == APICallType.GET_ALL_ORDERS_CUSTOMER_RESPONSE) {

        }
        else if (message.getType() == APICallType.GET_ALL_ORDERS_CUSTOMER_ERROR){
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error it was unable to fetch orders.");
        }
        else if (message.getType() == APICallType.ORDER_LOST_CONFORMATION_CODE_RESPONSE){
            if (isFetchingOrders) {
                Platform.runLater(() -> {
                    addOrdersTimesToCombobox((ArrayList<OrderResponse>) message.getData());
                    if (!comboBoxOrders.getItems().isEmpty()) {
                        isFetchingOrders = false;
                        btnRetrieveCode.setText("Retrieve Code");
                        lblOrders.setDisable(false);
                        comboBoxOrders.setDisable(false);
                    }
                });
            }
            else{
                clearForm();
                BistroUtilities.showMessage("Bistro Restaurant", "We've sent you the confirmation code to your email/phone number, please check");
            }

        }
        else if (message.getType() == APICallType.ORDER_LOST_CONFORMATION_CODE_ERROR){
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, we're unable to send you the confirmation code, please try again later");
        }

    }
    @FXML
    private void onButtonRetrieveCodeClicked(ActionEvent event) {
        if (isFetchingOrders) {
            if(txtFieldEmail.getText().isEmpty() && txtFieldPhoneNumber.getText().isEmpty()){
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter either email address or phone number");
            }
            else{
                if (!BistroUtilities.isValidEmail(txtFieldEmail.getText())){
                    BistroUtilities.showMessage("Bistro Restaurant", "Please enter valid email address");
                }
                else if (!BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())){
                    BistroUtilities.showMessage("Bistro Restaurant", "Please enter valid phone number");
                }
                else {
                    controller.requestAllOrdersForCustomer(new OrderRequest(null, null, null, null, txtFieldEmail.getText(), txtFieldPhoneNumber.getText()));
                }
            }
        }
        else{
            String[] dateTimeOrder = comboBoxOrders.getSelectionModel().getSelectedItem().split(" ");
            controller.requestLostConfirmationCode(dateTimeOrder[0], dateTimeOrder[1]);
        }

    }
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Table/GetTableForm.fxml",
                "Bistro Restaurant - Confirm Arrival"
        );
    }

    private void addOrdersTimesToCombobox(List<OrderResponse> orders) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (OrderResponse order : orders){
            comboBoxOrders.getItems().add(order.getOrderDateTime().format(formatter));
        }
    }

    private void clearForm() {
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        comboBoxOrders.getItems().clear();
        lblOrders.setDisable(true);
        comboBoxOrders.setDisable(true);
        btnRetrieveCode.setText("Get Orders");
    }
}
