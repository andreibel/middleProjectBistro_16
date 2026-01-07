package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
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
    private TableColumn<WaitingListResponse, Integer> colWaitingNumber;
    @FXML
    private TableColumn<WaitingListResponse, Integer> colOrderNumber;
    @FXML
    private TableColumn<WaitingListResponse, Integer> colNumberOfGuests;
    @FXML
    private TableColumn<WaitingListResponse, Integer> colSubscriberID;
    @FXML
    private TableColumn<WaitingListResponse, String> colEmail;
    @FXML
    private TableColumn<WaitingListResponse, String> colPhoneNumber;
    @FXML
    private Button btnGoBack;

    private BistroClientController controller;
    private final ObservableList<WaitingListResponse> waitingList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

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
        switch (message.getType()) {
            case GET_WAITING_LIST_RESPONSE -> populateTable((List<WaitingListResponse>) message.getData());
            case GET_WAITING_LIST_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, unable to fetch current waiting list.");
        }
    }

    private void populateTable(List<WaitingListResponse> data) {
        waitingList.setAll(data);
    }

    private void requestWaitingListWhenSceneIsShown() {
        tblViewCurrentWaiting.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> controller.requestCurrentWaitingList());
                    }
                });
            }
        });
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        waitingList.clear();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
