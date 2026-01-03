package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.SchedulesReportResponse;
import com.andreibel.message.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class ScheduleReportsFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;
    @FXML
    private ComboBox<XYChart.Series<String, Number>> comboBoxTrack;
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

    private Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture;
    private Map<LocalDate, Integer> customerLate;
    private Map<LocalDate, Integer> customerDelay;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private int interval;
    private List<XYChart.Series<String, Number>> days = new ArrayList<>();

    private BistroClientController controller;

    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        lblTitle.setText("Subscribers Report for Month: " + getCurrentMonth());
        requestScheduleReportWhenSceneIsShown();
        controller.requestSchedulesReport();
    }

    @Override
    public void onServerResponse(Message message) {
        if (message.getType() == APICallType.SCHEDULES_REPORT_RESPONSE) {
            Platform.runLater(() -> {
                SchedulesReportResponse data = (SchedulesReportResponse) message.getData();

                customerArriveDeparture = data.getCustomerArriveDeparture();
                customerLate = data.getCustomerLate();
                customerDelay = data.getCustomerDelay();
                openingTime = data.getOpeningTime();
                closingTime = data.getClosingTime();
                interval = data.getInterval();

                clearCharts();
                setupLineChart();
                setupBarChart();
            });
        } else if (message.getType() == APICallType.SCHEDULES_REPORT_ERROR) {
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to get the report.");
        }
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearCharts();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    @FXML
    private void onComboBoxTrackValueSelected(ActionEvent event) {
        XYChart.Series<String, Number> selectedSeries = comboBoxTrack.getValue();
        lineChartArrivalsDepartures.getData().clear();

        if (selectedSeries != null) {
            if (selectedSeries.getName().equals("All Days")) {
                lineChartArrivalsDepartures.getData().addAll(days);
            } else {
                lineChartArrivalsDepartures.getData().add(selectedSeries);
            }
        }
    }

    private void setupLineChart() {
        days.clear();
        lineChartArrivalsDepartures.getData().clear();

        // X Axis: hours
        List<String> hours = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime tempTime = openingTime;
        while (!tempTime.isAfter(closingTime)) {
            hours.add(tempTime.format(formatter));
            tempTime = tempTime.plusMinutes(interval);
        }
        xAxis.setCategories(FXCollections.observableArrayList(hours));
        xAxis.setLabel("Hour");

        // Y Axis
        yAxis.setLabel("Number of People");
        yAxis.setAutoRanging(true);

        // Populate series per day
        int dayCounter = 1;
        for (LocalDate date : customerArriveDeparture.keySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(String.valueOf(dayCounter));

            Map<LocalTime, Integer> times = customerArriveDeparture.get(date);
            for (LocalTime time : times.keySet()) {
                series.getData().add(new XYChart.Data<>(time.format(formatter), times.get(time)));
            }

            lineChartArrivalsDepartures.getData().add(series);
            days.add(series);
            dayCounter++;
        }

        setupComboBox();
    }

    private void setupComboBox() {
        XYChart.Series<String, Number> allDays = new XYChart.Series<>();
        allDays.setName("All Days");

        List<XYChart.Series<String, Number>> comboItems = new ArrayList<>();
        comboItems.add(allDays);
        comboItems.addAll(days);

        comboBoxTrack.setItems(FXCollections.observableArrayList(comboItems));
        comboBoxTrack.setConverter(new StringConverter<>() {
            @Override
            public String toString(XYChart.Series<String, Number> series) {
                return series.getName();
            }

            @Override
            public XYChart.Series<String, Number> fromString(String string) {
                return null;
            }
        });

        comboBoxTrack.getSelectionModel().selectFirst();
    }
    @SuppressWarnings("unchecked")
    private void setupBarChart() {
        barChartLatesDelays.getData().clear();

        XYChart.Series<String, Number> lateSeries = new XYChart.Series<>();
        lateSeries.setName("Late");
        XYChart.Series<String, Number> delaySeries = new XYChart.Series<>();
        delaySeries.setName("Delay");

        for (LocalDate date : customerLate.keySet()) {
            String dayLabel = String.valueOf(date.getDayOfMonth());
            lateSeries.getData().add(new XYChart.Data<>(dayLabel, customerLate.get(date)));
            delaySeries.getData().add(new XYChart.Data<>(dayLabel, customerDelay.get(date)));
        }

        barChartLatesDelays.getData().addAll(lateSeries, delaySeries);

        // Configure axes
        List<String> daysOfMonth = new ArrayList<>();
        for (LocalDate date : customerArriveDeparture.keySet()) {
            daysOfMonth.add(String.valueOf(date.getDayOfMonth()));
        }
        xDayAxis.setCategories(FXCollections.observableArrayList(daysOfMonth));
        xDayAxis.setLabel("Day of Month");
        yPeopleAxis.setLabel("Number of People");
        yPeopleAxis.setAutoRanging(true);
    }

    private void clearCharts() {
        lineChartArrivalsDepartures.getData().clear();
        barChartLatesDelays.getData().clear();
        days.clear();
    }

    private void requestScheduleReportWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event ->
                                controller.requestSchedulesReport()
                        );
                    }
                });
            }
        });
    }

    public static String getCurrentMonth() {
        return LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
}
