package com.andreibel.client.Table;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LostMyCodeFormGUIController {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblSubscriberID;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private TextField txtFieldSubscriberID;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private Button btnRetrieveCode;
    @FXML
    private Button btnGoBack;

    public void onButtonRetrieveCodeClicked(ActionEvent event) {}
    public void onButtonGoBackClicked(ActionEvent event) {}
}
