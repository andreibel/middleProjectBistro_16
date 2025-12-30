package com.andreibel.client.Client;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.DTO.WorkerNewRequest;
import com.andreibel.message.Message;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        for (IServerResponseListener listener : listeners)
            listener.onServerResponse(response);
    }



    //=======================Order Request==========================

    public void requestOrders() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_ORDERS, null));
    }
    //NEED TO CHANGE ORDER REQUEST AND ADD A FIELD FOR STATUS
    public void requestUpdateOrderStatus(OrderRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.UPDATE_ORDER, req));
    }
    public  void requestOrder(int numberOfPeople, LocalDateTime date, Integer subscriberId, String email, String phoneNumber) {
        if (!isClientAvailable()) return;
        OrderRequest req = new OrderRequest(
                null,
                numberOfPeople,
                date,
                subscriberId,
                email,
                phoneNumber
        );
        client.send(new Message(APICallType.CREATE_ORDER, req));
    }

    public void requestAvailableTimes(OrderRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_TIMES_IN_DATE, req));
    }

    public void requestOrderCancel(int confirmationCode){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.DELETE_ORDER, confirmationCode));
    }

    //=======================Subscriber Request==========================
    public void requestSubscriberLogin(Integer subscriberId) {
        if (!isClientAvailable()) return;
        //client.send(new Message(APICallType.SUBSCRIBER_LOGIN, subscriberId));
    }
    public void requestAllSubscriberOrders(int subscriberId) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_SUBSCRIBER_ORDERS, subscriberId));
    }

    public void requestSubscriberUpdateDetails(SubscriberRequest req){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.UPDATE_SUBSCRIBER, req));
    }

    //=======================Table Request==========================
    public void requestArrivalConfirmation(OrderRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.ORDER_ARRIVED, req));
    }

    public void requestGetTable(Integer confirmationCode, Integer subscriberId) {
        if (!isClientAvailable()) return;
        //client.send(new Message(APICallType.GET_TABLE, ...)
    }

    public void requestLostConfirmationCode(String email, String phoneNumber) {
        if (!isClientAvailable()) return;
        //client.send(new Message(APICallType.LOST_CNFRM_CODE, new OrderRequest(null, null, null, null, email, phoneNumber)));
    }

    //=======================Worker Request==========================

    public void requestWorkerLogin(String username, String password){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.LOGIN_WORKER, new WorkerAuth(username, password)));
    }

    public void requestRegisterNewSubscriber(SubscriberRequest req) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.CREATE_SUBSCRIBER, req));
    }

    public void requestSubscribersReport(){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_SUBSCRIBERS, null));
    }

    public void requestScheduleReportForCurrentMonth(){
        if (!isClientAvailable()) return;
        //client.send(new Message(APICallType.SCHEDULE_REPORT_REQUEST, LocalDate.now()));
    }

    private boolean isClientAvailable() {
        if (client == null || !client.isConnected()) {
            BistroUtilities.showMessage("Bistro Restaurant - Client Error","Client not connected");
            return false;
        }
        return true;
    }
}