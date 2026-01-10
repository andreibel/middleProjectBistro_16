package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.WorkerNewRequest;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class WorkerRegisterFormGUIController implements IServerResponseListener {

    @FXML
    private TextField txtFieldWorkerName;
    @FXML
    private TextField txtFieldPassword;
    @FXML
    private CheckBox chkBoxManager;
    @FXML
    private Button btnRegisterWorker;
    @FXML
    private Button btnGoBack;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        clearForm();
    }

    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case WORKER_CREATE_RESPONSE -> {
                clearForm();
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Successfully added a new worker."
                );
            }
            case WORKER_CREATE_ERROR ->
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            "Due to server error, it was unable to add a new worker."
                    );
        }
    }

    @FXML
    private void onButtonRegisterWorkerClicked(ActionEvent event) {
        String workerName = txtFieldWorkerName.getText().trim();
        String password = txtFieldPassword.getText().trim();

        if (workerName.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a worker name");
            return;
        }

        if (password.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a password");
            return;
        }

        if (!BistroUtilities.isValidFullName(workerName)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid worker name");
            return;
        }

        if (!BistroUtilities.isPasswordValid(password)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid password");
            return;
        }
        controller.requestRegisterNewWorker(
                new WorkerNewRequest(workerName, password, chkBoxManager.isSelected())
        );
    }

    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    private void clearForm() {
        txtFieldWorkerName.clear();
        txtFieldPassword.clear();
        chkBoxManager.setSelected(false);
        txtFieldWorkerName.requestFocus();
    }
}
