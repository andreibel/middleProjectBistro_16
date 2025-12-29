package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class SubscriberReportsFormGUIController implements IServerResponseListener {
    @FXML
    private Label lblTitle;
    @FXML
    private BarChart<String, Number> barChartSubscribersOrders;
    @FXML
    private CategoryAxis xDayAxisOrders;
    @FXML
    private NumberAxis yOrdersAxis;
    @FXML
    private BarChart<String, Number> barChartSubscribersWaiting;
    @FXML
    private CategoryAxis xDayAxisWaiting;
    @FXML
    private NumberAxis yWaitingAxis;
    @FXML
    private Button btnGoBack;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        initiateSubscriberOrdersBarChart();
        initiateSubscriberWaitingBarChart();
    }
    @Override
    public void onServerResponse(Message message) {
        
    }
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) {

    }
    private void initiateSubscriberOrdersBarChart(){
        List<String> days = new ArrayList<>();
        for (int day = 1; day <= 30; day++)
            days.add(Integer.toString(day));
        xDayAxisOrders.setCategories(FXCollections.observableArrayList(days));
        xDayAxisOrders.setLabel("Day of Month");
        yOrdersAxis.setLabel("Number of Orders");
        yOrdersAxis.setAutoRanging(true);
    }

    private void initiateSubscriberWaitingBarChart(){
        List<String> days = new ArrayList<>();
        for (int day = 1; day <= 30; day++)
            days.add(Integer.toString(day));
        xDayAxisWaiting.setCategories(FXCollections.observableArrayList(days));
        xDayAxisWaiting.setLabel("Day of Month");
        yWaitingAxis.setLabel("Number of Subscribers Waiting");
        yWaitingAxis.setAutoRanging(true);
        xDayAxisWaiting.setTickLabelRotation(45);
    }

}
