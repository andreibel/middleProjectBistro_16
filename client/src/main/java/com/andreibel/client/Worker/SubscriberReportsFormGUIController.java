package com.andreibel.client.Worker;

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

public class SubscriberReportsFormGUIController {
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

    @FXML
    public void initialize() {
        initiateSubscriberOrdersBarChart();
        initiateSubscriberWaitingBarChart();
    }

    public void onGoBackButtonClicked(ActionEvent event) {

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
