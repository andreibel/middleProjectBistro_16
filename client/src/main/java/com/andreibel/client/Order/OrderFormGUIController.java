package com.andreibel.client.Order;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;


class WizardLocation {
    public static final int PART1_NUM_OF_PEOPLE_AND_DATE = 1;
    public static final int PART2_TIME = 2;
    public static final int PART2_USER_TYPE = 2;
    public static final int PART3_USER_TYPE = 3;
}

public class OrderFormGUIController implements IServerResponseListener {
    @FXML
    private DatePicker datePickerOrder;
    @FXML
    private ComboBox<String> comboBoxTime;
    @FXML
    private TextField txtFieldNumberOfPeople;
    @FXML
    private RadioButton radioGuest;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private Label lblWhoAmI;
    @FXML
    private Label lblNumberOfPeople;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblTime;
    @FXML
    private Label lblSubscriberId;
    @FXML
    private Label lblWizardTitle;
    @FXML
    private Label lblOR;
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
    private Button btnPrevious;
    @FXML
    private Button btnGoBack;

    private static int wizardLocation = WizardLocation.PART1_NUM_OF_PEOPLE_AND_DATE;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        adjustFormToWizardSetup();
    }

    @Override
    public void onServerResponse(Message message){
        if (message.getType() != APICallType.CREATE_ORDER_RESPONSE)
            return;
        BistroUtilities.showMessage("Order", "Your order has been successfully created!");
    }
    @FXML
    private void onRadioUserTypeChanged(ActionEvent event) {
        adjustElementsBasedOnUserType(event);
    }
    @FXML
    private void onOrderNowButtonClicked(ActionEvent event) {
        if ((wizardLocation == WizardLocation.PART2_USER_TYPE && CustomerStateManager.getInstance() != null && CustomerStateManager.getInstance().getSubscriber() != null) || wizardLocation == WizardLocation.PART3_USER_TYPE){
            wizardLocation = WizardLocation.PART1_NUM_OF_PEOPLE_AND_DATE;
            controller.requestOrder(Integer.parseInt(txtFieldNumberOfPeople.getText()), datePickerOrder.getValue().atStartOfDay(), Integer.parseInt(txtFieldSubscriberId.getText()), txtFieldPhoneNumber.getText(), txtFieldEmail.getText());
            //need to add time in the OrderRequest in Message API..
            return;
        }
        //Validate Each of the wizard setups checking input
        ++wizardLocation;
        adjustFormToWizardSetup();
//        // reading data from the form
//        int guests;
//        try {
//            guests = Integer.parseInt(txtFieldNumberOfPeople.getText().trim());
//        } catch (NumberFormatException e) {
//            BistroUtilities.showMessage("Error","Please enter a valid number of people");
//            return;
//        }
//
//        LocalDate date = datePickerOrder.getValue();
//        if (date == null) {
//            BistroUtilities.showMessage("Error","Please select a date");
//            return;
//        }
//
//        String timeStr = comboBoxTime.getValue();
//        if (timeStr == null) {
//            BistroUtilities.showMessage("Error","Please select a time");
//            return;
//        }
//
//        LocalDateTime orderDateTime =
//                LocalDateTime.of(date, LocalTime.parse(timeStr));
//
//
//        Integer subscriberId = null;
//        String email = null;
//        String phoneNumber = null;
//
//        if (radioSubscriber.isSelected()) {
//            // Subscriber selected
//            String idText = txtFieldSubscriberId.getText().trim();
//            if (idText.isEmpty()) {
//                BistroUtilities.showMessage("Error","Please enter Subscriber ID");
//                return;
//            }
//            subscriberId = Integer.parseInt(idText);
//        } else {
//            // Guest selected
//            email = txtFieldEmail.getText().trim();
//            phoneNumber = txtFieldPhoneNumber.getText().trim();
//
//            if (!BistroUtilities.isValidEmail(email)) {
//
//            }
//            if (!BistroUtilities.isValidPhoneNumber(phoneNumber)) {}
//        }
//
//        // --------------------------------------------------
//        // calling Controller / Client
//        // --------------------------------------------------
//        //Calling controller.createOrder...
    }

    @FXML
    private void onPreviousButtonClicked(ActionEvent event) {
        --wizardLocation;
        adjustFormToWizardSetup();
    }

    @FXML
    private void onGoBackButtonClick(ActionEvent event) throws IOException {
        wizardLocation = WizardLocation.PART1_NUM_OF_PEOPLE_AND_DATE;
        adjustFormToWizardSetup();
        clearForm();
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro"
        );
    }
    @FXML
    private void adjustElementsBasedOnUserType(ActionEvent event) {
        if(radioSubscriber.isSelected()){
            lblSubscriberId.setVisible(true);
            txtFieldSubscriberId.setVisible(true);
            txtFieldSubscriberId.setManaged(true);
            lblOR.setVisible(false);
            lblEmail.setVisible(false);
            txtFieldEmail.setVisible(false);
            txtFieldEmail.setManaged(false);
            lblPhoneNumber.setVisible(false);
            txtFieldPhoneNumber.setVisible(false);
            txtFieldPhoneNumber.setManaged(false);
        }
        else{
            lblSubscriberId.setVisible(false);
            txtFieldSubscriberId.setVisible(false);
            txtFieldSubscriberId.setManaged(false);
            lblOR.setVisible(true);
            lblEmail.setVisible(true);
            txtFieldEmail.setVisible(true);
            txtFieldEmail.setManaged(true);
            lblPhoneNumber.setVisible(true);
            txtFieldPhoneNumber.setVisible(true);
            txtFieldPhoneNumber.setManaged(true);
        }
    }

    private void clearForm() {
        txtFieldEmail.setText("");
        txtFieldPhoneNumber.setText("");
        txtFieldNumberOfPeople.setText("");
        txtFieldSubscriberId.setText("");
        datePickerOrder.setValue(null);
        comboBoxTime.getSelectionModel().clearSelection();
        radioGuest.setSelected(true);
        txtFieldEmail.setStyle(null);
    }

    private void adjustFormToWizardSetup() {
        switch (wizardLocation) {
            case WizardLocation.PART1_NUM_OF_PEOPLE_AND_DATE:
                if (CustomerStateManager.getInstance() != null && CustomerStateManager.getInstance().getSubscriber() != null) {
                    lblWizardTitle.setText("Part 1 / 2 - Number of People and Date of Order");
                }
                else {
                    lblWizardTitle.setText("Part 1 / 3 - Number of People and Date of Order");
                }
                btnOrderNow.setText("Next");
                btnPrevious.setVisible(false);
                lblTime.setVisible(false);
                comboBoxTime.setVisible(false);
                lblWhoAmI.setVisible(false);
                lblOR.setVisible(false);
                radioGuest.setVisible(false);
                radioSubscriber.setVisible(false);
                lblPhoneNumber.setVisible(false);
                txtFieldPhoneNumber.setVisible(false);
                lblEmail.setVisible(false);
                txtFieldEmail.setVisible(false);
                lblSubscriberId.setVisible(false);
                txtFieldSubscriberId.setVisible(false);

                lblDate.setVisible(true);
                datePickerOrder.setVisible(true);
                lblNumberOfPeople.setVisible(true);
                txtFieldNumberOfPeople.setVisible(true);
                break;
            case WizardLocation.PART2_USER_TYPE | WizardLocation.PART2_TIME:
                if (CustomerStateManager.getInstance() != null && CustomerStateManager.getInstance().getSubscriber() != null) {
                    lblWizardTitle.setText("Part 2 / 2 - Select Order's Time");
                    btnOrderNow.setText("Order Now");
                }
                else {
                    lblWizardTitle.setText("Part 2 / 3 - Select Order's Time");
                    btnOrderNow.setText("Next");
                }
                lblDate.setVisible(false);
                datePickerOrder.setVisible(false);
                lblNumberOfPeople.setVisible(false);
                txtFieldNumberOfPeople.setVisible(false);
                lblPhoneNumber.setVisible(false);
                txtFieldPhoneNumber.setVisible(false);
                lblOR.setVisible(false);
                radioGuest.setVisible(false);
                radioSubscriber.setVisible(false);
                lblWhoAmI.setVisible(false);
                lblEmail.setVisible(false);
                txtFieldEmail.setVisible(false);
                lblSubscriberId.setVisible(false);
                txtFieldSubscriberId.setVisible(false);

                btnPrevious.setVisible(true);
                lblTime.setVisible(true);
                comboBoxTime.setVisible(true);
                break;
            case WizardLocation.PART3_USER_TYPE:
                    lblWizardTitle.setText("Part 3 / 3 - Who will this order be placed for?");
                    lblTime.setVisible(false);
                    comboBoxTime.setVisible(false);

                    btnOrderNow.setText("Order Now");
                    lblWhoAmI.setVisible(true);
                    lblOR.setVisible(true);
                    radioGuest.setVisible(true);
                    radioSubscriber.setVisible(true);
                    adjustElementsBasedOnUserType(null);
        }
    }
}
