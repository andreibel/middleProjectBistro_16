package com.andreibel.client.Client;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.*;
import com.andreibel.message.Message;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Central client-side controller responsible for communication
 * between the GUI layer and the server.
 *
 * <p>This class:
 * <ul>
 *     <li>Acts as a facade for sending requests to the server</li>
 *     <li>Receives server responses and dispatches them to listeners</li>
 *     <li>Ensures UI updates are executed on the JavaFX Application Thread</li>
 * </ul>
 * </p>
 *
 * <p>Implemented as a singleton to guarantee a single communication
 * entry point per client instance.</p>
 */
public class BistroClientController {

    /** Singleton instance */
    private static BistroClientController instance;

    /** Network client responsible for server communication */
    private BistroClient client;

    /** Registered listeners awaiting server responses */
    private final List<IServerResponseListener> listeners = new ArrayList<>();

    /**
     * Returns the singleton instance of the client controller.
     *
     * @return the single {@link BistroClientController} instance
     */
    public static BistroClientController getInstance() {
        if (instance == null) {
            instance = new BistroClientController();
        }
        return instance;
    }

    /**
     * Attaches a {@link BistroClient} to this controller and
     * registers this controller as its response handler.
     *
     * @param c the client to attach
     */
    public void attachClient(BistroClient c) {
        this.client = c;
        c.setController(this);
    }

    /**
     * Registers a listener to receive server responses.
     *
     * @param listener the listener to add
     */
    public void addListener(IServerResponseListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a previously registered server response listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(IServerResponseListener listener) {
        listeners.remove(listener);
    }

    // ======================= Server Response Handling =======================

    /**
     * Handles incoming responses from the server.
     *
     * <p>The response is forwarded to all registered listeners.
     * Execution is wrapped with {@link Platform#runLater(Runnable)}
     * to ensure JavaFX thread safety.</p>
     *
     * @param response the message received from the server
     * @throws IOException if a listener fails while processing the response
     */
    public void handleServerResponse(Message response) throws IOException, Exception {
        if (response == null || response.getType() == null) {
            BistroUtilities.showMessage("Error", "Empty response from server");
            return;
        }
        //Only one GUI controller will receive the message from the server
        Platform.runLater(() -> {
            for (IServerResponseListener listener : listeners) {
                try {
                    listener.onServerResponse(response);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to handle server response", e);
                }
            }
        });
    }

    // ======================= Order Requests =======================

    /**
     * Requests order completion using a confirmation code.
     *
     * @param confirmationCode unique order confirmation identifier
     */
    public void requestCompleteOrder(UUID confirmationCode) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.COMPLETE_ORDER, confirmationCode));
    }

    /**
     * Requests creation of a new order.
     *
     * @param req order creation data
     */
    public void requestOrderCreation(OrderRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.CREATE_ORDER, req));
    }

    /**
     * Requests available dining times for a specific date.
     *
     * @param req time availability request
     */
    public void requestAvailableTimes(TimeGetterRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_TIMES_IN_DATE, req));
    }

    /**
     * Requests cancellation of an order.
     *
     * @param confirmationCode order confirmation code
     */
    public void requestOrderCancel(UUID confirmationCode) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.DELETE_ORDER, confirmationCode));
    }

    // ======================= Subscriber Requests =======================

    /**
     * Requests subscriber login.
     *
     * @param subscriberId subscriber identifier
     */
    public void requestSubscriberLogin(Integer subscriberId) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.SUBSCRIBER_LOGIN, subscriberId));
    }

    /**
     * Requests all orders associated with a subscriber.
     *
     * @param subscriberId subscriber identifier
     */
    public void requestAllSubscriberOrders(Integer subscriberId) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_SUBSCRIBER_ORDERS, subscriberId));
    }

    /**
     * Requests an update to subscriber details.
     *
     * @param req updated subscriber information
     */
    public void requestSubscriberUpdateDetails(SubscriberRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.UPDATE_SUBSCRIBER, req));
    }

    // ======================= Table & Waiting List Requests =======================

    /**
     * Confirms customer arrival for a reservation.
     *
     * @param req order confirmation code
     */
    public void requestArrivalConfirmation(UUID req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ORDER_ARRIVED, req));
    }

    /**
     * Requests dining without a prior order (waiting list).
     *
     * @param req waiting list request data
     */
    public void requestDiningWithoutOrder(WaitingListRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ADD_TO_WAITING_LIST, req));
    }

    /**
     * Requests all orders for a customer when confirmation code was lost.
     *
     * @param req order lookup request
     */
    public void requestAllOrdersForCustomer(OrderRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ORDER_LOST_CONFORMATION_CODE, req));
    }

    /**
     * Sends confirmation code to a customer.
     *
     * @param message message containing contact information
     */
    public void requestSendConfirmationCode(String message) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ORDER_LOST_CONFORMATION_CODE, message));
    }

    // ======================= Worker & Admin Requests =======================

    /**
     * Requests worker login.
     *
     * @param req worker authentication data
     */
    public void requestWorkerLogin(WorkerAuth req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.WORKER_LOGIN, req));
    }

    /**
     * Registers a new subscriber.
     *
     * @param req subscriber registration data
     */
    public void requestRegisterNewSubscriber(SubscriberRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.CREATE_SUBSCRIBER, req));
    }

    /**
     * Registers a new worker.
     *
     * @param req worker creation data
     */
    public void requestRegisterNewWorker(WorkerNewRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.WORKER_CREATE, req));
    }

    /**
     * Requests the schedules report.
     */
    public void requestSchedulesReport() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.SCHEDULES_REPORT, null));
    }

    /**
     * Requests the subscribers report.
     */
    public void requestSubscribersReport() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.SUBSCRIBER_REPORT, null));
    }

    /**
     * Requests all subscribers information.
     */
    public void requestAllSubscribersInfo() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_SUBSCRIBERS, null));
    }

    /**
     * Requests the current waiting list.
     */
    public void requestCurrentWaitingList() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_WAITING_LIST, null));
    }

    /**
     * Requests all currently active orders.
     */
    public void requestActiveOrders() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_ACTIVE_ORDER, null));
    }

    /**
     * Requests all arrived but not completed orders.
     */
    public void requestCurrentDiningList() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_ARRIVED_AND_NOT_COMPLETE, null));
    }

    /**
     * Requests regular bistro opening times.
     */
    public void requestBistroTimes() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_REGULAR_OPEN_TIME, null));
    }

    /**
     * Requests an update to bistro opening times.
     *
     * @param req updated bistro time data
     */
    public void requestEditBistroTimes(BistroTimeDTO req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.CHANGE_BISTRO_TIME, req));
    }

    /**
     * Requests all tables information.
     */
    public void requestTables() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_TABLES, null));
    }

    /**
     * Applies changes to the bistro layout.
     *
     * @param reqs list of table layout updates
     */
    public void requestApplyLayoutChanges(List<TableRequest> reqs) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.EDIT_BISTRO_LAYOUT, reqs));
    }

    /**
     * Requests creation of a new special event day.
     *
     * @param req special day data
     */
    public void requestNewEventCreation(SpecialDayRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ADD_SPECIAL_DAY, req));
    }

    // ======================= Utility =======================

    /**
     * Checks whether the client is connected and available.
     *
     * @return {@code true} if the client is connected, {@code false} otherwise
     */
    private boolean isClientAvailable() {
        if (client == null || !client.isConnected()) {
            BistroUtilities.showMessage(
                    "Bistro Restaurant - Client Error",
                    "Client not connected"
            );
            return false;
        }
        return true;
    }
}