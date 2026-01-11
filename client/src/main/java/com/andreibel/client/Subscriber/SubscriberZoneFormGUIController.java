package com.andreibel.client.Subscriber;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Controller for the subscriber zone screen.
 *
 * <p>This is the main menu for a logged-in subscriber. It provides navigation
 * to view order history, edit subscriber information, or return to the main application screen.</p>
 *
 * <p>The subscriber's name is retrieved from {@link CustomerStateManager} and
 * displayed in a personalized greeting.</p>
 */
public class SubscriberZoneFormGUIController {

    /** Label for personalized subscriber greeting */
    @FXML
    private Label lblSubscriber;

    /** Root pane for the scene */
    @FXML
    private AnchorPane rootPane;

    /**
     * Initializes the controller after the FXML has been loaded.
     * Sets the subscriber greeting when the scene is displayed.
     */
    @FXML
    private void initialize() {
        updateMainLabelWhenSceneIsShown();
    }

    /**
     * Navigates to the subscriber's order history screen.
     *
     * @param event the action event triggered by the button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonOrderHistoryClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Subscriber/SubscriberOrderListForm.fxml",
                "Bistro Restaurant - Orders History"
        );
    }

    /**
     * Navigates back to the main application screen.
     *
     * @param event the action event triggered by the back button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }

    /**
     * Navigates to the subscriber information editing screen.
     *
     * @param event the action event triggered by the edit info button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void onButtonEditInfoClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Subscriber/SubscriberEditForm.fxml",
                "Bistro Restaurant - Edit Subscriber Info"
        );
    }

    /**
     * Updates the greeting label with the subscriber's name
     * when the scene is first displayed.
     */
    private void updateMainLabelWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null && CustomerStateManager.getInstance().getSubscriber() != null) {
                        lblSubscriber.setText(
                                "Hi " + CustomerStateManager.getInstance().getSubscriber().getName()
                        );
                    }
                });
            }
        });
    }
}