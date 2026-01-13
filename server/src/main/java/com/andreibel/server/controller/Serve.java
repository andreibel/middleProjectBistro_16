package com.andreibel.server.controller;

import com.andreibel.message.Message;
import com.lloseng.ocsf.server.AbstractServer;
import com.lloseng.ocsf.server.ConnectionToClient;

import java.io.IOException;

import static com.andreibel.server.utils.TUI.serverInputLog;
import static com.andreibel.server.utils.TUI.serverOutputLog;

/**
 * Main OCSF server implementation for the Bistro system.
 *
 * <p>This class extends {@link AbstractServer} and provides the networking entry point for the server:
 * it receives objects from clients, expects them to be {@link Message} instances, routes them by
 * {@link Message#getType()} to the appropriate controller, and sends a response back to the client.</p>
 *
 * <h2>Routing</h2>
 * <p>Requests are dispatched using a {@code switch} on the message type, delegating to:</p>
 * <ul>
 *   <li>{@link OrderController} - orders and availability-related requests</li>
 *   <li>{@link SubscriberController} - subscriber login, CRUD and order history</li>
 *   <li>{@link WorkerController} - worker login, restaurant configuration and layout</li>
 *   <li>{@link WaitingController} - waiting list flows and reports</li>
 * </ul>
 *
 * <h2>GUI integration</h2>
 * <p>If a {@link BistroServerGUIController} is attached via {@link #setGUIController(BistroServerGUIController)},
 * connection lifecycle events are forwarded to the GUI so it can display current client states.</p>
 *
 * <h2>Threading note</h2>
 * <p>OCSF callbacks may run on non-JavaFX threads. If the GUI controller updates JavaFX UI controls,
 * it should wrap the updates using {@code javafx.application.Platform.runLater(...)}.</p>
 *
 * @author Andrei Beloziyorove
 */
public class Serve extends AbstractServer {

    /** Controller responsible for order-related operations. */
    private final OrderController orderController;

    /** Controller responsible for subscriber-related operations. */
    private final SubscriberController subscriberController;

    /** Controller responsible for worker-related operations. */
    private final WorkerController workerController;

    /** Controller responsible for waiting-list flows and reports. */
    private final WaitingController waitingController;
    private final ReportController reportController;
    private final PaymentController paymentController;

    /**
     * Optional GUI controller reference used to reflect connection state in the server UI.
     * Can be {@code null} if the server is run without GUI.
     */
    private BistroServerGUIController controller;

    /**
     * Constructs a server that listens on the given port and initializes controllers.
     *
     * @param port server port to listen on
     */
    public Serve(int port) {
        super(port);
        this.orderController = OrderController.getInstance();
        this.subscriberController = SubscriberController.getInstance();
        this.workerController = WorkerController.getInstance();
        this.waitingController = WaitingController.getInstance();
        this.reportController = ReportController.getInstance();
        this.paymentController = PaymentController.getInstance();
    }

    /**
     * Attaches a GUI controller used to display and update connection state.
     *
     * <p>If not set, connection lifecycle hooks will still run but will not update any UI.</p>
     *
     * @param controller GUI controller instance (may be {@code null})
     */
    public void setGUIController(BistroServerGUIController controller) {
        this.controller = controller;
    }

    /**
     * Handles an incoming object received from a specific client connection.
     *
     * <p>The OCSF framework calls this method whenever a client sends an object.
     * This implementation:</p>
     * <ol>
     *   <li>Validates the object is a {@link Message}.</li>
     *   <li>Logs the request via {@link com.andreibel.server.utils.TUI#serverInputLog(Message)}.</li>
     *   <li>Routes by {@code message.getType()} to the corresponding controller handler.</li>
     *   <li>Logs the response via {@link com.andreibel.server.utils.TUI#serverOutputLog(Message)}.</li>
     *   <li>Sends the response back to the client with {@link ConnectionToClient#sendToClient(Object)}.</li>
     * </ol>
     *
     * <p><b>Important:</b> some handlers may return {@code null} (for example, if the request is treated
     * as a server-side-only action). In that case, sending {@code null} may not be desirable. Consider
     * guarding with {@code if (response != null) sendToClient(response)}.</p>
     *
     * @param msg    object received from the client (expected to be a {@link Message})
     * @param client client connection that sent the object
     * @throws RuntimeException if the incoming object is not a {@link Message} or an {@link IOException}
     *                          occurs while sending the response
     */
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (!(msg instanceof Message message)) {
                throw new IllegalArgumentException("Invalid message type: expected Message");
            }

            serverInputLog(message);

            Message response = switch (message.getType()) {
                case CREATE_ORDER -> orderController.createOrder(message);
                case DELETE_ORDER -> orderController.deleteOrder(message);
                case GET_ONE_ORDER -> orderController.getOrder(message);
                case ORDER_ARRIVED -> orderController.updateArrives(message);
                case ORDER_LOST_CONFORMATION_CODE -> orderController.lostCode(message);
                case COMPLETE_ORDER -> paymentController.payByConformationCode(message);
                case GET_ALL_TIMES_IN_DATE -> orderController.getAllAvailableTime(message);

                case GET_ALL_SUBSCRIBERS -> subscriberController.getAllSub();
                case SUBSCRIBER_LOGIN -> subscriberController.getSub(message);
                case GET_SUBSCRIBER_ORDERS -> subscriberController.getSubOrders(message);
                case UPDATE_SUBSCRIBER -> subscriberController.updateSub(message);
                case CREATE_SUBSCRIBER -> subscriberController.createSub(message);

                case WORKER_LOGIN -> workerController.login(message);
                case WORKER_CREATE -> workerController.createWorker(message);
                case ADD_SPECIAL_DAY -> workerController.addSpecialDay(message);
                case CHANGE_BISTRO_TIME -> workerController.editRegulaDay(message);
                case GET_ALL_TABLES -> workerController.getAllTables();
                case EDIT_BISTRO_LAYOUT -> workerController.updateTables(message);
                case GET_REGULAR_OPEN_TIME -> workerController.getRegularDate();

                case SCHEDULES_REPORT -> reportController.scheduleReport(message);
                case SUBSCRIBER_REPORT -> reportController.subscriberReport(message);

                case GET_WAITING_LIST -> waitingController.getWaitingList();
                case ADD_TO_WAITING_LIST -> waitingController.addWaitingList(message);

                case GET_ALL_ACTIVE_ORDER -> orderController.getAllActiveOrder();
                case GET_ALL_ARRIVED_AND_NOT_COMPLETE -> orderController.getNowEating();

                default -> null;
            };

            serverOutputLog(response);

            // If response can be null, prefer guarding to avoid sending null over OCSF.
            client.sendToClient(response);

        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * OCSF hook invoked when a client successfully connects.
     *
     * <p>If a GUI controller is attached, the connection is added to the GUI table.</p>
     *
     * @param client the connected client
     */
    @Override
    protected void clientConnected(ConnectionToClient client) {
        if (controller != null) {
            controller.addNewConnection(client);
        }
    }

    /**
     * OCSF hook invoked when a client disconnects.
     *
     * <p>If a GUI controller is attached, the corresponding connection status is marked closed.</p>
     *
     * @param client the disconnected client
     */
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        if (controller != null) {
            controller.editConnection(client);
        }
    }

    /**
     * OCSF hook invoked when an exception occurs for a client connection.
     *
     * <p>This typically indicates an abnormal disconnection. If a GUI controller is attached,
     * the corresponding connection status is updated.</p>
     *
     * @param client     the client connection where the exception occurred
     * @param exception  the thrown exception
     */
    @Override
    protected void clientException(ConnectionToClient client, Throwable exception) {
        if (controller != null) {
            controller.editConnection(client);
        }
    }
}