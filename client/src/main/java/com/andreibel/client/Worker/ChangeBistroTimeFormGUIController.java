package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.BistroTimeRequest;
import com.andreibel.message.DTO.SpecialDayRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ChangeBistroTimeFormGUIController implements IServerResponseListener {

    @FXML
    private TextField txtFieldOpen;
    @FXML
    private TextField txtFieldClose;
    @FXML
    private TextField txtFieldInterval;
    @FXML
    private Button btnUpdateRegular;

    @FXML
    private TextField txtFieldEventName;
    @FXML
    private DatePicker datePickerEvent;
    @FXML
    private TextField txtFieldEventOpen;
    @FXML
    private TextField txtFieldEventClose;
    @FXML
    private TextField txtFieldEventInterval;
    @FXML
    private Button btnCreateNewEvent;

    @FXML
    private Button btnGoBack;
    @FXML
    private AnchorPane rootPane;

    private BistroClientController controller;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        setDatePicker();
        requestBistroTimesWhenSceneIsShown();
        controller.requestBistroTimes();
    }

    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
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

    @FXML
    private void onUpdateRegularButtonClicked(ActionEvent event) {
        if (!isTimesValidated(txtFieldOpen.getText(), txtFieldClose.getText(), txtFieldInterval.getText()))
            return;

        //controller.requestEditBistroTimes(new BistroTimeRequest(LocalTime.parse(txtFieldOpen.getText(), TIME_FORMATTER),
        //        LocalTime.parse(txtFieldClose.getText(), TIME_FORMATTER),
        //        Integer.parseInt(txtFieldInterval.getText())));
    }

    @FXML
    private void onCreateNewEventButtonClicked(ActionEvent event) {
        if (!BistroUtilities.isValidFullName(txtFieldEventName.getText())) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid event name");
            return;
        }

        if (!isValidDate() || !isTimesValidated(txtFieldEventOpen.getText(), txtFieldEventClose.getText(), txtFieldEventInterval.getText()))
            return;

        controller.requestNewEventCreation(
                new SpecialDayRequest(
                        datePickerEvent.getValue(),
                        LocalTime.parse(txtFieldEventOpen.getText(), TIME_FORMATTER),
                        LocalTime.parse(txtFieldEventClose.getText(), TIME_FORMATTER),
                        Integer.parseInt(txtFieldEventInterval.getText())
                )
        );
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    private void requestBistroTimesWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene == null) return;

            newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                if (newWindow != null) {
                    newWindow.setOnShown(e -> controller.requestBistroTimes());
                }
            });
        });
    }

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

    public boolean isValidDate() {
        LocalDate selectedDate = datePickerEvent.getValue();
        if (selectedDate == null) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter event date");
            return false;
        }
        return true;
    }

    public boolean isTimesValidated(String opening, String closing, String interval) {
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
