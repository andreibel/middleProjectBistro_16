package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public class SubscriberOrderListFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblSubscriber;

    @FXML
    private TableView<OrderHistory> tblViewOrderHistory;

    @FXML
    private TableColumn<OrderHistory, Integer> colRowNumber;

    @FXML
    private TableColumn<OrderHistory, String> colOrderDateTime;

    @FXML
    private TableColumn<OrderHistory, Integer> colNumberOfPeople;

    private ObservableList<OrderHistory> orderHistoryList;
    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        orderHistoryList = FXCollections.observableArrayList();
        tblViewOrderHistory.setItems(orderHistoryList);
        tblViewOrderHistory.setEditable(false);

        initializeTableColumns();
        updateSubscriberLabel();

        requestOrdersWhenSceneIsShown();
        Integer subscriberId = CustomerStateManager.fillSubscriberIDDetails();
        controller.requestAllSubscriberOrders(subscriberId);
    }

    private void initializeTableColumns() {
        colRowNumber.setCellValueFactory(new PropertyValueFactory<>("orderRowNumber"));
        colOrderDateTime.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
        colNumberOfPeople.setCellValueFactory(new PropertyValueFactory<>("numberOfPeople"));
    }

    private void updateSubscriberLabel() {
        if (CustomerStateManager.getInstance().getSubscriber() != null) {
            lblSubscriber.setText(
                    "Hi, " +
                            CustomerStateManager.getInstance().getSubscriber().getName() +
                            ", here is your orders history:"
            );
        } else {
            lblSubscriber.setText("Orders History:");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_SUBSCRIBER_ORDERS_RESPONSE -> {
                List<OrderResponse> orders = (List<OrderResponse>) message.getData();
                if (orders != null) populateTable(orders);
            }
            case GET_SUBSCRIBER_ORDERS_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Unable to retrieve order history due to server error."
            );
        }
    }

    private void populateTable(List<OrderResponse> orders) {
        orderHistoryList.clear();

        int rowNumber = 1;
        for (OrderResponse order : orders) {
            String dateTime = order.getOrderDateTime() != null ? order.getOrderDateTime().toString() : "-";
            orderHistoryList.add(new OrderHistory(rowNumber++, dateTime, order.getNumberOfGuests()));
        }
    }

    private void requestOrdersWhenSceneIsShown() {
        tblViewOrderHistory.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            Integer subscriberId = CustomerStateManager.fillSubscriberIDDetails();
                            if (subscriberId != null) {
                                controller.requestAllSubscriberOrders(subscriberId);
                            }
                        });
                    }
                });
            }
        });
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        orderHistoryList.clear();

        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Subscriber/SubscriberZoneForm.fxml",
                "Bistro Restaurant - Subscriber Zone"
        );
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class OrderHistory {

        private final int orderRowNumber;
        private final String orderDateTime;
        private final int numberOfPeople;
    }
}
