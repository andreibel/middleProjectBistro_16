package com.andreibel.client;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.message.DTO.OrderResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class BistroClientGUIController {

    @FXML
    private Button btnUpdateOrder;

    @FXML
    private DatePicker orderDatePicker;

    // NOTE: matches fx:id="txtBoxNumberOfGuests" in your FXML
    @FXML
    private TextField txtBoxNumberOfGuests;

    @FXML
    private TableView<OrderResponse> tableViewOrders;

    @FXML
    private TableColumn<OrderResponse, Integer> orderNumberColumn;

    @FXML
    private TableColumn<OrderResponse, LocalDateTime> orderDateTimeColumn;

    @FXML
    private TableColumn<OrderResponse, Integer> numberOfGuestsColumn;

    @FXML
    private TableColumn<OrderResponse, UUID> conformationCodeColumn;

    @FXML
    private TableColumn<OrderResponse, Integer> subscriberIdColumn;

    @FXML
    private TableColumn<OrderResponse, LocalDateTime> placedOrderDateTimeColumn;

    private BistroClientController controller;
    private ObservableList<OrderResponse> orders;

    @FXML
    public void initialize() {
        setupTableView();
        setupRowSelection();
        // since FXML has no onAction, wire it here:
        btnUpdateOrder.setOnAction(this::onUpdateOrderButtonClicked);
    }

    public void setController(BistroClientController controller) {
        this.controller = controller;
    }

    private void setupTableView() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
        numberOfGuestsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        conformationCodeColumn.setCellValueFactory(new PropertyValueFactory<>("conformationCode"));
        subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        placedOrderDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("placedOrderDateTime"));
    }

    private void setupRowSelection() {
        tableViewOrders.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue != null) {
                        onSelectedOrderFromTableView(newValue);
                    }
                });
    }

    private void onSelectedOrderFromTableView(OrderResponse selectedItem) {
        txtBoxNumberOfGuests.setText(String.valueOf(selectedItem.getNumberOfGuests()));

        LocalDateTime dt = selectedItem.getOrderDateTime();
        if (dt != null) {
            orderDatePicker.setValue(dt.toLocalDate());
        } else {
            orderDatePicker.setValue(null);
        }
    }

    @FXML
    private void onUpdateOrderButtonClicked(ActionEvent event) {
        if (controller == null) {
            showError("No controller attached");
            return;
        }

        OrderResponse selected = tableViewOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an order");
            return;
        }

        int guests;
        try {
            guests = Integer.parseInt(txtBoxNumberOfGuests.getText());
        } catch (NumberFormatException e) {
            showError("Number of guests must be a number");
            return;
        }

        LocalDate date = orderDatePicker.getValue();
        if (date == null) {
            showError("Please choose a date");
            return;
        }

        LocalTime time = selected.getOrderDateTime() != null
                ? selected.getOrderDateTime().toLocalTime()
                : LocalTime.now();

        LocalDateTime orderDateTime = LocalDateTime.of(date, time);


        // --- Added: determine Subscriber vs Guest details ---
        Integer subscriberId = null;
        String email = null;
        String phoneNumber = null;

//        if (subscriberRadio.isSelected()) {
//            // Subscriber: require subscriber ID
//            String idText = txtSubscriberId.getText().trim();
//            if (idText.isEmpty()) {
//                showError("Please enter Subscriber ID");
//                return;
//            }
//            try {
//                subscriberId = Integer.parseInt(idText);
//            } catch (NumberFormatException e) {
//                showError("Subscriber ID must be a number");
//                return;
//            }
//        } else {
//            // Guest: require email + phone number
//            email = txtEmail.getText().trim();
//            phoneNumber = txtPhoneNumber.getText().trim();
//
//            if (email.isEmpty() || phoneNumber.isEmpty()) {
//                showError("Please enter Email and Phone Number");
//                return;
//            }
//        }
/// //////////////

//        controller.requestOrder(
//                selected.getOrderNumber(),
//                guests,
//                orderDateTime,
//                subscriberId,
//                email,
//                phoneNumber
//        );

    }

    // called from BistroClientController when server returns list
    public void setOrdersToGUI(List<OrderResponse> ordersFromServer) {
        if (ordersFromServer == null) {
            this.orders = FXCollections.observableArrayList();
        } else {
            this.orders = FXCollections.observableArrayList(ordersFromServer);
        }

        tableViewOrders.setItems(this.orders);

        if (!this.orders.isEmpty()) {
            tableViewOrders.getSelectionModel().selectFirst();
        }
    }

//    // called from BistroClientController when only one order is updated
//    public void refreshSingleOrder(OrderResponse updated) {
//        if (orders == null || updated == null) {
//            return;
//        }
//
//        for (int i = 0; i < orders.size(); i++) {
//            if (orders.get(i).getOrderNumber() == updated.getOrderNumber()) {
//                orders.set(i, updated);                         // OK now, mutable list
//                tableViewOrders.getSelectionModel().select(i);  // keep row selected
//                break;
//            }
//        }
//    }

    public void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}