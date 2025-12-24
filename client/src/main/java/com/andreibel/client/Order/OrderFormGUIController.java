package com.andreibel.client.Order;

import com.andreibel.client.util.ScreenTransfer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

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
    @FXML
    void adjustFormBasedOnType(ActionEvent event){
        if(radioSubscriber.isSelected()){
            txtFieldSubscriberId.setVisible(true);
            txtFieldSubscriberId.setManaged(true);
            txtFieldEmail.setVisible(false);
            txtFieldEmail.setManaged(false);
            txtFieldPhoneNumber.setVisible(false);
            txtFieldPhoneNumber.setManaged(false);
        }
        else{
            txtFieldSubscriberId.setVisible(false);
            txtFieldSubscriberId.setManaged(false);
            txtFieldEmail.setVisible(true);
            txtFieldEmail.setManaged(true);
            txtFieldPhoneNumber.setVisible(true);
            txtFieldPhoneNumber.setManaged(true);
        }
    }
    @FXML
    public void initialize() {

        radioGuest.setSelected(true);
        adjustFormBasedOnType(null);
    }


    @FXML
    public void onOrderNowButtonClicked(ActionEvent event) {

        // reading data from the form
        int guests;
        try {
            guests = Integer.parseInt(numberOfPeopleField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Please enter a valid number of people");
            return;
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            showError("Please select a date");
            return;
        }

        String timeStr = timeCombo.getValue();
        if (timeStr == null) {
            showError("Please select a time");
            return;
        }

        LocalDateTime orderDateTime =
                LocalDateTime.of(date, LocalTime.parse(timeStr));





        Integer subscriberId = null;
        String email = null;
        String phoneNumber = null;

        if (subscriberRadio.isSelected()) {
            // Subscriber selected
            String idText = subscriberIdField.getText().trim();
            if (idText.isEmpty()) {
                showError("Please enter Subscriber ID");
                return;
            }
            subscriberId = Integer.parseInt(idText);
        } else {
            // Guest selected
            email = emailField.getText().trim();
            phoneNumber = phoneField.getText().trim();

            if (email.isEmpty() || phoneNumber.isEmpty()) {
                showError("Please enter Email and Phone Number");
                return;
            }
        }

        // --------------------------------------------------
        // calling Controller / Client
        // --------------------------------------------------

        controller.updateOrder(
                selected.getOrderNumber(),
                guests,
                orderDateTime,
                subscriberId,
                email,
                phoneNumber
        );
    }


    private void clearForm() {

        txtFieldEmail.setText("");
        txtFieldPhoneNumber.setText("");
        txtFieldNumberOfPeople.setText("");
        txtFieldSubscriberId.setText("");


        datePickerOrder.setValue(null);
        comboBoxTime.getSelectionModel().clearSelection();


        radioGuest.setSelected(true);


        adjustFormBasedOnType(null);


        txtFieldEmail.setStyle(null);
    }
    @FXML
    void onGoBackButtonClick(ActionEvent event) throws IOException {
        clearForm();
        ScreenTransfer.switchScreen(
                event,
                "/Main/MainFormGUIController.fxml",
                "MainFormGUIController"
            );
    }
}
