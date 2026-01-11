package com.andreibel.client.Main;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.client.util.WorkerStateManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * GUI controller for the main application screen.
 *
 * <p>This controller acts as the central navigation hub of the Bistro client
 * application. It allows customers, subscribers, and workers to navigate
 * to the appropriate areas of the system.</p>
 *
 * <p>The controller dynamically updates the UI based on the current
 * authentication state of subscribers and workers.</p>
 *
 * <p>All navigation between screens is performed via
 * {@link BistroUtilities#switchScreen(Node, String, String)},
 * which handles scene switching and caching.</p>
 */
public class MainFormGUIController {

    /**
     * Root pane of the main form.
     */
    @FXML
    private AnchorPane rootPane;

    /**
     * Button for subscriber access (login or subscriber area).
     */
    @FXML
    private Button btnSubscriber;

    /**
     * Button for worker access (login or staff area).
     */
    @FXML
    private Button btnWorker;
    @FXML
    private Button btnWorkerLogout;

    @FXML
    private HBox hBoxWorker;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>This method registers listeners to update the UI when the scene
     * is shown, ensuring that button labels reflect the current login
     * state of subscribers and workers.</p>
     */
    @FXML
    private void initialize() {
        updateMainFormWhenSceneIsShown();
    }

    /**
     * Navigates to the order creation form.
     *
     * @param event the action event triggered by clicking the order button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onOrderButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Order/OrderForm.fxml",
                "Bistro Restaurant - Create your Order"
        );
    }

    /**
     * Navigates to the table arrival confirmation form.
     *
     * @param event the action event triggered by clicking the arrived button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onArrivedButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Table/GetTableForm.fxml",
                "Bistro Restaurant - Confirm Arrival"
        );
    }

    /**
     * Navigates to the order cancellation form.
     *
     * @param event the action event triggered by clicking the cancel button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onCancelButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Order/CancelOrderForm.fxml",
                "Bistro Restaurant - Cancel Order"
        );
    }

    /**
     * Navigates to the subscriber login form or subscriber area,
     * depending on the current authentication state.
     *
     * @param event the action event triggered by clicking the subscriber button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onSubscriberButtonClicked(ActionEvent event) throws IOException {
        if (CustomerStateManager.hasSubscriberLoggedIn()) {
            BistroUtilities.switchScreen(
                    (Node) event.getSource(),
                    "/Subscriber/SubscriberZoneForm.fxml",
                    "Bistro Restaurant - Subscriber Area"
            );
        } else {
            BistroUtilities.switchScreen(
                    (Node) event.getSource(),
                    "/Subscriber/SubscriberLoginForm.fxml",
                    "Bistro Restaurant - Subscriber Login"
            );
        }
    }

    /**
     * Navigates to the worker login form or staff area,
     * depending on the current authentication state.
     *
     * @param event the action event triggered by clicking the worker button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onWorkerButtonClicked(ActionEvent event) throws IOException {
        if (WorkerStateManager.hasWorkerLoggedIn()) {
            BistroUtilities.switchScreen(
                    (Node) event.getSource(),
                    "/Worker/WorkerForm.fxml",
                    "Bistro Restaurant - Staff Area"
            );
        } else {
            BistroUtilities.switchScreen(
                    (Node) event.getSource(),
                    "/Worker/WorkerLoginForm.fxml",
                    "Bistro Restaurant - Worker Login"
            );
        }
    }

    /**
     * Navigates to the "Dine-In without Order" form.
     *
     * <p>This option allows guests to be seated without placing
     * an order in advance.</p>
     *
     * @param event the action event triggered by clicking the button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onNoOrderButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Order/NoOrderForm.fxml",
                "Bistro Restaurant - Dine-In without Order"
        );
    }

    /**
     * Navigates to the payment form for completing an order.
     *
     * @param event the action event triggered by clicking the payment button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onPayOrderButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Table/LeaveTableForm.fxml",
                "Bistro Restaurant - Pay for Order"
        );
    }

    /**
     * Handles the action of logging out the currently authenticated worker.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *     <li>Clears the current worker session by setting the worker in
     *         {@link WorkerStateManager} to {@code null}.</li>
     *     <li>Updates the UI components to reflect the logged-out state by
     *         calling {@link #updateFormBasedOnUserType()}.</li>
     *     <li>Requests a layout pass on the root pane to ensure proper
     *         visibility and layout of all elements.</li>
     * </ul>
     *
     * @param event the action event triggered by clicking the worker logout button
     * @throws IOException if switching UI components causes an input/output error
     */
    @FXML
    private void onWorkerLogoutButtonClicked(ActionEvent event) throws IOException {
        WorkerStateManager.getInstance().setWorker(null);
        updateFormBasedOnUserType();
        rootPane.layout();
    }

    /**
     * Updates the visibility and labels of UI components based on the currently
     * logged-in user type.
     *
     * <p>This method adjusts the main form elements to reflect the user's
     * authentication state:</p>
     * <ul>
     *     <li>If no subscriber or worker is logged in, shows worker login options
     *         and subscriber buttons appropriately.</li>
     *     <li>If a subscriber is logged in, hides worker-related elements and
     *         updates the subscriber button to indicate access to the subscriber area.</li>
     *     <li>If a worker is logged in, shows worker-related buttons, hides
     *         subscriber buttons, and enables the worker logout button.</li>
     * </ul>
     *
     * <p>All visibility changes also update the managed property to ensure proper
     * layout behavior in the UI.</p>
     */
    private void updateFormBasedOnUserType(){
        if (!CustomerStateManager.hasSubscriberLoggedIn() && !WorkerStateManager.hasWorkerLoggedIn()) {
            btnWorker.setText("I'm a Staff");
            btnSubscriber.setText("Subscriber Login");
            hBoxWorker.setVisible(true);
            hBoxWorker.setManaged(true);
            btnWorkerLogout.setVisible(false);
            btnWorkerLogout.setManaged(false);
            btnSubscriber.setVisible(true);
            btnSubscriber.setManaged(true);
        }
        else{
            if (CustomerStateManager.hasSubscriberLoggedIn()) {
                btnSubscriber.setText("Subscriber Area");
                hBoxWorker.setVisible(false);
                hBoxWorker.setManaged(false);
            }
            else{
                hBoxWorker.setVisible(true);
                hBoxWorker.setManaged(true);
                btnWorkerLogout.setVisible(false);
                btnWorkerLogout.setManaged(false);
            }
            if (WorkerStateManager.hasWorkerLoggedIn()) {
                btnWorker.setText("Staff Area");
                btnWorkerLogout.setVisible(true);
                btnWorkerLogout.setManaged(true);
                btnSubscriber.setVisible(false);
                btnSubscriber.setManaged(false);
            }
            else{
                btnSubscriber.setVisible(true);
                btnSubscriber.setManaged(true);
            }
        }
    }

    /**
     * Updates the main form UI when the scene becomes visible.
     *
     * <p>This method ensures that button labels reflect the current
     * login state of subscribers and workers.</p>
     */
    private void updateMainFormWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        updateFormBasedOnUserType();
                    }
                });
            }
        });
    }
}