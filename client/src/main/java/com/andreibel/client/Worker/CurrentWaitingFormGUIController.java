package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

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

    @Override
    public void onServerResponse(Message message) {}

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        tblViewCurrentWaiting.getItems().clear();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    private void onSceneShown(){

    }
}
