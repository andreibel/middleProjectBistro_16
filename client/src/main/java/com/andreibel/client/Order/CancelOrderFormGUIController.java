package com.andreibel.client.Order;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CancelOrderFormGUIController implements IServerResponseListener {
    @FXML
    private Button btnCancelOrder;
    @FXML
    private Button btnGoBack;
    @FXML
    private TextField txtFieldConfirmationCode;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message){
        if (message.getType() != APICallType.DELETE_ORDER_RESPONSE)
            return;
        BistroUtilities.showMessage("Order Cancellation", "Your order has been successfully canceled.");
    }
    @FXML
    private void onCancelOrderButtonClicked(ActionEvent event) {
        controller.requestOrderCancel(Integer.parseInt(txtFieldConfirmationCode.getText()));
    }
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Main/MainForm.fxml", "Bistro Restaurant");
    }
}
