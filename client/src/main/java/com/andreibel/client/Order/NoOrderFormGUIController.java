package com.andreibel.client.Order;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;

public class NoOrderFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblGuest;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private Label lblOR;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private TextField txtFieldNumberOfPeople;
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnGoBack;
    @FXML
    private AnchorPane rootPane;

    private static final Point buttonGuestLocation = new Point(318, 264);
    private static final Point buttonSubscriberLocation = new Point(318, 150);
    private BistroClientController controller;

    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        adjustElementsBasedOnUserType();
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            adjustElementsBasedOnUserType();
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onServerResponse(Message message){

    }

    @FXML
    private void onSubmitButtonClicked(ActionEvent event) {

    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Main/MainForm.fxml", "Bistro Restaurant");
    }

    private void clearForm(){
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        txtFieldNumberOfPeople.clear();
    }
    private void adjustElementsBasedOnUserType(){
        if (CustomerStateManager.getInstance().getSubscriber() != null){
            lblOR.setVisible(false);
            lblGuest.setVisible(false);
            lblEmail.setVisible(false);
            lblPhoneNumber.setVisible(false);
            txtFieldEmail.setVisible(false);
            txtFieldPhoneNumber.setVisible(false);
            btnSubmit.setLayoutX(buttonSubscriberLocation.getX());
            btnSubmit.setLayoutY(buttonSubscriberLocation.getY());
        }
        else{
            lblOR.setVisible(true);
            lblGuest.setVisible(true);
            lblEmail.setVisible(true);
            lblPhoneNumber.setVisible(true);
            txtFieldEmail.setVisible(true);
            txtFieldPhoneNumber.setVisible(true);
            btnSubmit.setLayoutX(buttonGuestLocation.getX());
            btnSubmit.setLayoutY(buttonGuestLocation.getY());
        }
    }
}
