package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.WorkerStateManager;
import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.DTO.WorkerResponse;
import com.andreibel.message.Message;
import javafx.application.Platform;
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
    public void onServerResponse(Message message) throws IOException {
        switch (message.getType()) {
            case WORKER_LOGIN_RESPONSE -> {
                WorkerResponse worker = (WorkerResponse) message.getData();
                if (worker == null) {
                    BistroUtilities.showMessage(
                            "Bistro Restaurant", "Invalid worker credentials."
                    );
                    return;
                }
                clearFields();
                WorkerStateManager.getInstance().setWorker(worker);

                BistroUtilities.switchScreen(
                        btnLogin,
                        "/Worker/WorkerForm.fxml",
                        "Bistro Restaurant"
                );
            }
            case WORKER_LOGIN_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, we were unable to log you in. Please try again later."
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
        String staffName = txtFieldStaffName.getText().trim();
        String password = txtFieldPassword.getText().trim();

        if (staffName.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter your staff name.");
            return;
        }

        if (password.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter your password.");
            return;
        }

        controller.requestWorkerLogin(new WorkerAuth(staffName, password));
    }

    /**
     * Navigates back to the main application form.
     *
     * @param event the action event triggered by clicking the go-back button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearFields();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }
    /**
     * Clears the input fields in the login form.
     *
     * <p>Specifically, it resets the text fields for staff name and password,
     * allowing the user to enter new credentials without manually deleting previous input.</p>
     */
    private void clearFields() {
        txtFieldStaffName.clear();
        txtFieldPassword.clear();
    }
}
