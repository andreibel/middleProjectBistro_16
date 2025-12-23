package com.andreibel.client.Order;

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


    public void onOrderNowButtonClicked(ActionEvent event) {

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
    void onGoBackButtonClick(ActionEvent event) {
        clearForm();


        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main/MainFormGUIController.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading previous screen");
        }
    }
}
