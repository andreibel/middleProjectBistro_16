package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.SubscriberReportResponse;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class SubscriberReportsFormGUIController implements IServerResponseListener {

    @FXML
    private AnchorPane rootPane;
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

    private Map<LocalDate, Integer> subscriberOrdersCount;
    private Map<LocalDate, Integer> subscriberWaitingListCount;
    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        lblTitle.setText("Subscribers Report for Month: " + getCurrentMonth());
        requestSubscribersReportWhenSceneIsShown();
        controller.requestSubscribersReport();
    }

    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()){
            case SUBSCRIBER_REPORT_RESPONSE -> {
                SubscriberReportResponse response = (SubscriberReportResponse) message.getData();
                if (response == null) return;

                subscriberOrdersCount = response.getSubscriberOrdersCount();
                subscriberWaitingListCount = response.getSubscriberWaitingListCount();

                barChartSubscribersOrders.getData().clear();
                barChartSubscribersWaiting.getData().clear();

                initiateSubscriberOrdersBarChart();
                initiateSubscriberWaitingBarChart();
                putDataInCharts();
            }
            case SUBSCRIBER_REPORT_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, was unable to retrieve report data.");
        }
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearReport();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    private void clearReport() {
        barChartSubscribersOrders.getData().clear();
        barChartSubscribersWaiting.getData().clear();
    }

    private void requestSubscribersReportWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(e -> controller.requestSubscribersReport());
                    }
                });
            }
        });
    }

    private void putDataInCharts() {
        XYChart.Series<String, Number> ordersSeries = new XYChart.Series<>();
        ordersSeries.setName("Orders");
        subscriberOrdersCount.keySet().forEach(date ->
                ordersSeries.getData().add(new XYChart.Data<>(String.valueOf(date.getDayOfMonth()), subscriberOrdersCount.get(date)))
        );
        barChartSubscribersOrders.getData().add(ordersSeries);

        XYChart.Series<String, Number> waitingSeries = new XYChart.Series<>();
        waitingSeries.setName("Waiting List");
        subscriberWaitingListCount.keySet().forEach(date ->
                waitingSeries.getData().add(new XYChart.Data<>(String.valueOf(date.getDayOfMonth()), subscriberWaitingListCount.get(date)))
        );
        barChartSubscribersWaiting.getData().add(waitingSeries);
    }

    private void initiateSubscriberOrdersBarChart() {
        List<String> days = subscriberOrdersCount.keySet().stream()
                .map(date -> String.valueOf(date.getDayOfMonth()))
                .collect(Collectors.toList());
        xDayAxisOrders.setCategories(FXCollections.observableArrayList(days));
        xDayAxisOrders.setLabel("Day of Month");
        yOrdersAxis.setLabel("Number of Orders");
        yOrdersAxis.setAutoRanging(true);
    }

    private void initiateSubscriberWaitingBarChart() {
        List<String> days = subscriberWaitingListCount.keySet().stream()
                .map(date -> String.valueOf(date.getDayOfMonth()))
                .collect(Collectors.toList());
        xDayAxisWaiting.setCategories(FXCollections.observableArrayList(days));
        xDayAxisWaiting.setLabel("Day of Month");
        yWaitingAxis.setLabel("Number of Subscribers Waiting");
        yWaitingAxis.setAutoRanging(true);
    }

    public static String getCurrentMonth() {
        return LocalDate.now()
                .getMonth()
                .getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
}
