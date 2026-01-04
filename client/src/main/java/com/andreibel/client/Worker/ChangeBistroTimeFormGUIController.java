package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.BistroTimeRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

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
        if (message.getType() == APICallType.CHANGE_BISTRO_TIME_RESPONSE) {
            BistroUtilities.showMessage("Bistro Restaurant", "Successfully changed restaurant times");return;
        }
        else if (message.getType() == APICallType.CHANGE_BISTRO_TIME_ERROR) {
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to change restaurant times");
        }
        else if (message.getType() == APICallType.ADD_SPECIAL_DAY_RESPONSE){
            BistroUtilities.showMessage("Bistro Restaurant", "Successfully added new event to restaurant schedule");
        }
        else if (message.getType() == APICallType.ADD_SPECIAL_DAY_ERROR){
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to add new event to restaurant schedule");
        }
    }

    @FXML
    private void onUpdateRegularButtonClicked(ActionEvent event) {
        if (!isTimesValidated(txtFieldOpen.getText(), txtFieldClose.getText(), txtFieldInterval.getText())) return;
        //controller.requestEditBistroTimes(new BistroTimeRequest(LocalTime.parse(txtFieldOpen.getText()), LocalTime.parse(txtFieldClose.getText()), Integer.parseInt(txtFieldInterval.getText())));
    }

    @FXML
    private void onCreateNewEventButtonClicked(ActionEvent event) {

    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");

    }
    private void requestBistroTimesWhenSceneIsShown(){
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event ->
                                controller.requestBistroTimes()
                        );
                    }
                });
            }
        });
    }

    private void setDatePicker(){
        datePickerEvent.setEditable(false);
        datePickerEvent.setPromptText("Select Event Date");
        datePickerEvent.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });
    }

    public boolean isTimesValidated(String opening, String closing, String interval) {
        try {
            LocalTime openingTime = LocalTime.parse(opening, TIME_FORMATTER);
            LocalTime closingTime = LocalTime.parse(closing, TIME_FORMATTER);

            if (!closingTime.isAfter(openingTime)) {
                BistroUtilities.showMessage("Bistro Restaurant", "Closing time must be after opening time.");
                return false;
            }
            if (!BistroUtilities.isNumeric(interval)){
                BistroUtilities.showMessage("Bistro Restaurant", "Interval must be numeric.");
                return false;
            }
            if (Integer.parseInt(interval) < 0){
                BistroUtilities.showMessage("Bistro Restaurant", "Interval must be a positive integer.");
                return false;
            }
            if (Integer.parseInt(interval) < 15 || Integer.parseInt(interval) > 60){
                BistroUtilities.showMessage("Bistro Restaurant", "Interval must be between 15 and 60.");
                return false;
            }
            return true;

        } catch (DateTimeParseException e) {
            BistroUtilities.showMessage("Bistro Restaurant", "Invalid time format. Please use HH:mm (e.g. 09:30).");
            return false;
        }
    }

    private void clearForm(){
        txtFieldOpen.clear();
        txtFieldClose.clear();
        txtFieldEventName.clear();
        txtFieldEventOpen.clear();
        txtFieldEventClose.clear();
    }
}
