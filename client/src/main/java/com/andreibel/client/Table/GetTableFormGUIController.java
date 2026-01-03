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
        if (message.getType() == APICallType.ORDER_ARRIVED_RESPONSE){
            clearForm();
            CustomerStateManager.getInstance().setArrivedToTable(true);
            CustomerStateManager.getInstance().setConfirmationCode(Integer.parseInt(txtFieldConfirmation.getText()));
            BistroUtilities.showMessage("Bistro Restaurant", "Thank you for confirming your arrival, please head to your table");
        }
        else if (message.getType() == APICallType.ORDER_ARRIVED_WAITING_RESPONSE)
            BistroUtilities.showMessage("Bistro Restaurant", "Thank you for confirming your arrival, Unfortunately we currently don't have a table ready for you.\nYou'll be entering a waiting list and we'll let you know when your table is ready, we apologize for the inconvenience.");
        else if (message.getType() == APICallType.ORDER_ARRIVED_ERROR)
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to confirm your arrival, please contact staff for help");

    }

    @FXML
    private void onButtonConfirmArrivalClicked(ActionEvent event)  {
        if (CustomerStateManager.getInstance().getSubscriber() == null && txtFieldConfirmation.getText().isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Dear Guest, please enter confirmation code");
            return;
        }

        if (CustomerStateManager.getInstance().getSubscriber() == null && !BistroUtilities.isNumeric(txtFieldConfirmation.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter valid confirmation code");
            return;
        }

        controller.requestArrivalConfirmation(new OrderRequest(UUID.fromString(txtFieldConfirmation.getText()), null, null, fillSubscriberIDDetails(), null, null));
    }
    @FXML
    private void onLostMyCodeButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Table/LostMyCodeForm.fxml", "Bistro Restaurant - Lost My Code");
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
        txtFieldConfirmation.clear();
    }

    private void adjustFormBasedOnUserType(){
        if (CustomerStateManager.getInstance().getSubscriber() != null) {
            lblSubscriberInfo.setVisible(true);
        }
        else{
            lblSubscriberInfo.setVisible(false);
        }

    }

    private Integer fillSubscriberIDDetails(){
        return (CustomerStateManager.getInstance().getSubscriber() != null)? CustomerStateManager.getInstance().getSubscriber().getSubscriberId() : null;
    }

}
