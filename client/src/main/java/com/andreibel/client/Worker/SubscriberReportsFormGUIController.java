package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TODO: Need to finish onServerResponse, setting the data in each of the line charts

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

    private List<SubscriberResponse> subscribers;
    //NEED TO CREATE IN MESSAGE API WaitingResponse
    //private List<WaitingResponse> waitingSubscribers;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        lblTitle.setText("Subscribers Report for Month: " + getCurrentMonth());
        initiateSubscriberOrdersBarChart();
        initiateSubscriberWaitingBarChart();
        onSceneShown();

        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
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

    //NEED TO ADD GET_ALL_SUBSCRIBERS_ERROR APICallType
    @Override
    public void onServerResponse(Message message) {
        if (message.getType() != APICallType.GET_ALL_SUBSCRIBERS_RESPONSE)
            return;
//        if (message.getType() == APICallType.GET_ALL_SUBSCRIBERS_ERROR) {
//            BistroUtilities.showMessage("Bistro Restaurant - Subscribers Report", "Unable to fetch subscribers report due to server error");
//            return;
//        }

    }
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearReport();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro - Staff Area");
    }

    private void clearReport(){
        barChartSubscribersOrders.getData().clear();
        barChartSubscribersWaiting.getData().clear();
    }

    private void onSceneShown() {
        //fetch data from DB
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

    public static String getCurrentMonth() {
        return LocalDate.now()
                .getMonth()
                .getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

}
