package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class SubscriberZoneFormGUIController{

    @FXML
    private Label lblSubscriber;
    @FXML
    private Button btnOrderHistory;
    @FXML
    private Button btnOrderNow;
    @FXML
    private Button btnGetTable;
    @FXML
    private Button btnEditInfo;
    @FXML
    private Button btnGoBack;

    @FXML
    private void initialize() {
        lblSubscriber.setText("Hi " + CustomerStateManager.getInstance().getSubscriber().getName() + ", please select the following " +
                "options:");
    }
    @FXML
    private void onButtonOrderHistoryClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Subscriber/OrderList.fxml", "Bistro - Orders History");
    }
    @FXML
    private void onButtonOrderNowClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Order/OrderForm.fxml", "Bistro - Order Now");
    }
    @FXML
    private void onButtonGetTableClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Table/GetTableForm.fxml", "Bistro - Orders History");
    }
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Subscriber/OrderListForm.fxml", "Bistro - Orders History");
    }
    @FXML
    private void onButtonEditInfoClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Subscriber/SubscriberEditForm.fxml", "Bistro - Edit Subscriber Info");
    }
}
