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

import java.io.IOException;

public class WorkerFormGUIController {
    @FXML
    private Label lblTitle;
    @FXML
    private Button btnRegister;
    @FXML
    private Button btnSubscribersReport;
    @FXML
    private Button btnSchedulesReport;
    @FXML
    private Button btnViewCurrentDining;
    @FXML
    private Button btnRegisterWorker;
    @FXML
    private Button btnGoBack;

    @FXML
    private void initialize() {
        lblTitle.setText("Hello " + WorkerStateManager.getInstance().getWorkerName() + ", select an option below to proceed.");
        adjustFormBasedOnWorkerType();
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

    private void adjustFormBasedOnWorkerType(){
        if (!WorkerStateManager.getInstance().isManager()){
            btnRegisterWorker.setVisible(false);
            btnSchedulesReport.setVisible(false);
        }
    }

}
