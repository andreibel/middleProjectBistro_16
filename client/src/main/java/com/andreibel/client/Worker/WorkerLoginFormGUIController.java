package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.WorkerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.message.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * GUI controller for the worker login form.
 *
 * <p>This controller handles worker authentication by sending login requests
 * to the server and processing the corresponding responses.</p>
 *
 * <p>It registers itself as a listener to {@link BistroClientController} and
 * reacts only to {@link APICallType#LOGIN_WORKER_RESPONSE} messages, ensuring
 * that it handles only responses relevant to this form.</p>
 *
 * <p>Upon successful login, the worker's session data (name and manager status)
 * is stored in {@link WorkerStateManager} for global access throughout the
 * application.</p>
 */
public class WorkerLoginFormGUIController implements IServerResponseListener {

    /**
     * Text field for entering the worker's staff name.
     */
    @FXML
    private TextField txtFieldStaffName;

    /**
     * Text field for entering the worker's password.
     */
    @FXML
    private TextField txtFieldPassword;

    /**
     * Button used to submit the login request.
     */
    @FXML
    private Button btnLogin;

    /**
     * Button used to return to the main form.
     */
    @FXML
    private Button btnGoBack;

    /**
     * Singleton controller responsible for client-server communication.
     */
    private BistroClientController controller;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>This method obtains the singleton instance of
     * {@link BistroClientController} and registers this GUI controller
     * as a server response listener.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    /**
     * Handles server responses related to worker login.
     *
     * <p>This method ignores all messages except
     * {@link APICallType#LOGIN_WORKER_RESPONSE}.</p>
     *
     * <p>If the login fails or the worker does not exist, an error message
     * is displayed. Otherwise, the worker's details are stored in
     * {@link WorkerStateManager}.</p>
     *
     * @param message the message received from the server
     */
    @Override
    public void onServerResponse(Message message) {
        if (message.getType() != APICallType.LOGIN_WORKER_RESPONSE) {
            return;
        } else if (message.getData() == null) {
            BistroUtilities.showMessage("Error", "Worker does not exist");
            return;
        }

        WorkerResponse response = (WorkerResponse) message.getData();
        WorkerStateManager.getInstance().setWorkerName(txtFieldStaffName.getText());
        WorkerStateManager.getInstance().setManager(response.isManager());
    }

    /**
     * Sends a worker login request to the server using the provided credentials.
     *
     * @param event the action event triggered by clicking the login button
     */
    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        controller.requestWorkerLogin(
                txtFieldStaffName.getText(),
                txtFieldPassword.getText()
        );
    }

    /**
     * Navigates back to the main form.
     *
     * @param event the action event triggered by clicking the back button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Main/MainForm.fxml", "Bistro Restaurant");
    }
}
