package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActiveOrdersFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;

    @FXML
    private TableView<OrderResponse> tblActiveOrders;

    @FXML
    private TableColumn<OrderResponse, Integer> colNumberOfGuests;
    @FXML
    private TableColumn<OrderResponse, Integer> colSubscriberId;
    @FXML
    private TableColumn<OrderResponse, String> colEmail;
    @FXML
    private TableColumn<OrderResponse, String> colPhoneNumber;
    @FXML
    private TableColumn<OrderResponse, String> colOrderDateTime;

    private ObservableList<OrderResponse> activeList;
    private BistroClientController controller;

    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        lblTitle.setText("Active Orders for " + getCurrentDate());

        activeList = FXCollections.observableArrayList();
        tblActiveOrders.setItems(activeList);
        tblActiveOrders.setEditable(false);

        initializeTableColumns();
        controller.requestActiveOrders();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_ALL_ACTIVE_RESPONSE -> populateTable((List<OrderResponse>) message.getData());
            case GET_ALL_ACTIVE_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, we're unable to get the active orders for " + getCurrentDate()
            );
        }
    }

    private void populateTable(List<OrderResponse> data) {
        if (data == null) return;
        activeList.setAll(data);
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        activeList.clear();

        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    private void initializeTableColumns() {
        colNumberOfGuests.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        colSubscriberId.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colOrderDateTime.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
    }

    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
