package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClient;
import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;


public class RegisteredSubscribersFormGUIController implements IServerResponseListener {

    @FXML
    private TableView<SubscriberResponse> tblSubscribers;
    @FXML
    private TableColumn<SubscriberResponse, Integer> colSubscriberId;
    @FXML
    private TableColumn<SubscriberResponse, String> colName;
    @FXML
    private TableColumn<SubscriberResponse, String> colEmail;
    @FXML
    private TableColumn<SubscriberResponse, String> colPhone;
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button btnGoBack;

    private BistroClientController controller;

    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        setTabView();
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
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
        if (message.getType() == APICallType.GET_ALL_SUBSCRIBERS_RESPONSE) {
            Platform.runLater(() -> {
                List<SubscriberResponse> subscribers = ((List<SubscriberResponse>) message.getData());
                for (SubscriberResponse subscriber : subscribers)
                    tblSubscribers.getItems().add(subscriber);
            });
        }
        else if (message.getType() == APICallType.GET_ALL_SUBSCRIBERS_ERROR){
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, unable to fetch all subscribers.");
        }
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        tblSubscribers.getItems().clear();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    @FXML
    private void setTabView(){
        colSubscriberId.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void onSceneShown(){
        controller.requestAllSubscribersInfo();
    }
}
