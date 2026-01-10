package com.andreibel.client.Worker;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.WorkerStateManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
        String workerName = WorkerStateManager.getInstance().getWorker().getWorkerName();
        lblTitle.setText("Hello " + (workerName != null ? workerName : "") + ", select an option below to proceed");
        adjustFormWhenSceneIsShown();
        adjustFormBasedOnWorkerType();
    }

    @FXML
    private void onRegisterButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/SubscriberRegisterForm.fxml",
                "Bistro Restaurant - Register new Subscriber");
    }

    @FXML
    private void onSubscribersReportButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/SubscriberReportsForm.fxml",
                "Bistro Restaurant - Subscribers Report");
    }

    @FXML
    private void onSchedulesReportButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/ScheduleReportsForm.fxml",
                "Bistro Restaurant - Schedules Report");
    }

    @FXML
    private void onViewCurrentDiningButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/CurrentDiningForm.fxml",
                "Bistro Restaurant - Dining at this time");
    }

    @FXML
    private void onRegisterWorkerButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/WorkerRegisterForm.fxml",
                "Bistro Restaurant - Register new Worker");
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant");
    }

    @FXML
    private void onChangeRestaurantLayoutButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/EditRestaurantLayoutForm.fxml",
                "Bistro Restaurant - Change Layout of Restaurant");
    }

    @FXML
    private void onViewCurrentWaitingButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/CurrentWaitingListForm.fxml",
                "Bistro Restaurant - Current on Waiting List");
    }

    @FXML
    private void onViewCurrentOrderButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/ActiveOrdersForm.fxml",
                "Bistro Restaurant - Current Active Orders");
    }

    @FXML
    private void onChangeBistroButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(),
                "/Worker/ChangeBistroTimeForm.fxml",
                "Bistro Restaurant - Change Bistro Time / Add Special Event");
    }

    @FXML
    private void onSubListButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Worker/RegisteredSubscribersForm.fxml", "Bistro Restaurant - Subscriber List");
    }

    /**
     * Adjust the manager section when the scene is shown to ensure visibility is correct.
     */
    private void adjustFormWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> adjustFormBasedOnWorkerType());
                    }
                });
            }
        });
    }

    /**
     * Show or hide manager-specific options based on current worker type.
     */
    private void adjustFormBasedOnWorkerType() {
        boolean isManager = WorkerStateManager.getInstance().getWorker().isManager();
        vBoxManager.setVisible(isManager);
        vBoxManager.setManaged(isManager);
    }
}
