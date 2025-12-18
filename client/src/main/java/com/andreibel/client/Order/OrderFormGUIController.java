package com.andreibel.client.Order;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class OrderFormGUIController {
    @FXML
    private DatePicker datePickerOrder;
    @FXML
    private ComboBox<String> comboBoxTime;
    @FXML
    private TextField txtFieldNumberOfPeople;
    @FXML
    private RadioButton radioGuest;
    @FXML
    private RadioButton radioSubscriber;
    @FXML
    private TextField txtFieldSubscriberId;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private Button btnOrderNow;
    @FXML
    private Button btnGoBack;

    public void onOrderNowButtonClicked(ActionEvent event) {}
    public void onGoBackButtonClicked(ActionEvent event) {}
}
