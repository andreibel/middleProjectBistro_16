package com.andreibel.client.Main;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

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

    /**
     * Initializes the main form controller after the FXML has been loaded.
     *
     * <p>This method ensures that the {@link CustomerStateManager} singleton
     * instance is created and ready for use.</p>
     */
    @FXML
    private void initialize() {
        CustomerStateManager.getInstance();
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
                "Bistro - Create your Order"
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
                "Bistro - Get Table"
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
                "Bistro - Cancel Order"
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
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Subscriber/SubscriberLoginForm.fxml",
                "Bistro - Subscriber Login"
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
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Worker/WorkerLoginForm.fxml",
                "Bistro - Worker Login"
        );
    }
}
