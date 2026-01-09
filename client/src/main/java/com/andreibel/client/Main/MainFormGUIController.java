package com.andreibel.client.Main;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.client.util.WorkerStateManager;
import com.andreibel.message.DTO.SubscriberResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

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
    /**
     * Initializes the main form controller after the FXML has been loaded.
     *
     * <p>This method ensures that the {@link CustomerStateManager} singleton
     * instance is created and ready for use.</p>
     */
    @FXML
    private void initialize() {
        var fontUrl = getClass().getResource("/fonts/MaterialSymbolsOutlined-VariableFont_FILL,GRAD,opsz,wght.ttf");
        System.out.println("FONT URL = " + fontUrl);
        if (fontUrl != null) Font.loadFont(fontUrl.toExternalForm(), 16);

        var cssUrl = getClass().getResource("/com/andreibel/client/Main/main-form.css");
        System.out.println("CSS URL = " + cssUrl);

        // Add stylesheet when scene becomes available (safe)
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && cssUrl != null) {
                if (!newScene.getStylesheets().contains(cssUrl.toExternalForm())) {
                    newScene.getStylesheets().add(cssUrl.toExternalForm());
                }
            }
        });
        CustomerStateManager.getInstance().setSubscriber(new SubscriberResponse());
        WorkerStateManager.getInstance().setManager(true);
        updateMainFormWhenSceneIsShown();
        if (CustomerStateManager.getInstance() != null && CustomerStateManager.hasSubscriberLoggedIn())
            btnSubscriber.setText("Subscriber Area");
    }

    private static String ms(String hex) {
        int code = Integer.parseInt(hex, 16);
        return new String(Character.toChars(code));
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

    private void updateMainFormWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            if (CustomerStateManager.getInstance() != null && CustomerStateManager.hasSubscriberLoggedIn())
                                btnSubscriber.setText("Subscriber Area");
                        });
                    }
                });
            }
        });
    }
}
