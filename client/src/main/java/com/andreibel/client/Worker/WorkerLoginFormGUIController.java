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
 * <p>This controller is responsible for authenticating workers by sending
 * login requests to the server and handling the corresponding responses.</p>
 *
 * <p>The controller registers itself as a listener to
 * {@link BistroClientController} and reacts only to worker-login-related
 * responses.</p>
 *
 * <p>Upon successful authentication, the worker's name and manager status
 * are stored in {@link WorkerStateManager} to allow global access to the
 * worker's session information throughout the application.</p>
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
     * Button used to navigate back to the main form.
     */
    @FXML
    private Button btnGoBack;

    /**
     * Singleton controller responsible for client-server communication.
     */
    private BistroClientController controller;

    /**
     * Initializes the worker login form controller after the FXML has been loaded.
     *
     * <p>This method retrieves the singleton instance of
     * {@link BistroClientController} and registers this controller
     * as a server response listener.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
    }

    /**
     * Handles server responses related to worker login attempts.
     *
     * <p>This method processes responses related to worker authentication,
     * including successful login responses and login errors. All unrelated
     * message types are ignored.</p>
     *
     * <p>If the worker does not exist or the response data is {@code null},
     * an informative error message is displayed to the user.</p>
     *
     * <p>On successful login, the worker's name and manager status are saved
     * in {@link WorkerStateManager} for later use.</p>
     *
     * <p>If a server-side error occurs during login, an error message is
     * displayed.</p>
     *
     * @param message the message received from the server
     */
    @Override
    public void onServerResponse(Message message) {
        if (message.getType() == APICallType.WORKER_LOGIN_RESPONSE) {
            if (message.getData() == null) {
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Worker does not exist."
                );
                return;
            }

            WorkerResponse response = (WorkerResponse) message.getData();
            WorkerStateManager.getInstance().setWorkerName(txtFieldStaffName.getText());
            WorkerStateManager.getInstance().setManager(response.isManager());

        } else if (message.getType() == APICallType.WORKER_LOGIN_ERROR) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to a server error, login could not be completed."
            );
        }
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
     * Navigates back to the main application form.
     *
     * @param event the action event triggered by clicking the go-back button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }
}
