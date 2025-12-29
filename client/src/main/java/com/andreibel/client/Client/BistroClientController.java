package com.andreibel.client.Client;

import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.DTO.WorkerNewRequest;
import com.andreibel.message.Message;

import java.io.IOException;
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
        for (IServerResponseListener listener : listeners)
            listener.onServerResponse(response);
//        switch (response.getType()) {
//            case GET_ALL_ORDERS_RESPONSE -> {
//                @SuppressWarnings("unchecked")
//                List<OrderResponse> orders = (List<OrderResponse>) response.getData();
//                //Platform.runLater(() -> guiController.setOrdersToGUI(orders));
//            }
//
//            case UPDATE_ORDER_RESPONSE -> {
//                OrderResponse updated = (OrderResponse) response.getData();
//                //Platform.runLater(() -> guiController.refreshSingleOrder(updated));
//            }
//
//            case ERROR -> {
//                String msg = response.getData() != null ?
//                        response.getData().toString() : "Unknown error";
//                BistroUtilities.showMessage("Error",msg);
//            }
//
//            default -> BistroUtilities.showMessage("Error","Unhandled response type: " + response.getType());
//
        //Only one GUI Controller will complete onServerResponse the others will terminate at the start of the method
    }



    //=======================order Request==========================

    public void requestOrders() {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_ALL_ORDERS, null));
    }

//    public void updateOrderStatus(int){
//        if (!isClientAvailable()) return;
//    }
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

    public void requestOrderCancel(int confirmationCode){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.DELETE_ORDER, confirmationCode));
    }

    //=======================Subscriber Request==========================
    public void requestSubscriberLogin(int subscriberId) {
        if (!isClientAvailable()) return;
        //Need to add in the APICallType LOGIN_SUBSCRIBER
        //client.send(new Message(APICallType.LOGIN_SUBSCRIBER, subscriberId));
    }
    public void requestAllSubscriberOrders(int subscriberId) {
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.GET_SUBSCRIBER_ORDERS, subscriberId));
    }

    //=======================Worker Request==========================

    public void requestWorkerLogin(String username, String password){
        if (!isClientAvailable()) return;
        client.send(new Message(APICallType.LOGIN_WORKER, new WorkerAuth(username, password)));
    }


    private boolean isClientAvailable() {
        if (client == null || !client.isConnected()) {
            BistroUtilities.showMessage("Error","Client not connected");
            return false;
        }
        return true;
    }
}