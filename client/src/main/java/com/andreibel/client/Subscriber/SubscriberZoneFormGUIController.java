package com.andreibel.client.Subscriber;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * GUI controller for the subscriber zone screen.
 *
 * <p>This controller represents the main menu for a logged-in subscriber.
 * It provides navigation options such as viewing order history, creating a new
 * order, confirming arrival to get a table, editing subscriber information,
 * and returning to the main screen.</p>
 *
 * <p>The controller personalizes the UI by displaying the subscriber's name,
 * which is retrieved from {@link CustomerStateManager}.</p>
 */
public class SubscriberZoneFormGUIController {

    /**
     * Label displaying a personalized greeting to the subscriber.
     */
    @FXML
    private Label lblSubscriber;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>This method retrieves the logged-in subscriber's information from
     * {@link CustomerStateManager} and displays a personalized welcome message.</p>
     */
    @FXML
    private void initialize() {
        lblSubscriber.setText(
                "Hi " +
                        CustomerStateManager.getInstance().getSubscriber().getName() +
                        ", please select the following options:"
        );
    }

    /**
     * Navigates to the subscriber's order history screen.
     *
     * @param event the action event triggered by clicking the order history button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonOrderHistoryClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Subscriber/OrderListForm.fxml",
                "Bistro Restaurant - Orders History"
        );
    }

    /**
     * Navigates to the order creation screen.
     *
     * @param event the action event triggered by clicking the order now button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonOrderNowClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Order/OrderForm.fxml",
                "Bistro Restaurant - Create Order"
        );
    }

    /**
     * Navigates to the table arrival confirmation screen.
     *
     * @param event the action event triggered by clicking the get table button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonGetTableClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Table/GetTableForm.fxml",
                "Bistro Restaurant - Confirm Arrival"
        );
    }

    /**
     * Navigates back to the main application screen.
     *
     * @param event the action event triggered by clicking the back button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }

    /**
     * Navigates to the subscriber information editing screen.
     *
     * @param event the action event triggered by clicking the edit info button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonEditInfoClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node)event.getSource(),
                "/Subscriber/SubscriberEditForm.fxml",
                "Bistro Restaurant - Edit Subscriber Info"
        );
    }
}
