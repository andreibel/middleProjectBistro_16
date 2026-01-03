package com.andreibel.server.controller;

import com.andreibel.message.Message;
import com.lloseng.ocsf.server.AbstractServer;
import com.lloseng.ocsf.server.ConnectionToClient;

import java.io.IOException;

import static com.andreibel.server.utils.TUI.serverInputLog;

/**
 * OCSF-based server implementation for the Bistro system.
 *
 * <p>
 * This class extends {@link AbstractServer} and acts as the main networking layer:
 * it receives incoming objects from clients, validates they are {@link Message} instances,
 * routes them by {@code message.getType()} to the appropriate controller, and sends a
 * response back to the client.
 * </p>
 *
 * <p>
 * The server also reports connection lifecycle events (connect/disconnect/exceptions)
 * to the {@link BistroServerGUIController} to allow the GUI to reflect current client state.
 * </p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Initialize controllers used to handle requests.</li>
 *   <li>Dispatch incoming client messages using a switch on message type.</li>
 *   <li>Send response {@link Message} objects back to the client.</li>
 *   <li>Notify GUI controller about connection changes.</li>
 * </ul>
 *
 * @see AbstractServer
 * @see ConnectionToClient
 * @see Message
 * @see OrderController
 * @see SubscriberController
 *
 * @author Andrei Beloziyorove
 */
public class Serve extends AbstractServer {

    /** Controller responsible for order-related operations. */
    OrderController orderController;

    /** Controller responsible for subscriber-related operations. */
    SubscriberController subscriberController;

    WorkerController workerController;
    /**
     * GUI controller reference (optional).
     * Used to update UI on client connect/disconnect/exception events.
     */
    private BistroServerGUIController controller;

    /**
     * Constructs a server that listens on the given port.
     *
     * <p>
     * Initializes internal controllers (Singleton instances) used for handling requests.
     * </p>
     *
     * @param port server port to listen on
     */
    public Serve(int port) {
        super(port);
        orderController = OrderController.getInstance();
        subscriberController = SubscriberController.getInstance();
        workerController = WorkerController.getInstance();
    }

    /**
     * Attaches the GUI controller used for displaying and updating connection state.
     *
     * @param controller GUI controller instance
     */
    public void setGUIController(BistroServerGUIController controller) {
        this.controller = controller;
    }

    /**
     * Handles an incoming message received from a specific client connection.
     *
     * <p>
     * The OCSF framework calls this method whenever the client sends an object.
     * This implementation:
     * </p>
     * <ol>
     *   <li>Validates that {@code msg} is a {@link Message}.</li>
     *   <li>Routes the request by {@code message.getType()} to the correct controller method.</li>
     *   <li>Sends a {@link Message} response back to the client.</li>
     * </ol>
     *
     * <p>
     * If the incoming object is not a {@link Message}, an {@link IllegalArgumentException} is thrown.
     * IO failures while responding are converted into a {@link RuntimeException}.
     * </p>
     *
     * @param msg the object received from the client (expected to be {@link Message})
     * @param client the client connection that sent the object
     *
     * @throws RuntimeException if an {@link IOException} occurs while sending the response
     *                          or if the message type is invalid
     */
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (!(msg instanceof Message message)) {
                throw new IllegalArgumentException("Invalid message type");
            }
            serverInputLog(message);
            Message response = switch (message.getType()) {
                case CREATE_ORDER -> orderController.createOrder(message);
                case DELETE_ORDER -> orderController.deleteOrder(message);
                case GET_ALL_ORDERS_SUB -> orderController.getAllOrdersBySubscriber();
                case GET_ONE_ORDER -> orderController.getOrder(message);
                case ORDER_ARRIVED -> orderController.updateArrives(message);
                case ORDER_LOST_CONFORMATION_CODE -> orderController.lostCode(message);
                case COMPLETE_ORDER -> orderController.closeOrder(message);
                case GET_ALL_TIMES_IN_DATE -> orderController.getAllAvailableTime(message);
                case GET_ALL_SUBSCRIBERS -> subscriberController.getAllSub();
                case SUBSCRIBER_LOGIN -> subscriberController.getSub(message);
                case GET_SUBSCRIBER_ORDERS -> subscriberController.getSubOrders(message);
                case UPDATE_SUBSCRIBER -> subscriberController.updateSub(message);
                case WORKER_LOGIN -> workerController.login(message);
                case WORKER_CREATE -> workerController.createWorker(message);
                case CREATE_SUBSCRIBER -> subscriberController.createSub(message);
                case ADD_SPECIAL_DAY -> null;
                case CHANGE_BISTRO_TIME -> null;
                case GET_ALL_TABLES -> null;
                case EDIT_BISTRO_LAYOUT -> null;
                case SCHEDULES_REPORT -> null;
                case SUBSCRIBER_REPORT -> null;
                case GET_WAITING_LIST -> null;
                case GET_ALL_ACTIVE_ORDER -> null;
                case GET_ALL_ARRIVED_AND_NOT_COMPLETE -> null;
                case ADD_TO_WAITING_LIST -> null;
                case REMOVE_FROM_WAITING_LIST -> null;
                case ARRIVE_WAITING_LIST -> null;
                default -> null;
            };

            client.sendToClient(response);

        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hook method called by OCSF when a client successfully connects.
     *
     * <p>
     * This implementation notifies the GUI controller (if set) to record/display
     * the new connection.
     * </p>
     *
     * @param client the connected client
     */
    @Override
    protected void clientConnected(ConnectionToClient client) {
        controller.addNewConnection(client);
    }

    /**
     * Hook method called by OCSF when a client disconnects.
     *
     * <p>
     * This implementation notifies the GUI controller (if set) to update the
     * displayed connection status.
     * </p>
     *
     * @param client the disconnected client
     */
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        controller.editConnection(client);
    }

    /**
     * Hook method called by OCSF when an exception occurs for a client connection.
     *
     * <p>
     * Logs the exception and updates the GUI controller (if set) to reflect
     * that the client encountered an error / disconnected unexpectedly.
     * </p>
     *
     * @param client the client connection where the exception occurred
     * @param exception the thrown exception
     */
    @Override
    protected void clientException(ConnectionToClient client, Throwable exception) {
        System.out.println("Client exception: " + client.getId() + " - " + exception.getMessage());
        controller.editConnection(client);
    }
}