package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
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
    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        lblTitle.setText("Current Waiting List for: " + getCurrentDate());
        setTabView();
        onSceneShown();
        tblViewCurrentWaiting.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            onSceneShown();
                        });
                    }
                });
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message) {
        if (message.getType() == APICallType.GET_WAITING_LIST_RESPONSE){
            Platform.runLater(() -> {
                List<WaitingListResponse> waitingList = (List<WaitingListResponse>) message.getData();
                for (WaitingListResponse waitingListResponse : waitingList) {
                    tblViewCurrentWaiting.getItems().add(waitingListResponse);
                }
            });
        }
        else if (message.getType() == APICallType.ADD_TO_WAITING_LIST_ERROR){
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, we're unable to fetch the current dating list");
        }
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        tblViewCurrentWaiting.getItems().clear();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    @FXML
    private void setTabView(){
        colWaitingNumber.setCellValueFactory(new PropertyValueFactory<>("waitingNumber"));
        colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colNumberOfGuests.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        colSubscriberID.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void onSceneShown(){
        controller.requestCurrentWaitingList();
    }

    private void getCurrentDate(){

    }

}
