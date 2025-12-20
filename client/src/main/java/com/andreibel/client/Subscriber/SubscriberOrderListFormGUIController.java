package com.andreibel.client.Subscriber;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;

import java.util.Date;

public class SubscriberOrderListFormGUIController {
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

    public void initialize() {
        setTabView();
    }
    private void setTabView(){
        colRowNumber.setCellValueFactory(new PropertyValueFactory<>("orderRowNumber"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colNumberOfPeople.setCellValueFactory(new PropertyValueFactory<>("numberOfPeople"));
    }

    public void onGoBackButtonClicked(ActionEvent actionEvent) {}

    public class OrderHistory{
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
