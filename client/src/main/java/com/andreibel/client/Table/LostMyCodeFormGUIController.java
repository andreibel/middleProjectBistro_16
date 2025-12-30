package com.andreibel.client.Table;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

//TODO: Finish onServerResponse

public class LostMyCodeFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private Button btnRetrieveCode;
    @FXML
    private Button btnGoBack;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    @Override
    public void onServerResponse(Message message) {
//        if (message.getType() != APICallType.LOST_CNFRM_CODE_RESPONSE)
//            return;
        clearForm();
        BistroUtilities.showMessage("Lost My Code", "We've sent you the confrimation code to your email/phone number, please check");
    }
    @FXML
    private void onButtonRetrieveCodeClicked(ActionEvent event) {
        if(txtFieldEmail.getText().isEmpty() && txtFieldPhoneNumber.getText().isEmpty()){
            BistroUtilities.showMessage("Bistro Restaurant - Lost My Code", "Please enter either  email address or phone number");
        }
        else{
            if (!BistroUtilities.isValidEmail(txtFieldEmail.getText())){
                BistroUtilities.showMessage("Bistro Restaurant - Lost My Code", "Please enter valid email address");
            }
            else if (!BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())){
                BistroUtilities.showMessage("Bistro Restaurant - Lost My Code", "Please enter valid phone number");
            }
            else {
                controller.requestLostConfirmationCode(txtFieldEmail.getText(), txtFieldPhoneNumber.getText());
            }
        }
    }
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Table/GetTableForm.fxml",
                "Bistro Restaurant - Confirm Arrival"
        );
    }

    private void clearForm() {
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
    }
}
