package com.andreibel.client;

import message.DTO.OrderResponse;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;

public class BistroClientGUIController {

    @FXML
    private Button btnUpdateOrder;
    @FXML
    private DatePicker orderDatePicker;
    @FXML
    private TextField txtNumberOfGuests;
    @FXML
    private TableView<OrderResponse> tableViewOrders;

    @FXML
    private TableColumn<OrderResponse, Integer> orderNumberColumn;
    @FXML
    private TableColumn<OrderResponse, Integer> numberOfGuestsColumn;
    @FXML
    private TableColumn<OrderResponse, Integer> conformationCodeColumn;
    @FXML
    private TableColumn<OrderResponse, Integer> subscriberIdColumn;
    @FXML
    private TableColumn<OrderResponse, LocalDateTime> orderDateTimeColumn;
    @FXML
    private TableColumn<OrderResponse, LocalDateTime> placedOrderDateTimeColumn;

    private List<OrderResponse> orders;

    @FXML
    public void initialize() {
        //setupTableView();
        //setupRowSelection();
    }

    private void setupTableView() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        numberOfGuestsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        conformationCodeColumn.setCellValueFactory(new PropertyValueFactory<>("conformationCode"));
        subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        orderDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
        placedOrderDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("placedOrderDateTime"));
    }
    private void setupRowSelection() {
        tableViewOrders.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue != null) onSelectedOrderFromTableView(newValue);
                });
    }

    private void onSelectedOrderFromTableView(OrderResponse selectedItem) {
        txtNumberOfGuests.setText(String.valueOf(selectedItem.getNumberOfGuests()));

        if (selectedItem.getOrderDateTime() != null)
            orderDatePicker.setValue(selectedItem.getOrderDateTime().toLocalDate());
        else
            orderDatePicker.setValue(null);
    }

    @FXML
    private void onUpdateOrderButtonClicked(ActionEvent event) {
        // handle update order
        //after updating order, validating the form and the table view
    }

    public void setOrdersToGUI(List<OrderResponse> orders){
        this.orders = orders;

        if (orders != null) {
            tableViewOrders.setItems(FXCollections.observableArrayList(orders));

            if (!orders.isEmpty())
                tableViewOrders.getSelectionModel().selectFirst();
        }
    }


}

