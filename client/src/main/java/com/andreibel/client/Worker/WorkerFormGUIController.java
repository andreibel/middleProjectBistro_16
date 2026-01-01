package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.WorkerStateManager;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class WorkerFormGUIController {
    @FXML
    private Label lblTitle;
    @FXML
    private VBox vBoxManager;
    @FXML
    private AnchorPane rootPane;

    @FXML
    private void initialize() {
        lblTitle.setText("Hello " + WorkerStateManager.getInstance().getWorkerName() + ", select an option below to proceed.");
        adjustFormBasedOnWorkerType();
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            adjustFormBasedOnWorkerType();
                        });
                    }
                });
            }
        });
    }

    @FXML
    private void onRegisterButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/SubscriberRegisterForm.fxml", "Bistro Restaurant - Register new Subscriber");
    }
    @FXML
    private void onSubscribersReportButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/SubscriberReportsForm.fxml", "Bistro Restaurant - Subscribers Report");
    }
    @FXML
    private void onSchedulesReportButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/ScheduleReportsForm.fxml", "Bistro Restaurant - Schedules Report");
    }
    @FXML
    private void onViewCurrentDiningButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/CurrentDiningForm.fxml", "Bistro Restaurant - Dining at this time");
    }
    @FXML
    private void onRegisterWorkerButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/WorkerRegisterForm.fxml", "Bistro Restaurant - Register new Worker");
    }
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Main/MainForm.fxml", "Bistro Restaurant");
    }

    @FXML
    private void onChangeRestaurantLayoutButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/EditRestaurantLayoutForm.fxml", "Bistro Restaurant - Change Layout of Restaurant");
    }
    @FXML
    private void onViewCurrentWaitingButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/CurrentWaitingForm.fxml", "Bistro Restaurant - Current on Waiting List");
    }

    @FXML
    private void onViewCurrentOrderButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/ActiveOrdersForm.fxml", "Bistro Restaurant - Current Active Orders");
    }

    @FXML
    private void onChangeBistroButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/ChangeBistroTimeForm.fxml", "Bistro Restaurant - Change Bistro Time / Add Special Event");
    }

    private void adjustFormBasedOnWorkerType(){
        if (!WorkerStateManager.getInstance().isManager())
            vBoxManager.setVisible(false);
        else vBoxManager.setVisible(true);
    }

}
