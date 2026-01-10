package com.andreibel.client.Order;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.TimeGetterRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class OrderFormGUIController implements IServerResponseListener {

    @FXML public HBox TimeHbox;
    @FXML public VBox EmailPhoneVBOX;
    @FXML public ProgressBar wizardProgrtes;
    @FXML public VBox DateNUmberVBOX;

    private enum WizardStep { PART1, PART2_TIME, PART3_USER_INFO }

    @FXML
    private DatePicker datePickerOrder;
    @FXML
    private ComboBox<String> comboBoxTime;
    @FXML
    private TextField txtFieldNumberOfPeople;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private TextField txtFieldPhoneNumber;
    @FXML
    private Button btnOrderNow, btnPrevious, btnGoBack;

    private WizardStep wizardStep = WizardStep.PART1;
    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        comboBoxTime.setDisable(true);
        adjustFormToWizardStep();
        setDatePicker();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message) throws IOException {
        switch (message.getType()) {
            case CREATE_ORDER_RESPONSE -> {
                clearForm();
                wizardStep = WizardStep.PART1;
                BistroUtilities.showMessage("Bistro Restaurant", "Your order has been successfully created!");
                BistroUtilities.switchScreen(btnOrderNow,"/Main/MainForm.fxml", "Bistro Restaurant");
            }
            case CREATE_ORDER_ERROR -> BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, order creation failed.");
            case GET_ALL_TIMES_IN_DATE_RESPONSE ->
                populateAvailableTimes((List<LocalTime>) message.getData());
            case GET_ALL_TIMES_IN_DATE_ERROR -> BistroUtilities.showMessage("Bistro Restaurant", "There are no available times in this day.");
        }
    }

    @FXML
    private void onOrderNowButtonClicked(ActionEvent event) {
        switch (wizardStep) {
            case PART1 -> {
                if (!validatePart1()) return;
                controller.requestAvailableTimes(
                        new TimeGetterRequest(datePickerOrder.getValue(),
                                Integer.parseInt(txtFieldNumberOfPeople.getText()))
                );
                wizardStep = WizardStep.PART2_TIME;
            }
            case PART2_TIME -> {
                if (!validatePart2()) return;
                if (CustomerStateManager.getInstance().getSubscriber() != null) {
                    createOrder();
                }
                else {
                    wizardStep = WizardStep.PART3_USER_INFO;
                }
            }
            case PART3_USER_INFO -> {
                if (!validatePart3()) return;
                createOrder();
            }
        }
        adjustFormToWizardStep();
    }

    @FXML
    private void onPreviousButtonClicked(ActionEvent event) {
        if (wizardStep == WizardStep.PART2_TIME) wizardStep = WizardStep.PART1;
        else if (wizardStep == WizardStep.PART3_USER_INFO) wizardStep = WizardStep.PART2_TIME;
        adjustFormToWizardStep();
    }

    @FXML
    private void onGoBackButtonClick(ActionEvent event) throws IOException {
        wizardStep = WizardStep.PART1;
        clearForm();
        adjustFormToWizardStep();
        BistroUtilities.switchScreen((Node)event.getSource(), "/Main/MainForm.fxml", "Bistro Restaurant");
    }

    private void adjustFormToWizardStep() {
        boolean isSubscriber = CustomerStateManager.getInstance().getSubscriber() != null;
        int totalSteps = isSubscriber ? 2 : 3;

        // Reset visibility
        DateNUmberVBOX.setVisible(false); DateNUmberVBOX.setManaged(false);
        TimeHbox.setVisible(false); TimeHbox.setManaged(false);
        EmailPhoneVBOX.setVisible(false); EmailPhoneVBOX.setManaged(false);


        btnPrevious.setVisible(wizardStep != WizardStep.PART1);

        switch (wizardStep) {
            case PART1 -> {
                DateNUmberVBOX.setVisible(true); DateNUmberVBOX.setManaged(true);

                btnOrderNow.setText("Next");
                wizardProgrtes.setProgress(0);

            }
            case PART2_TIME -> {
                TimeHbox.setVisible(true); TimeHbox.setManaged(true);
                btnOrderNow.setText(isSubscriber ? "Order Now" : "Next");
                wizardProgrtes.setProgress(0.3);
            }
            case PART3_USER_INFO -> {
                if (!isSubscriber) {
                    EmailPhoneVBOX.setVisible(true); EmailPhoneVBOX.setManaged(true);
                    btnOrderNow.setText("Order Now");
                    wizardProgrtes.setProgress(0.6);
                }
            }
        }
    }


    private boolean validatePart1() {
        if (txtFieldNumberOfPeople.getText().isEmpty() || !BistroUtilities.isNumeric(txtFieldNumberOfPeople.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Enter valid number of people.");
            return false;
        }
        if (datePickerOrder.getValue() == null) {
            BistroUtilities.showMessage("Bistro Restaurant", "Select a valid date.");
            return false;
        }
        comboBoxTime.getItems().clear();
        comboBoxTime.setDisable(true);
        return true;
    }

    private boolean validatePart2() {
        if (comboBoxTime.getValue() == null || comboBoxTime.getValue().isBlank()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Select a valid time.");
            return false;
        }
        return true;
    }

    private boolean validatePart3() {
        if ((txtFieldEmail.getText().isBlank() && txtFieldPhoneNumber.getText().isBlank())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Enter either email or phone number.");
            return false;
        }
        if (!txtFieldEmail.getText().isBlank() && !BistroUtilities.isValidEmail(txtFieldEmail.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Enter a valid email.");
            return false;
        }
        if (!txtFieldPhoneNumber.getText().isBlank() && !BistroUtilities.isValidPhoneNumber(txtFieldPhoneNumber.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Enter a valid phone number.");
            return false;
        }
        return true;
    }

    private void createOrder() {
        controller.requestOrderCreation(new OrderRequest(null,  Integer.parseInt(txtFieldNumberOfPeople.getText()),
                LocalDateTime.of(datePickerOrder.getValue(), LocalTime.parse(comboBoxTime.getValue())),
                CustomerStateManager.fillSubscriberIDDetails(),
                txtFieldEmail.getText(),
                txtFieldPhoneNumber.getText())
        );
        wizardStep = WizardStep.PART1;
    }

    private void populateAvailableTimes(List<LocalTime> times) {
        comboBoxTime.getItems().clear();
        if (times.isEmpty()) {
            comboBoxTime.setPromptText("No times available");
        } else {
            for (LocalTime time : times) comboBoxTime.getItems().add(time.toString());
            comboBoxTime.setPromptText("Select available time");
            comboBoxTime.setDisable(false);
        }
    }

    private void setDatePicker() {
        datePickerOrder.setEditable(false);
        datePickerOrder.setPromptText("Select Order Date");
        datePickerOrder.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void clearForm() {
        txtFieldNumberOfPeople.clear();
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        datePickerOrder.setValue(null);
        comboBoxTime.getItems().clear();
        comboBoxTime.setDisable(true);
    }
}
