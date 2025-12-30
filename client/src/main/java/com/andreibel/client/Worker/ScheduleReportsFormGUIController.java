package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//TODO: work on onServerResponse, initialize and combobox event

public class ScheduleReportsFormGUIController implements IServerResponseListener {
    @FXML
    private Label lblTitle;
    @FXML
    private ComboBox<Integer> comboBoxTrack;
    @FXML
    private LineChart<String, Number> lineChartArrivalsDepartures;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private BarChart<String, Number> barChartLatesDelays;
    @FXML
    private CategoryAxis xDayAxis;
    @FXML
    private NumberAxis yPeopleAxis;
    @FXML
    private Button btnGoBack;

    @FXML
    private AnchorPane rootPane;

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        initiateArrivalsDeparturesLineChart();
        initiateLeavesDelaysBarChart();

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
        onSceneShown();
        addChartsDataForTest();
    }

    @Override
    public void onServerResponse(Message message) {

    }
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearReport();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }
    @FXML
    private void onComboBoxTrackValueSelected(ActionEvent event) {
        int trackedDay = comboBoxTrack.getValue();
    }

    private void initiateArrivalsDeparturesLineChart(){
        //X Axis
        List<String> hours = new ArrayList<>();
        LocalTime time = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(21, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        while (!time.isAfter(end)) {
            hours.add(time.format(formatter));
            time = time.plusMinutes(30);
        }
        xAxis.setCategories(FXCollections.observableArrayList(hours));
        xAxis.setLabel("Hour");

        //Y Axis
        yAxis.setLabel("Number of People");
        yAxis.setAutoRanging(true);
    }

    private void initiateLeavesDelaysBarChart(){
        List<String> days = new ArrayList<>();
        for (int day = 1; day <= 30; day++)
            days.add(Integer.toString(day));
        xDayAxis.setCategories(FXCollections.observableArrayList(days));
        xDayAxis.setLabel("Day of Month");
        yPeopleAxis.setLabel("Number of People");
        yPeopleAxis.setAutoRanging(true);
    }

    private void onSceneShown() {
        controller.requestScheduleReportForCurrentMonth();
    }

    private void clearReport(){
        lineChartArrivalsDepartures.getData().clear();
        barChartLatesDelays.getData().clear();
    }

    //For Testing Purpose REMOVE BEFORE FINAL PRODUCT
    private void addChartsDataForTest(){
        //Adding Data for test
        XYChart.Series<String, Number> monday = new XYChart.Series<>();
        monday.setName("Monday");

        monday.getData().add(new XYChart.Data<>("09:00", 5));
        monday.getData().add(new XYChart.Data<>("10:00", 18));
        monday.getData().add(new XYChart.Data<>("11:00", 30));
        monday.getData().add(new XYChart.Data<>("12:00", 38));
        monday.getData().add(new XYChart.Data<>("13:00", 42));
        monday.getData().add(new XYChart.Data<>("14:00", 40));
        monday.getData().add(new XYChart.Data<>("15:00", 35));
        monday.getData().add(new XYChart.Data<>("16:00", 28));
        monday.getData().add(new XYChart.Data<>("17:00", 18));
        monday.getData().add(new XYChart.Data<>("18:00", 10));
        monday.getData().add(new XYChart.Data<>("19:00", 4));
        monday.getData().add(new XYChart.Data<>("21:00", 0));

        lineChartArrivalsDepartures.getData().add(monday);

        XYChart.Series<String, Number> tuesday = new XYChart.Series<>();
        tuesday.setName("Tuesday");

        tuesday.getData().add(new XYChart.Data<>("09:00", 8));
        tuesday.getData().add(new XYChart.Data<>("10:00", 22));
        tuesday.getData().add(new XYChart.Data<>("11:00", 35));
        tuesday.getData().add(new XYChart.Data<>("12:00", 45));
        tuesday.getData().add(new XYChart.Data<>("13:00", 50));
        tuesday.getData().add(new XYChart.Data<>("14:00", 48));
        tuesday.getData().add(new XYChart.Data<>("15:00", 42));
        tuesday.getData().add(new XYChart.Data<>("16:00", 32));
        tuesday.getData().add(new XYChart.Data<>("17:00", 22));
        tuesday.getData().add(new XYChart.Data<>("18:00", 12));
        tuesday.getData().add(new XYChart.Data<>("19:00", 6));
        tuesday.getData().add(new XYChart.Data<>("21:00", 1));
        lineChartArrivalsDepartures.getData().add(tuesday);

        XYChart.Series<String, Number> wednesday = new XYChart.Series<>();
        wednesday.setName("Wednesday");

        wednesday.getData().add(new XYChart.Data<>("09:00", 3));
        wednesday.getData().add(new XYChart.Data<>("10:00", 10));
        wednesday.getData().add(new XYChart.Data<>("11:00", 20));
        wednesday.getData().add(new XYChart.Data<>("12:00", 28));
        wednesday.getData().add(new XYChart.Data<>("13:00", 30));
        wednesday.getData().add(new XYChart.Data<>("14:00", 27));
        wednesday.getData().add(new XYChart.Data<>("15:00", 23));
        wednesday.getData().add(new XYChart.Data<>("16:00", 18));
        wednesday.getData().add(new XYChart.Data<>("17:00", 12));
        wednesday.getData().add(new XYChart.Data<>("18:00", 7));
        wednesday.getData().add(new XYChart.Data<>("19:00", 3));
        wednesday.getData().add(new XYChart.Data<>("21:00", 0));
        lineChartArrivalsDepartures.getData().add(wednesday);

        XYChart.Series<String, Number> lateSeries = new XYChart.Series<>();
        lateSeries.setName("Late");
        XYChart.Series<String, Number> delaySeries = new XYChart.Series<>();
        delaySeries.setName("Delay");

        lateSeries.getData().add(new XYChart.Data<>("1", 5));
        delaySeries.getData().add(new XYChart.Data<>("1", 2));

        lateSeries.getData().add(new XYChart.Data<>("2", 8));
        delaySeries.getData().add(new XYChart.Data<>("2", 4));

        lateSeries.getData().add(new XYChart.Data<>("3", 3));
        delaySeries.getData().add(new XYChart.Data<>("3", 1));

        lateSeries.getData().add(new XYChart.Data<>("4", 10));
        delaySeries.getData().add(new XYChart.Data<>("4", 6));

        lateSeries.getData().add(new XYChart.Data<>("5", 6));
        delaySeries.getData().add(new XYChart.Data<>("5", 2));

        barChartLatesDelays.getData().addAll(lateSeries, delaySeries);
    }
}
