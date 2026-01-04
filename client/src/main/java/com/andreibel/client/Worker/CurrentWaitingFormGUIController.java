package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CurrentWaitingFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;

    @FXML
    private TableView<WaitingListResponse> tblViewCurrentWaiting;

    @FXML
    private TableColumn<WaitingListResponse, String> colWaitingNumber;
    @FXML
    private TableColumn<WaitingListResponse, String> colOrderNumber;
    @FXML
    private TableColumn<WaitingListResponse, String> colNumberOfGuests;
    @FXML
    private TableColumn<WaitingListResponse, String> colSubscriberID;
    @FXML
    private TableColumn<WaitingListResponse, String> colEmail;
    @FXML
    private TableColumn<WaitingListResponse, String> colPhoneNumber;

    @FXML
    private Button btnGoBack;

    private BistroClientController controller;
    private ObservableList<WaitingListResponse> waitingList;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        waitingList = FXCollections.observableArrayList();
        tblViewCurrentWaiting.setItems(waitingList);
        tblViewCurrentWaiting.setEditable(false);

        initializeTableColumns();
        lblTitle.setText("Current Waiting List for: " + getCurrentDate());
        requestWaitingListWhenSceneIsShown();
        controller.requestCurrentWaitingList();
    }

    private void initializeTableColumns() {
        colWaitingNumber.setCellValueFactory(new PropertyValueFactory<>("waitingNumber"));
        colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colNumberOfGuests.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        colSubscriberID.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onServerResponse(Message message) {

        if (message.getType() == APICallType.GET_WAITING_LIST_RESPONSE) {
            populateTable((List<WaitingListResponse>) message.getData());
        }

        else if (message.getType() == APICallType.ADD_TO_WAITING_LIST_ERROR) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, we're unable to fetch the current waiting list"
            );
        }
    }

    private void populateTable(List<WaitingListResponse> data) {
        waitingList.clear();
        waitingList.addAll(data);
    }

    private void requestWaitingListWhenSceneIsShown() {
        tblViewCurrentWaiting.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event ->
                                controller.requestCurrentWaitingList()
                        );
                    }
                });
            }
        });
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        controller.removeListener(this);
        waitingList.clear();

        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    private String getCurrentDate() {
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
