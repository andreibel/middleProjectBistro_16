package com.andreibel.client;
import message.APICallType;
import message.DTO.OrderRequest;
import message.DTO.OrderResponse;
import message.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class BistroClientController {
    private static BistroClient client;
    private static List<OrderResponse> orders;

    public void attachClient(BistroClient c) {
        client = c;
    }

    public void handleServerResponse(Message response) {
       switch (response.getApiCallType()){
           case GET_ORDERS:
               if (response.getObject() != null){
                   try{
                       orders = (List<OrderResponse>) response.getObject();
                   }
                   catch (ClassCastException e) { e.printStackTrace(); }
               }
               else throw new NoSuchElementException("Unable to retrieve orders");
           case UPDATE_ORDER:
               System.out.println("Update Order");
               //Validate Table View
               break;
       }
    }

    public static void requestOrders() {
        client.send(new Message(APICallType.GET_ORDERS, null));
    }

    public static void updateOrder(int orderNumber, int numberOfPeople, LocalDateTime date) {
        client.send(new Message(APICallType.UPDATE_ORDER, new OrderRequest(orderNumber, numberOfPeople, date)
        ));
    }

    public static List<OrderResponse> getListOfOrders() {
        if (orders == null) return null;
        return orders;
    }

    public void showError(String msg) {
        System.err.println(msg);
    }
}
