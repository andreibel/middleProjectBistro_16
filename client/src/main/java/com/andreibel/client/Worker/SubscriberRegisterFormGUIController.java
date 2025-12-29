package com.andreibel.client.Worker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SubscriberRegisterFormGUIController {
    @FXML
    private Label lblName;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private TextField txtFieldName;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private Button btnRegister;
    @FXML
    private Button btnGoBack;

    @FXML
    private void initialize() {}

    @FXML
    private void onRegisterButtonClicked(ActionEvent event) {}
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) {}
}
