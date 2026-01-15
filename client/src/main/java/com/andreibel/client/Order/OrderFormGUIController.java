package com.andreibel.client.Order;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.TimeGetterRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for the order creation wizard.
 *
 * <p>This controller manages a multi-step ordering process:
 * <ul>
 *     <li>Step 1: Select date and number of people</li>
 *     <li>Step 2: Select available time</li>
 *     <li>Step 3: Enter contact details (non-subscribers only)</li>
 * </ul>
 *
 * <p>The controller communicates with the server to retrieve available times
 * and create orders, while dynamically updating the UI.</p>
 */
public class OrderFormGUIController implements IServerResponseListener {

    /** Wizard container panes */
    @FXML private VBox dateNumberVbox;
    @FXML private HBox timeHbox;
    @FXML private VBox emailPhoneVbox;

    /** Wizard progress indicator */
    @FXML private ProgressBar wizardProgress;

    /** Input controls */
    @FXML private DatePicker datePickerOrder;
    @FXML private ComboBox<String> comboBoxTime;
    @FXML private TextField txtFieldNumberOfPeople;
    @FXML private TextField txtFieldEmail;
    @FXML private TextField txtFieldPhoneNumber;

    /** Navigation buttons */
    @FXML private Button btnOrderNow;
    @FXML private Button btnPrevious;
    @FXML private Button btnGoBack;

    /** Root pane */
    @FXML private AnchorPane rootPane;

    /**
     * Represents the current wizard step.
     */
    private enum WizardStep {
        PART1,
        PART2_TIME,
        PART3_USER_INFO
    }

    private WizardStep wizardStep = WizardStep.PART1;
    private BistroClientController controller;

    /**
     * Initializes the controller and registers it as a server response listener.
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        adjustFormWhenSceneIsShown();
    }

    /**
     * Handles server responses related to order creation and time availability.
     *
     * @param message message received from the server
     * @throws IOException if screen switching fails
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message) throws IOException {
        switch (message.getType()) {
            case CREATE_ORDER_RESPONSE -> {
                clearForm();
                wizardStep = WizardStep.PART1;
                BistroUtilities.showSelectableMessage("Bistro Restaurant",
                        "Your order has been successfully created!\nYour confirmation code is:",
                        ((OrderResponse)message.getData()).getConformationCode().toString()
                );
                BistroUtilities.switchScreen(
                        btnOrderNow,
                        "/Main/MainForm.fxml",
                        "Bistro Restaurant"
                );
            }
            case CREATE_ORDER_ERROR ->
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            "Due to a server error, order creation failed."
                    );

            case GET_ALL_TIMES_IN_DATE_RESPONSE ->
                    populateAvailableTimes((List<LocalTime>) message.getData());

            case GET_ALL_TIMES_IN_DATE_ERROR ->
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            "There are no available times on this date."
                    );
        }
    }

    /**
     * Handles the main wizard action button (Next / Order Now).
     */
    @FXML
    private void onOrderNowButtonClicked(ActionEvent event) {
        switch (wizardStep) {
            case PART1 -> {
                if (!validatePart1()) return;
                controller.requestAvailableTimes(
                        new TimeGetterRequest(
                                datePickerOrder.getValue(),
                                Integer.parseInt(txtFieldNumberOfPeople.getText())
                        )
                );
                wizardStep = WizardStep.PART2_TIME;
            }
            case PART2_TIME -> {
                if (!validatePart2()) return;
                if (CustomerStateManager.getInstance().getSubscriber() != null) {
                    createOrder();
                } else {
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

    /**
     * Navigates to the previous wizard step.
     */
    @FXML
    private void onPreviousButtonClicked(ActionEvent event) {
        if (wizardStep == WizardStep.PART2_TIME)
            wizardStep = WizardStep.PART1;
        else if (wizardStep == WizardStep.PART3_USER_INFO)
            wizardStep = WizardStep.PART2_TIME;

        adjustFormToWizardStep();
    }

    /**
     * Returns the user to the main screen and resets the form.
     */
    @FXML
    private void onGoBackButtonClick(ActionEvent event) throws IOException {
        wizardStep = WizardStep.PART1;
        clearForm();
        adjustFormToWizardStep();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }

    /**
     * Adjusts visible UI components according to the current wizard step.
     */
    private void adjustFormToWizardStep() {
        boolean isSubscriber =
                CustomerStateManager.getInstance().getSubscriber() != null;

        dateNumberVbox.setVisible(false);
        dateNumberVbox.setManaged(false);
        timeHbox.setVisible(false);
        timeHbox.setManaged(false);
        emailPhoneVbox.setVisible(false);
        emailPhoneVbox.setManaged(false);

        btnPrevious.setVisible(wizardStep != WizardStep.PART1);

        switch (wizardStep) {
            case PART1 -> {
                dateNumberVbox.setVisible(true);
                dateNumberVbox.setManaged(true);
                btnOrderNow.setText("Next");
                wizardProgress.setProgress(0);
            }
            case PART2_TIME -> {
                timeHbox.setVisible(true);
                timeHbox.setManaged(true);
                btnOrderNow.setText(isSubscriber ? "Order Now" : "Next");
                wizardProgress.setProgress(isSubscriber ? 0.5 : 0.3);
            }
            case PART3_USER_INFO -> {
                emailPhoneVbox.setVisible(true);
                emailPhoneVbox.setManaged(true);
                btnOrderNow.setText("Order Now");
                wizardProgress.setProgress(0.6);
            }
        }
    }

    /** Validates date and number of people input. */
    private boolean validatePart1() {
        if (txtFieldNumberOfPeople.getText().isEmpty()
                || !BistroUtilities.isNumeric(txtFieldNumberOfPeople.getText())
                || Integer.parseInt(txtFieldNumberOfPeople.getText()) <= 0) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Enter a valid number of people."
            );
            return false;
        }
        if (datePickerOrder.getValue() == null) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Select a valid date."
            );
            return false;
        }
        comboBoxTime.getItems().clear();
        comboBoxTime.setDisable(true);
        return true;
    }

    /** Validates time selection. */
    private boolean validatePart2() {
        if (comboBoxTime.getValue() == null
                || comboBoxTime.getValue().isBlank()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Select a valid time."
            );
            return false;
        }
        return true;
    }

    /** Validates email / phone details for non-subscribers. */
    private boolean validatePart3() {
        if (txtFieldEmail.getText().isBlank()
                && txtFieldPhoneNumber.getText().isBlank()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Enter either email or phone number."
            );
            return false;
        }
        if (!txtFieldEmail.getText().isBlank()
                && !BistroUtilities.isValidEmail(txtFieldEmail.getText())) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Enter a valid email."
            );
            return false;
        }
        if (!txtFieldPhoneNumber.getText().isBlank()
                && !BistroUtilities.isValidPhoneNumber(
                txtFieldPhoneNumber.getText())) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Enter a valid phone number."
            );
            return false;
        }
        return true;
    }

    /** Sends an order creation request to the server. */
    private void createOrder() {
        String email = txtFieldEmail.getText().isBlank() ? null : txtFieldEmail.getText();
        String phoneNumber = txtFieldPhoneNumber.getText().isBlank() ? null : txtFieldPhoneNumber.getText();
        controller.requestOrderCreation(
                new OrderRequest(
                        null,
                        Integer.parseInt(txtFieldNumberOfPeople.getText()),
                        LocalDateTime.of(
                                datePickerOrder.getValue(),
                                LocalTime.parse(comboBoxTime.getValue())
                        ),
                        CustomerStateManager.fillSubscriberIDDetails(),
                        email,
                        phoneNumber
                )
        );
        wizardStep = WizardStep.PART1;
    }

    /** Populates available times in the time selector. */
    private void populateAvailableTimes(List<LocalTime> times) {
        comboBoxTime.getItems().clear();
        if (times.isEmpty()) {
            comboBoxTime.setPromptText("No times available");
        } else {
            times.forEach(t -> comboBoxTime.getItems().add(t.toString()));
            comboBoxTime.setPromptText("Select available time");
            comboBoxTime.setDisable(false);
        }
    }

    /** Applies UI setup once the scene is displayed. */
    private void adjustFormWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o, ow, nw) -> {
                    if (nw != null) {
                        comboBoxTime.setDisable(true);
                        adjustFormToWizardStep();
                        setDatePicker();
                    }
                });
            }
        });
    }

    /** Configures date picker to disable past dates. */
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

    /** Clears all user input fields. */
    private void clearForm() {
        txtFieldNumberOfPeople.clear();
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
        datePickerOrder.setValue(null);
        comboBoxTime.getItems().clear();
        comboBoxTime.setDisable(true);
    }
}