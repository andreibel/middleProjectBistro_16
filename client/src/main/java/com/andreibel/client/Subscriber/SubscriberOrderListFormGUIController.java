package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//TODO Finish onServerResponse

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
        setTabView();
        lblSubscriber.setText("Hi, " + CustomerStateManager.getInstance().getSubscriber().getName() + " here is your orders history:");
        onSceneShown();
        tblViewOrderHistory.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            onSceneShown();
                        });
                    }
                });
            }
        });

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onServerResponse(Message message) {
        if (message.getType() == APICallType.GET_SUBSCRIBER_ORDERS_RESPONSE){
            Platform.runLater(() -> {
                orderHistoryList.clear();
                List<OrderResponse> subscriberOrders = (List<OrderResponse>) message.getData();
                for (OrderResponse orderResponse : subscriberOrders) {
                    orderHistoryList.add(new OrderHistory(
                            orderResponse.getOrderDateTime().toString(),
                            orderResponse.getNumberOfGuests()
                    ));
                }
            });
        }
        else if (message.getType() == APICallType.GET_SUBSCRIBER_ORDERS_ERROR)
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to retrieve history orders");
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        tblViewOrderHistory.getItems().clear();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Subscriber/SubscriberZoneForm.fxml", "Bistro Restaurant - Subscriber Zone");
    }

    private void onSceneShown() {
        controller.requestAllSubscriberOrders(CustomerStateManager.getInstance().getSubscriber().getSubscriberId());
    }

    @FXML
    private void setTabView(){
        colRowNumber.setCellValueFactory(new PropertyValueFactory<>("orderRowNumber"));
        colOrderDateTime.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
        colNumberOfPeople.setCellValueFactory(new PropertyValueFactory<>("numberOfPeople"));
    }

    public class OrderHistory {
        private static int RowNumber = 0;
        @Getter
        private int orderRowNumber;
        @Getter
        private String orderDateTime;
        @Getter
        private int numberOfPeople;

        public OrderHistory(String orderDateTime, int numberOfPeople) {
            ++RowNumber;
            this.orderDateTime = orderDateTime;
            this.numberOfPeople = numberOfPeople;
            this.orderRowNumber = RowNumber;
        }
    }
}
