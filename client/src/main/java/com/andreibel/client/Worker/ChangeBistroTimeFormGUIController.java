package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.BistroTimeDTO;
import com.andreibel.message.DTO.SpecialDayRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * GUI controller for changing the bistro's opening hours and creating special events.
 *
 * <p>This controller allows staff to update the regular restaurant opening/closing times,
 * set the interval between reservations, and add new events with specific hours.
 * It validates input, communicates with the server, and provides user feedback via messages.</p>
 */
public class ChangeBistroTimeFormGUIController implements IServerResponseListener {

    /** TextField for regular opening time (HH:mm). */
    @FXML
    private TextField txtFieldOpen;

    /** TextField for regular closing time (HH:mm). */
    @FXML
    private TextField txtFieldClose;

    /** TextField for reservation interval in minutes. */
    @FXML
    private TextField txtFieldInterval;

    /** TextField for the name of a special event. */
    @FXML
    private TextField txtFieldEventName;

    /** DatePicker for selecting the date of a special event. */
    @FXML
    private DatePicker datePickerEvent;

    /** TextField for opening time of the special event. */
    @FXML
    private TextField txtFieldEventOpen;

    /** TextField for closing time of the special event. */
    @FXML
    private TextField txtFieldEventClose;

    /** TextField for reservation interval of the special event in minutes. */
    @FXML
    private TextField txtFieldEventInterval;

    /** Root pane of the scene used to attach scene listeners. */
    @FXML
    private AnchorPane rootPane;

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /** Formatter for parsing and displaying times in HH:mm format. */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>Sets up the date picker, adds this controller as a server listener,
     * and requests current bistro times when the scene is shown.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        setDatePicker();
        requestBistroTimesWhenSceneIsShown();
    }

    /**
     * Handles responses from the server regarding bistro times or special events.
     *
     * @param message the message received from the server
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_REGULAR_OPEN_TIME_RESPONSE ->
                    fillDataInFields((BistroTimeDTO) message.getData());
            case GET_REGULAR_OPEN_TIME_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to fetch restaurant time.");
            case CHANGE_BISTRO_TIME_RESPONSE ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Successfully changed restaurant times");
            case CHANGE_BISTRO_TIME_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to change restaurant times");
            case ADD_SPECIAL_DAY_RESPONSE ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Successfully added new event to restaurant schedule");
            case ADD_SPECIAL_DAY_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to add new event to restaurant schedule");
        }
    }

    /**
     * Handles clicks on the "Update Regular Times" button.
     *
     * <p>Validates input fields and sends a request to the server to update the regular bistro times.</p>
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    private void onUpdateRegularButtonClicked(ActionEvent event) {
        if (!isTimesValidated(txtFieldOpen.getText(), txtFieldClose.getText(), txtFieldInterval.getText()))
            return;

        controller.requestEditBistroTimes(new BistroTimeDTO(
                LocalTime.parse(txtFieldOpen.getText(), TIME_FORMATTER),
                LocalTime.parse(txtFieldClose.getText(), TIME_FORMATTER),
                Integer.parseInt(txtFieldInterval.getText())
        ));
    }

    /**
     * Handles clicks on the "Create New Event" button.
     *
     * <p>Validates event input fields and sends a request to the server to create a new special event.</p>
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    private void onCreateNewEventButtonClicked(ActionEvent event) {
        if (txtFieldEventName.getText().isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter event name");
            return;
        }

        if (!isValidDate() || !isTimesValidated(txtFieldEventOpen.getText(), txtFieldEventClose.getText(), txtFieldEventInterval.getText()))
            return;

        controller.requestNewEventCreation(new SpecialDayRequest(
                datePickerEvent.getValue(),
                txtFieldEventName.getText(),
                LocalTime.parse(txtFieldEventOpen.getText(), TIME_FORMATTER),
                LocalTime.parse(txtFieldEventClose.getText(), TIME_FORMATTER),
                Integer.parseInt(txtFieldEventInterval.getText())
        ));
    }

    /**
     * Handles clicks on the "Go Back" button.
     *
     * <p>Clears the form and navigates back to the staff main screen.</p>
     *
     * @param event the action event triggered by clicking the button
     * @throws IOException if the FXML cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    /**
     * Requests current bistro times from the server when the scene is shown.
     */
    private void requestBistroTimesWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        controller.requestBistroTimes();
                    }
                });
            }
        });
    }

    /**
     * Configures the date picker to disable past dates.
     */
    private void setDatePicker() {
        datePickerEvent.setEditable(false);
        datePickerEvent.setPromptText("Select Event Date");
        datePickerEvent.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    /**
     * Fills the regular bistro time fields with data from the server.
     *
     * @param response the DTO containing regular open, close, and interval
     */
    private void fillDataInFields(BistroTimeDTO response) {
        txtFieldOpen.setText(response.getStartTime().toString());
        txtFieldClose.setText(response.getEndTime().toString());
        txtFieldInterval.setText(String.valueOf(response.getInterval()));
    }

    /**
     * Validates that the selected event date is not empty.
     *
     * @return true if a valid date is selected, false otherwise
     */
    private boolean isValidDate() {
        if (datePickerEvent.getValue() == null) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter event date");
            return false;
        }
        return true;
    }

    /**
     * Validates that opening, closing, and interval fields are correct.
     *
     * <p>Checks for empty fields, proper time format, numeric interval, interval bounds, and logical time ordering.</p>
     *
     * @param opening opening time string
     * @param closing closing time string
     * @param interval interval string
     * @return true if all inputs are valid, false otherwise
     */
    private boolean isTimesValidated(String opening, String closing, String interval) {
        if (opening == null || opening.isEmpty() ||
                closing == null || closing.isEmpty() ||
                interval == null || interval.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Time and interval fields cannot be empty.");
            return false;
        }

        try {
            LocalTime openingTime = LocalTime.parse(opening, TIME_FORMATTER);
            LocalTime closingTime = LocalTime.parse(closing, TIME_FORMATTER);

            if (!closingTime.isAfter(openingTime)) {
                BistroUtilities.showMessage("Bistro Restaurant", "Closing time must be after opening time.");
                return false;
            }

            if (!BistroUtilities.isNumeric(interval)) {
                BistroUtilities.showMessage("Bistro Restaurant", "Interval must be numeric.");
                return false;
            }

            int parsedInterval = Integer.parseInt(interval);
            if (parsedInterval < 15 || parsedInterval > 60) {
                BistroUtilities.showMessage("Bistro Restaurant", "Interval must be between 15 and 60.");
                return false;
            }

            return true;
        } catch (DateTimeParseException e) {
            BistroUtilities.showMessage("Bistro Restaurant", "Invalid time format. Please use HH:mm (e.g. 09:30).");
            return false;
        }
    }

    /**
     * Clears all input fields on the form.
     */
    private void clearForm() {
        txtFieldOpen.clear();
        txtFieldClose.clear();
        txtFieldInterval.clear();
        txtFieldEventName.clear();
        txtFieldEventOpen.clear();
        txtFieldEventClose.clear();
        txtFieldEventInterval.clear();
    }
}