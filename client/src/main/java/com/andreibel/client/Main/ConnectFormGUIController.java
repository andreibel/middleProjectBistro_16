package com.andreibel.client.Main;

import com.andreibel.client.Client.BistroClient;
import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.util.BistroUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

/**
 * Controller class for the Connect Form screen.
 * <p>
 * This screen is the first screen displayed when the client application starts.
 * It allows the user to enter a server IP address and port number
 * in order to establish a connection to the Bistro server.
 * </p>
 *
 * <p>
 * Upon a successful connection, the application transitions to the main form.
 * The controller also ensures that the client connection is properly closed
 * when the application window is closed.
 * </p>
 */
public class ConnectFormGUIController {

    /**
     * Text field for entering the server IP address.
     */
    @FXML
    private TextField txtHost;

    /**
     * Text field for entering the server port.
     */
    @FXML
    private TextField txtPort;

    /**
     * Reference to the main application stage.
     * Used to register a close-request handler in order
     * to gracefully close the client connection.
     */
    @Setter
    @FXML
    private Stage mainStage;

    /**
     * Handles the Connect button click event.
     * <p>
     * Validates the user input, creates a {@link BistroClient},
     * connects to the server, and attaches the client to the
     * {@link BistroClientController}.
     * </p>
     *
     * <p>
     * If the connection is established successfully, the screen
     * transitions to the main application form.
     * </p>
     *
     * @param event the action event triggered by clicking the Connect button
     * @throws Exception if an unexpected error occurs during connection
     */
    @FXML
    private void onConnectClicked(ActionEvent event) throws Exception {
        String host = txtHost.getText().trim();
        String portText = txtPort.getText().trim();

        // Validate input
        if (host.isEmpty() || portText.isEmpty()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter a valid Server IP Address and Port."
            );
            return;
        }

        int port = Integer.parseInt(portText);

        // Log connection attempt
        System.out.println("Connecting to " + host + ":" + port);

        // Create client and connect to server
        BistroClient client = new BistroClient(host, port);
        BistroClientController.getInstance().attachClient(client);
        client.connectToServer();

        // Ensure connection is closed on application exit
        mainStage.setOnCloseRequest(windowEvent -> {
            try {
                client.closeConnection();
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to close client connection", e
                );
            }
        });

        // Transition to main form
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }
}
