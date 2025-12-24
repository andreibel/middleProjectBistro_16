package com.andreibel.client.Client;

import com.andreibel.client.BistroClientGUIController;
import com.andreibel.client.Main.MainForm;
import com.andreibel.client.Main.MainFormGUIController;
import javafx.application.Platform;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderRequest;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//================ Need to Change GUI Controller to MainFormGUIController ==================
public class BistroClientController {

    private BistroClient client;
    private final MainFormGUIController guiController;

    public BistroClientController(MainFormGUIController guiController) {
        this.guiController = guiController;
    }

    public void attachClient(BistroClient c) {
        this.client = c;
        c.setController(this);
    }

    // ----- called from BistroClient (when AbstractClient gets a message) -----
    public void handleServerResponse(Message response) {
        if (response == null || response.getType() == null) {
            showError("Empty response from server");
            return;
        }

        switch (response.getType()) {
            case GET_ALL_ORDERS_RESPONSE -> {
                @SuppressWarnings("unchecked")
                List<OrderResponse> orders = (List<OrderResponse>) response.getData();
                //Platform.runLater(() -> guiController.setOrdersToGUI(orders));
            }

            case UPDATE_ORDER_RESPONSE -> {
                OrderResponse updated = (OrderResponse) response.getData();
                //Platform.runLater(() -> guiController.refreshSingleOrder(updated));
            }

            case ERROR -> {
                String msg = response.getData() != null ?
                        response.getData().toString() : "Unknown error";
                showError(msg);
            }

            default -> showError("Unhandled response type: " + response.getType());
        }
    }

    // ----- called from GUI controller -----

    public void requestOrders() {
        if (client == null) {
            showError("Client not connected");
            return;
        }
        client.send(new Message(APICallType.GET_ALL_ORDERS, null));
    }

    public void updateOrder(int orderNumber,
                            int numberOfPeople,
                            LocalDateTime date,
                            Integer subscriberId,
                            String email,
                            String phoneNumber) {

        if (client == null) {
            showError("Client not connected");
            return;
        }

        UUID confirmationCode = UUID.randomUUID();

        OrderRequest req = new OrderRequest(
                confirmationCode,
                numberOfPeople,
                date,
                subscriberId,
                email,
                phoneNumber
        );

        client.send(new Message(APIcallType.UPDATE_ORDER, req));
    }


    // ----- error handling -----

    public void showError(String msg) {
        //Platform.runLater(() -> guiController.showError(msg));
    }
}