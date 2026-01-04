package com.andreibel.client.Order;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.TimeGetterRequest;
import com.andreibel.message.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

//TODO: Finish onServerResponse
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
    private Label lblEmail;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private Label lblNumberOfPeople;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblTime;
    @FXML
    private Label lblWizardTitle;
    @FXML
    private Label lblOR;
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
        setDatePicker();
    }

    @Override
    public void onServerResponse(Message message) throws IOException {
        if (message.getType() == APICallType.CREATE_ORDER_RESPONSE){
            clearForm();
            BistroUtilities.showMessage("Bistro Restaurant", "Your order has been successfully created!");
            BistroUtilities.switchScreen(btnOrderNow,"/Main/MainForm.fxml", "Bistro Restaurant");
        }
        else if (message.getType() == APICallType.CREATE_ORDER_ERROR){
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to create the order!");
        }
        else if (message.getType() == APICallType.GET_ALL_TIMES_IN_DATE_RESPONSE){
            setAvailableTimesForComboBox(message);
            if (comboBoxTime.getItems().isEmpty()){
                Platform.runLater(() -> {
                    comboBoxTime.setPromptText("No times available");  // safe now
                });
                return;
            }
            comboBoxTime.setPromptText("Select available times");
            comboBoxTime.setDisable(false);
        }
    }
    @FXML
    private void onOrderNowButtonClicked(ActionEvent event) {
        if (wizardLocation == WizardLocation.PART1_NUM_OF_PEOPLE_AND_DATE) {
            if (txtFieldNumberOfPeople.getText().isEmpty()){
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter the number of people who are ordering");
                return;
            }
            if (datePickerOrder.getValue() == null){
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter the date you wish to order");
            }
            if (!BistroUtilities.isNumeric(txtFieldNumberOfPeople.getText())){
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid number of people");
                return;
            }
            controller.requestAvailableTimes(new TimeGetterRequest(datePickerOrder.getValue(), Integer.parseInt(txtFieldNumberOfPeople.getText())));
        }
        else if ((wizardLocation == WizardLocation.PART2_TIME && CustomerStateManager.getInstance().getSubscriber() != null) || wizardLocation == WizardLocation.PART2_TIME) {
            if (comboBoxTime.getValue() == null){
                BistroUtilities.showMessage("Bistro Restaurant - Create Order", "Please enter the time you wish to order");
                return;
            }
        }
        else if (isOrderCreationDone()){
            controller.requestOrder(
                    Integer.parseInt(txtFieldNumberOfPeople.getText()),
                    LocalDateTime.of(
                            datePickerOrder.getValue(),
                            LocalTime.parse(comboBoxTime.getValue())
                    ),
                    fillSubscriberIDDetails(),
                    txtFieldEmail.getText(),
                    txtFieldPhoneNumber.getText()
            );
            setWizardLocation(WizardLocation.PART1_NUM_OF_PEOPLE_AND_DATE);
            return;
        }
        setWizardLocation(wizardLocation + 1);
        adjustFormToWizardSetup();
    }

    @FXML
    private void onPreviousButtonClicked(ActionEvent event) {
        setWizardLocation(wizardLocation - 1);
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
                "Bistro Restaurant"
        );
    }

    private void clearForm() {
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        txtFieldNumberOfPeople.clear();
        datePickerOrder.setValue(null);
        comboBoxTime.getItems().clear();
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
                comboBoxTime.getItems().clear();
                lblOR.setVisible(false);
                lblPhoneNumber.setVisible(false);
                txtFieldPhoneNumber.setVisible(false);
                lblEmail.setVisible(false);
                txtFieldEmail.setVisible(false);

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
                lblEmail.setVisible(false);
                txtFieldEmail.setVisible(false);

                btnPrevious.setVisible(true);
                lblTime.setVisible(true);
                comboBoxTime.setVisible(true);
                comboBoxTime.setDisable(true);
                break;
            case WizardLocation.PART3_USER_TYPE:
                    lblWizardTitle.setText("Part 3 / 3 - Who will this order be placed for?");
                    lblTime.setVisible(false);
                    comboBoxTime.setVisible(false);

                    btnOrderNow.setText("Order Now");
                    lblOR.setVisible(true);
                    lblPhoneNumber.setVisible(true);
                    txtFieldPhoneNumber.setVisible(true);
                    lblOR.setVisible(true);
                    lblEmail.setVisible(true);
                    txtFieldEmail.setVisible(true);
        }
    }

    private boolean isOrderCreationDone(){
        if ((wizardLocation == WizardLocation.PART2_USER_TYPE && CustomerStateManager.getInstance().getSubscriber() != null)) return true;
        else {
            if (txtFieldEmail.getText().isEmpty() && txtFieldPhoneNumber.getText().isEmpty()){
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter either email or phone number");
                return false;
            }
            if (!BistroUtilities.isValidEmail(txtFieldEmail.getText())){
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid email address");
                return false;
            }
            if (!BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())){
                BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid phone number");
                return false;
            }
            return true;
        }
    }

    private void setWizardLocation(int location){
        wizardLocation = location;
    }

    private void setAvailableTimesForComboBox(Message message){
        List<LocalTime> availableTimes = (List<LocalTime>) message.getData();
        for (LocalTime time : availableTimes) {
            comboBoxTime.getItems().add(time.toString());
        }
    }

    private Integer fillSubscriberIDDetails(){
        return (CustomerStateManager.getInstance().getSubscriber() != null)  ? (Integer)CustomerStateManager.getInstance().getSubscriber().getSubscriberId() : null;
    }

    private void setDatePicker(){
        datePickerOrder.setEditable(false);
        datePickerOrder.setPromptText("Select Order Date");
        datePickerOrder.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });
    }
}
