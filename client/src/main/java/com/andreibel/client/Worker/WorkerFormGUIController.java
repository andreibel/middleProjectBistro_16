package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.WorkerStateManager;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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
    }

    @FXML
    private void onRegisterButtonClicked(ActionEvent event) {}
    @FXML
    private void onSubscribersReportButtonClicked(ActionEvent event) {}
    @FXML
    private void onSchedulesReportButtonClicked(ActionEvent event) {}
    @FXML
    private void onViewCurrentDiningButtonClicked(ActionEvent event) {}
    @FXML
    private void onRegisterWorkerButtonClicked(ActionEvent event) {}
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) {}

}
