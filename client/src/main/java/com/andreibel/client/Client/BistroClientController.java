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

public class BistroClientController {

    private static BistroClientController instance;
    private BistroClient client;
    private final List<IServerResponseListener> listeners = new ArrayList<>();
    public static BistroClientController getInstance() {
        if (instance == null) {
            instance = new BistroClientController();
        }
        return instance;
    }

    public void attachClient(BistroClient c) {
        this.client = c;
        c.setController(this);
    }

    public void addListener(IServerResponseListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IServerResponseListener listener) {
        listeners.remove(listener);
    }

    // ----- called from BistroClient (when AbstractClient gets a message) -----
    public void handleServerResponse(Message response) throws IOException {
        if (response == null || response.getType() == null) {
            BistroUtilities.showMessage("Error","Empty response from server");
            return;
        }
        //Only one GUI Controller will invoke onServerResponse method
        Platform.runLater(() -> {
            for (IServerResponseListener listener : listeners) {
                try {
                    listener.onServerResponse(response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }



    //=======================Order Request==========================
    public void requestCompleteOrder(UUID confirmationCode) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.COMPLETE_ORDER, confirmationCode));
    }
    public void requestOrderCreation(OrderRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.CREATE_ORDER, req));
    }

    public void requestAvailableTimes(TimeGetterRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_TIMES_IN_DATE, req));
    }

    public void requestOrderCancel(UUID confirmationCode){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.DELETE_ORDER, confirmationCode));
    }

    //=======================Subscriber Request==========================
    public void requestSubscriberLogin(Integer subscriberId) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.SUBSCRIBER_LOGIN, subscriberId));
    }
    public void requestAllSubscriberOrders(Integer subscriberId) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_SUBSCRIBER_ORDERS, subscriberId));
    }

    public void requestSubscriberUpdateDetails(SubscriberRequest req){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.UPDATE_SUBSCRIBER, req));
    }

    //=======================Table Request==========================
    public void requestArrivalConfirmation(UUID req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ORDER_ARRIVED, req));
    }

    public void requestDiningWithoutOrder(WaitingListRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ADD_TO_WAITING_LIST, req));
    }

    public void requestAllOrdersForCustomer(OrderRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ORDER_LOST_CONFORMATION_CODE, req));
    }

    public void requestSendConfirmationCode(String message) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ORDER_LOST_CONFORMATION_CODE, message));
    }

    //=======================Worker Request==========================

    public void requestWorkerLogin(WorkerAuth req){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.WORKER_LOGIN, req));
    }

    public void requestRegisterNewSubscriber(SubscriberRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.CREATE_SUBSCRIBER, req));
    }

    public void requestRegisterNewWorker(WorkerNewRequest req){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.WORKER_CREATE, req));
    }

    public void requestSchedulesReport(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.SCHEDULES_REPORT, null));
    }

    public void requestSubscribersReport(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.SUBSCRIBER_REPORT, null));
    }

    public void requestAllSubscribersInfo(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_SUBSCRIBERS, null));
    }

    public void requestCurrentWaitingList(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_WAITING_LIST, null));
    }

    public void requestActiveOrders(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_ACTIVE_ORDER, null));
    }

    public void requestCurrentDiningList(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_ARRIVED_AND_NOT_COMPLETE, null));
    }

    public void requestBistroTimes(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_REGULAR_OPEN_TIME, null));
    }

    public void requestEditBistroTimes(BistroTimeDTO req){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.CHANGE_BISTRO_TIME, req));
    }

    public void requestTables(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_TABLES, null));
    }

    public void requestApplyLayoutChanges(List<TableRequest> reqs){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.EDIT_BISTRO_LAYOUT, reqs));
    }

    public void requestNewEventCreation(SpecialDayRequest req){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ADD_SPECIAL_DAY, req));
    }

    private boolean isClientAvailable() {
        if (client == null || !client.isConnected()) {
            BistroUtilities.showMessage("Bistro Restaurant - Client Error","Client not connected");
            return false;
        }
        return true;
    }
}