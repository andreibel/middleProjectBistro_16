package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;


//TODO: Finish OnServerResponse and add parameter for arrival time


import java.io.IOException;
import java.util.UUID;

public class GetTableFormGUIController implements IServerResponseListener{

    @FXML
    private Label lblSubscriberInfo;
    @FXML
    private Label lblWhoAmI;
    @FXML
    private RadioButton radioBtnGuest;
    @FXML
    private RadioButton radioBtnSubscriber;
    @FXML
    private TextField txtFieldConfirmation;
    @FXML
    private Label lblOR;
    @FXML
    private Label lblSubscriberID;
    @FXML
    private TextField txtFieldSubscriberID;
    @FXML
    private Button btnConfrimArrival;
    @FXML
    private Button btnGoBack;
    @FXML
    private Button btnLostMyCode;

    BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        radioBtnGuest.setSelected(true);
        adjustFormBasedOnUserType();
    }

    @Override
    public void onServerResponse(Message message) {
        if (message.getType() != APICallType.ORDER_ARRIVED_RESPONSE)
            return;
        clearForm();
        //request adding to waiting list IF NO TABLE IS AVAILABLE
        //controller.requestGetTable(.....);

        //ONLY IF HE GOT TO TABLE AND NOT ON WAITING LIST
        CustomerStateManager.getInstance().setArrivedToTable(true);
        CustomerStateManager.getInstance().setConfirmationCode(Integer.parseInt(txtFieldConfirmation.getText()));

    }

    @FXML
    private void onButtonConfirmArrivalClicked(ActionEvent event)  {
        if (radioBtnGuest.isSelected() && !txtFieldConfirmation.getText().isEmpty()) {
            BistroUtilities.showMessage("Error", "Dear Guest, please enter confirmation code");
            return;
        }
        if (radioBtnSubscriber.isSelected() && !txtFieldSubscriberID.getText().isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant - Confirm Arrival Error", "Dear Subscriber, please enter confirmation code or subscriber ID");
            return;
        }

        if (BistroUtilities.isNumeric(txtFieldConfirmation.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant - Confirm Error", "Please enter valid confirmation code");
            return;
        }

        if (BistroUtilities.isNumeric(txtFieldSubscriberID.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant - Confirm Error", "Please enter valid subscriber ID");
            return;
        }
        //Need to add parameter for local date time (Maybe String) for arrival time
        //controller.requestArrivalConfirmation(new OrderRequest(UUID.fromString(txtFieldConfirmation.getText()), ));
    }
    @FXML
    private void onLostMyCodeButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Table/LostMyCodeForm.fxml", "Bistro Restaurant - Lost My Code");
    }

    @FXML
    private void onRadioUserTypeChanged(ActionEvent event) {
        adjustFormBasedOnUserType();
    }

    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }

    private void clearForm() {
        adjustFormBasedOnUserType();
        radioBtnGuest.setSelected(false);
        radioBtnSubscriber.setSelected(false);
        txtFieldConfirmation.clear();
        txtFieldSubscriberID.clear();
    }

    private void adjustFormBasedOnUserType(){
        if (CustomerStateManager.getInstance().getSubscriber() != null) {
            lblWhoAmI.setVisible(false);
            radioBtnSubscriber.setVisible(false);
            radioBtnGuest.setVisible(false);
            lblSubscriberInfo.setVisible(true);
            lblOR.setVisible(true);
            lblSubscriberID.setVisible(true);
            txtFieldSubscriberID.setVisible(true);
        }
        else{
            if (radioBtnGuest.isSelected()) {
                lblSubscriberInfo.setVisible(false);
                lblOR.setVisible(false);
                lblSubscriberID.setVisible(false);
                txtFieldSubscriberID.setVisible(false);
            }
            else{
                lblSubscriberInfo.setVisible(true);
                lblOR.setVisible(true);
                lblSubscriberID.setVisible(true);
                txtFieldSubscriberID.setVisible(true);
            }
        }

    }

    private Integer fillSubscriberIDDetails(){
        return ((radioBtnSubscriber.isSelected() || CustomerStateManager.getInstance().getSubscriber() != null) && !txtFieldSubscriberID.getText().isEmpty()) ? Integer.parseInt(txtFieldSubscriberID.getText()) : null;
    }

}
