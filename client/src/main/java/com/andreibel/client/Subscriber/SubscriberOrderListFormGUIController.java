package com.andreibel.client.Subscriber;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class SubscriberOrderListFormGUIController implements IServerResponseListener {
    @FXML
    private Label lblSubscriber;
    @FXML
    private TableView<OrderHistory> tblViewOrderHistory;
    @FXML
    private TableColumn<OrderHistory, Integer> colRowNumber;
    @FXML
    private TableColumn<OrderHistory, Date> colOrderDate;
    @FXML
    private TableColumn<OrderHistory, Integer> colNumberOfPeople;
    @FXML
    private Button btnGoBack;
    @Getter
    @Setter
    private ObservableList<OrderHistory> orderHistoryList;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        setTabView();
        setOrderHistoryTable();
    }

    @Override
    public void onServerResponse(Message message) {

    }
    @FXML
    private void setTabView(){
        colRowNumber.setCellValueFactory(new PropertyValueFactory<>("orderRowNumber"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colNumberOfPeople.setCellValueFactory(new PropertyValueFactory<>("numberOfPeople"));
    }

    private void setOrderHistoryTable(){
        //needs to setup the client correctly
        if (orderHistoryList == null){
            orderHistoryList = FXCollections.observableArrayList();
        }
        tblViewOrderHistory.setItems(orderHistoryList);
    }
    @FXML
    private void onGoBackButtonClicked(ActionEvent actionEvent) {}

    public class OrderHistory {
        private static int RowNumber = 0;
        @Getter
        private int orderRowNumber;
        @Getter
        private Date orderDate;
        @Getter
        private int numberOfPeople;

        public OrderHistory(Date orderDate, int numberOfPeople) {
            ++RowNumber;
            this.orderDate = orderDate;
            this.numberOfPeople = numberOfPeople;
            this.orderRowNumber = RowNumber;
        }
    }
}
