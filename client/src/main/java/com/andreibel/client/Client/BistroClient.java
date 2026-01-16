package com.andreibel.client.Client;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.Message;
import com.lloseng.ocsf.client.AbstractClient;
import lombok.Setter;

import java.io.IOException;

/**
 * Client-side network handler for the Bistro application.
 *
 * <p>This class manages the TCP connection to the server and delegates
 * incoming messages to the associated {@link BistroClientController}.
 * It extends {@link AbstractClient} from the OCSF framework.</p>
 *
 * <p>Responsibilities include:
 * <ul>
 *     <li>Receiving messages from the server</li>
 *     <li>Forwarding valid messages to the controller</li>
 *     <li>Handling connection and send failures gracefully</li>
 * </ul>
 * </p>
 */
@Setter
public class BistroClient extends AbstractClient {

    /**
     * Controller responsible for handling server responses.
     */
    private BistroClientController controller;

    /**
     * Constructs a new BistroClient with the specified host and port.
     *
     * @param host the server host address
     * @param port the server port number
     */
    public BistroClient(String host, int port) {
        super(host, port);
    }

    /**
     * Handles incoming messages from the server.
     *
     * <p>If the message is of type {@link Message}, it is forwarded to the
     * controller for processing. Messages of unknown types are rejected
     * and an error dialog is displayed.</p>
     *
     * @param msg the message received from the server
     */
    @Override
    protected void handleMessageFromServer(Object msg) {
        if (controller == null) {
            return;
        }

        if (msg instanceof Message m) {
            try {
                controller.handleServerResponse(m);
            } catch (Exception e) {
                throw new RuntimeException("Failed to handle server response", e);
            }
        } else {
            BistroUtilities.showMessage(
                    "Error",
                    "Unknown message from server: " + msg
            );
        }
    }

    /**
     * Sends a message to the server.
     *
     * <p>If sending fails, an error message is displayed to the user.</p>
     *
     * @param msg the message to send
     */
    public void send(Object msg) {
        try {
            sendToServer(msg);
        } catch (IOException e) {
            if (controller != null) {
                BistroUtilities.showMessage(
                        "Error",
                        "Failed to send to server: " + e.getMessage()
                );
            }
        }
    }

    /**
     * Attempts to open a connection to the server.
     *
     * @return true if the connection was opened successfully, false otherwise
     */
    public void connectToServer() throws Exception {
        try {
            openConnection();

        } catch (Exception e) {
            throw new Exception("Failed to open connection", e);
        }

    }
}

