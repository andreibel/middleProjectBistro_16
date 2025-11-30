package com.andreibel.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ClientMainController {
    @FXML
    private Button btnUpdateOrder;
    @FXML
    private Label lblOrderDate;
    @FXML
    private Label lblNumberOfGuests;
    @FXML
    private DatePicker orderDatePicker;
    @FXML
    private TextField txtNumberOfGuests;

    private void onUpdateOrderButtonClicked(ActionEvent event) {

    }

}
