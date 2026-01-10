package com.andreibel.client.Main;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.client.util.WorkerStateManager;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * GUI controller for the main application screen.
 *
 * <p>This controller serves as the central navigation hub of the Bistro client
 * application. It allows users to navigate to different forms such as order
 * creation, table arrival, order cancellation, subscriber login, and worker
 * login.</p>
 *
 * <p>The controller initializes the {@link CustomerStateManager} to ensure
 * customer-related state is available throughout the application lifecycle.</p>
 *
 * <p>All navigation between screens is handled via
 * {@link BistroUtilities#switchScreen(Node, String, String)}, which
 * manages scene transitions and scene caching.</p>
 */
public class MainFormGUIController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button btnSubscriber;
    @FXML
    private Button btnWorker;
    /**
     * Initializes the main form controller after the FXML has been loaded.
     *
     * <p>This method ensures that the {@link CustomerStateManager} singleton
     * instance is created and ready for use.</p>
     */
    @FXML
    private void initialize() {
//        CustomerStateManager.getInstance().setSubscriber(new SubscriberResponse());
//        WorkerStateManager.getInstance().setManager(true);
        System.out.println(WorkerStateManager.getInstance().toString());
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
                (Node)event.getSource(),
                "/Order/OrderForm.fxml",
                "Bistro Restaurant - Create your Order"
        );
    }

    /**
     * Navigates to the table arrival form.
     *
     * @param event the action event triggered by clicking the arrived button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onArrivedButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
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
                (Node)event.getSource(),
                "/Order/CancelOrderForm.fxml",
                "Bistro Restaurant - Cancel Order"
        );
    }

    /**
     * Navigates to the subscriber login form.
     *
     * @param event the action event triggered by clicking the subscriber button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onSubscriberButtonClicked(ActionEvent event) throws IOException {
        if (CustomerStateManager.getInstance() != null && CustomerStateManager.hasSubscriberLoggedIn())
            BistroUtilities.switchScreen((Node)event.getSource(), "/Subscriber/SubscriberZoneForm.fxml", "Bistro Restaurant - Subscriber Area");
        else
            BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Subscriber/SubscriberLoginForm.fxml",
                "Bistro Restaurant - Subscriber Login"
        );
    }

    /**
     * Navigates to the worker login form.
     *
     * @param event the action event triggered by clicking the worker button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onWorkerButtonClicked(ActionEvent event) throws IOException {
        if (WorkerStateManager.hasWorkerLoggedIn())
            BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
        else
            BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Worker/WorkerLoginForm.fxml",
                "Bistro Restaurant - Worker Login"
        );
    }
    /**
     * Handles the "No Order" button click event.
     *
     * <p>When invoked, this method switches the current scene to the
     * "Dine-In without Order" form, allowing guests to be seated
     * without placing an order in advance.</p>
     *
     * @param event the action event triggered by clicking the button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onNoOrderButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(), "/Order/NoOrderForm.fxml", "Bistro Restaurant - Dine-In without Order");
    }

    @FXML
    private void onPayOrderButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node)event.getSource(), "/Table/LeaveTableForm.fxml", "Bistro Restaurant - Pay for order");
    }

    private void updateMainFormWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        if (CustomerStateManager.getInstance() != null && CustomerStateManager.hasSubscriberLoggedIn())
                            btnSubscriber.setText("Subscriber Area");
                        if (WorkerStateManager.getInstance() != null && WorkerStateManager.hasWorkerLoggedIn())
                            btnWorker.setText("Staff Area");
                    }
                });
            }
        });
    }
}


