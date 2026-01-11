package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.SubscriberResponse;
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
import javafx.scene.layout.AnchorPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

/**
 * Controller for displaying the subscriber's order history.
 *
 * <p>This form retrieves and shows all past orders of a logged-in subscriber
 * in a table view. The subscriber name is displayed at the top of the form.
 * Subscribers can return to the main subscriber zone using the back button.</p>
 */
public class SubscriberOrderListFormGUIController implements IServerResponseListener {

    /** Label to display subscriber greeting or order history title */
    @FXML private Label lblSubscriber;

    /** Table view to display order history */
    @FXML private TableView<OrderHistory> tblViewOrderHistory;

    /** Table columns */
    @FXML private TableColumn<OrderHistory, Integer> colRowNumber;
    @FXML private TableColumn<OrderHistory, String> colOrderDateTime;
    @FXML private TableColumn<OrderHistory, Integer> colNumberOfPeople;

    /** Root pane for scene listeners */
    @FXML private AnchorPane rootPane;

    private ObservableList<OrderHistory> orderHistoryList;
    private BistroClientController controller;

    /**
     * Initializes the form, sets up the table columns, and requests subscriber orders
     * when the scene is shown.
     */
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
    }

    /** Configures the table columns to bind to the OrderHistory fields */
    private void initializeTableColumns() {
        colRowNumber.setCellValueFactory(new PropertyValueFactory<>("orderRowNumber"));
        colOrderDateTime.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
        colNumberOfPeople.setCellValueFactory(new PropertyValueFactory<>("numberOfPeople"));
    }

    /** Updates the subscriber greeting label based on the logged-in subscriber */
    private void updateSubscriberLabel() {
        if (CustomerStateManager.getInstance().getSubscriber() != null) {
            lblSubscriber.setText(
                    "Hi, " + CustomerStateManager.getInstance().getSubscriber().getName()
            );
        } else {
            lblSubscriber.setText("Orders History:");
        }
    }

    /**
     * Handles server responses related to retrieving subscriber orders.
     *
     * @param message the server message
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_SUBSCRIBER_ORDERS_RESPONSE -> {
                List<OrderResponse> orders = ((SubscriberResponse) message.getData()).getOrders();
                if (orders != null) populateTable(orders);
                else BistroUtilities.showMessage("Bistro Restaurant", "No orders were found.");
            }
            case GET_SUBSCRIBER_ORDERS_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Unable to retrieve order history due to server error."
            );
        }
    }

    /**
     * Populates the table with order data.
     *
     * @param orders list of subscriber orders
     */
    private void populateTable(List<OrderResponse> orders) {
        orderHistoryList.clear();

        int rowNumber = 1;
        for (OrderResponse order : orders) {
            String dateTime = order.getOrderDateTime() != null ? order.getOrderDateTime().toString() : "-";
            orderHistoryList.add(new OrderHistory(rowNumber++, dateTime, order.getNumberOfGuests()));
        }
    }

    /** Requests subscriber orders when the scene is first displayed */
    private void requestOrdersWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Integer subscriberId = CustomerStateManager.fillSubscriberIDDetails();
                        if (subscriberId != null) {
                            controller.requestAllSubscriberOrders(subscriberId);
                        }
                    }
                });
            }
        });
    }

    /**
     * Handles the back button click, clearing the table and returning to
     * the subscriber zone.
     *
     * @param event action event triggered by the back button
     * @throws IOException if the screen cannot be switched
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        orderHistoryList.clear();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Subscriber/SubscriberZoneForm.fxml",
                "Bistro Restaurant - Subscriber Area"
        );
    }

    /**
     * Represents a single row in the order history table.
     */
    @AllArgsConstructor
    @Getter
    @Setter
    public static class OrderHistory {
        private final int orderRowNumber;
        private final String orderDateTime;
        private final int numberOfPeople;
    }
}