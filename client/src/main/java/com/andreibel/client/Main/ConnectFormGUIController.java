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
import java.util.regex.Pattern;

/**
 * Controller for the Connect Form screen.
 * <p>
 * This screen is the first screen displayed when the client application starts.
 * It allows the user to enter a server IP address (or hostname) and port number
 * in order to establish a connection to the Bistro server.
 * </p>
 *
 * <p>
 * The controller validates the user input, attempts to connect to the server,
 * and transitions to the main application screen only if the connection
 * is successfully established.
 * </p>
 */
public class ConnectFormGUIController {

    /**
     * Regular expression for validating IPv4 addresses.
     */
    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}" +
                            "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$"
            );

    /**
     * Regular expression for validating hostnames
     * (e.g., localhost, my-server, example.com).
     */
    private static final Pattern HOSTNAME_PATTERN =
            Pattern.compile(
                    "^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
                            "(\\.[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
            );

    /**
     * Text field for entering the server IP address or hostname.
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
     * This method validates the input fields, creates a {@link BistroClient},
     * attempts to connect to the server, and transitions to the main form
     * only if the connection is successful.
     * </p>
     *
     * @param event the action event triggered by clicking the Connect button
     */
    @FXML
    private void onConnectClicked(ActionEvent event) {
        String host = txtHost.getText().trim();
        String portText = txtPort.getText().trim();

        if (host.isEmpty() || portText.isEmpty()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Please enter a valid server IP/host and port."
            );
            return;
        }

        if (!isValidHost(host)) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Invalid server address. Example: 127.0.0.1 or localhost"
            );
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
            if (port < 1 || port > 65535) {
                BistroUtilities.showMessage(
                        "Bistro Restaurant",
                        "Port must be between 1 and 65535."
                );
                return;
            }
        } catch (NumberFormatException e) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Port must be a number."
            );
            return;
        }

        try {
            BistroClient client = new BistroClient(host, port);
            BistroClientController.getInstance().attachClient(client);
            client.connectToServer();
            BistroUtilities.switchScreen(
                    (Node) event.getSource(),
                    "/Main/MainForm.fxml",
                    "Bistro Restaurant"
            );


        } catch (Exception ex) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Failed to connect to " + host + ":" + port
            );
        }
    }

    /**
     * Validates whether the given host string is a valid IPv4 address
     * or a valid hostname.
     *
     * @param host the host string to validate
     * @return true if the host is valid, false otherwise
     */
    private boolean isValidHost(String host) {
        return IPV4_PATTERN.matcher(host).matches()
                || HOSTNAME_PATTERN.matcher(host).matches();
    }
}
