package com.andreibel.client.Client;

import com.andreibel.message.Message;

import java.io.IOException;

/**
 * Listener interface for receiving server responses that were transferred
 * from the {@link BistroClientController}.
 * <p>
 * The {@code BistroClientController} acts as an intermediary between the
 * network layer ({@code BistroClient}) and the graphical user interface.
 * When a message is received from the server, the controller forwards
 * (dispatches) the message to all registered implementations of this
 * interface.
 * </p>
 *
 * <p>
 * Each GUI controller that needs to react to server responses should
 * implement this interface and register itself in the
 * {@code BistroClientController}. Since each API call type is owned by
 * a specific form, the implementing GUI controller is responsible for
 * handling only the messages relevant to it (for example, by checking
 * the message type).
 * </p>
 *
 * <p>
 * Any updates to the JavaFX user interface must be performed on the
 * JavaFX Application Thread (for example, using {@code Platform.runLater}).
 * </p>
 */
public interface IServerResponseListener {

    /**
     * Invoked by the {@code BistroClientController} when a response
     * message is received from the server and forwarded to the GUI layer.
     *
     * @param message the response {@link Message} received from the server
     */
    void onServerResponse(Message message) throws IOException, Exception;
}
