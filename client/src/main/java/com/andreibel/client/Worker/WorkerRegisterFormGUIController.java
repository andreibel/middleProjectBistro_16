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

/**
 * GUI controller for the worker registration form.
 *
 * <p>This controller handles registering new workers by sending the
 * registration request to the server and processing the response.</p>
 *
 * <p>The controller validates the worker's name and password before
 * sending the request. It also manages UI behavior, such as clearing
 * input fields after a successful registration and navigating back to
 * the main Worker form.</p>
 */
public class WorkerRegisterFormGUIController implements IServerResponseListener {

    /**
     * Text field for entering the new worker's name.
     */
    @FXML
    private TextField txtFieldWorkerName;

    /**
     * Text field for entering the new worker's password.
     */
    @FXML
    private TextField txtFieldPassword;

    /**
     * Checkbox to indicate whether the new worker should have manager privileges.
     */
    @FXML
    private CheckBox chkBoxManager;

    /**
     * Button used to submit the worker registration request.
     */
    @FXML
    private Button btnRegisterWorker;

    /**
     * Button used to navigate back to the Worker main form.
     */
    @FXML
    private Button btnGoBack;

    /**
     * Singleton controller responsible for client-server communication.
     */
    private BistroClientController controller;

    /**
     * Initializes the worker registration form controller after the FXML has been loaded.
     *
     * <p>This method retrieves the singleton instance of
     * {@link BistroClientController} and registers this controller
     * as a server response listener.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        clearForm();
    }

    /**
     * Handles server responses related to worker registration attempts.
     *
     * <p>This method processes responses from the server regarding
     * successful or failed creation of a new worker. UI messages are
     * displayed accordingly, and the form is cleared after success.</p>
     *
     * @param message the message received from the server
     */
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

    /**
     * Sends a worker registration request to the server using the entered information.
     *
     * <p>Validates that the worker name and password are not empty and
     * comply with expected formats. If the data is valid, sends a
     * {@link WorkerNewRequest} to the server.</p>
     *
     * @param event the action event triggered by clicking the register button
     */
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

    /**
     * Navigates back to the Worker main form and clears the registration form.
     *
     * @param event the action event triggered by clicking the go-back button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    /**
     * Clears all input fields and resets the manager checkbox.
     *
     * <p>Also sets focus on the worker name field to improve usability.</p>
     */
    private void clearForm() {
        txtFieldWorkerName.clear();
        txtFieldPassword.clear();
        chkBoxManager.setSelected(false);
        txtFieldWorkerName.requestFocus();
    }
}