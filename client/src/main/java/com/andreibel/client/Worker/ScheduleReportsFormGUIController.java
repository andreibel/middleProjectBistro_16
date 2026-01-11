package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.SchedulesReportResponse;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
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

/**
 * GUI controller for displaying schedule reports in the restaurant system.
 *
 * <p>This controller shows both line and bar charts:
 * <ul>
 *     <li>Line chart for customer arrivals and departures per time slot for each day.</li>
 *     <li>Bar chart for late arrivals and delays per day of the month.</li>
 * </ul>
 * Staff can select individual days or view all days using the combo box.</p>
 */
public class ScheduleReportsFormGUIController implements IServerResponseListener {

    /** Label showing report title. */
    @FXML
    private Label lblTitle;

    /** ComboBox to select which day's data to track in the line chart. */
    @FXML
    private ComboBox<XYChart.Series<String, Number>> comboBoxTrack;

    /** LineChart showing customer arrivals and departures by time. */
    @FXML
    private LineChart<String, Number> lineChartArrivalsDepartures;

    /** X-axis for the line chart representing hours. */
    @FXML
    private CategoryAxis xAxis;

    /** Y-axis for the line chart representing number of people. */
    @FXML
    private NumberAxis yAxis;

    /** BarChart showing late arrivals and delays by day of month. */
    @FXML
    private BarChart<String, Number> barChartLatesDelays;

    /** X-axis for the bar chart representing day of the month. */
    @FXML
    private CategoryAxis xDayAxis;

    /** Y-axis for the bar chart representing number of people. */
    @FXML
    private NumberAxis yPeopleAxis;

    /** Root pane of the form. */
    @FXML
    private AnchorPane rootPane;

    private Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture;
    private Map<LocalDate, Integer> customerLate;
    private Map<LocalDate, Integer> customerDelay;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private int interval;
    private List<XYChart.Series<String, Number>> days = new ArrayList<>();

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /**
     * Initializes the controller after the FXML is loaded.
     *
     * <p>Sets the title, registers this controller as a server listener,
     * and requests schedule report data when the scene is shown.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        lblTitle.setText("Schedules Report for Month: " + getCurrentMonth());
        requestScheduleReportWhenSceneIsShown();
    }

    /**
     * Handles responses received from the server.
     *
     * @param message the server message
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()){
            case SCHEDULES_REPORT_RESPONSE -> {
                SchedulesReportResponse data = (SchedulesReportResponse) message.getData();
                try {
                    customerArriveDeparture = data.getCustomerArriveDeparture();
                    customerLate = data.getCustomerLate();
                    customerDelay = data.getCustomerDelay();
                    openingTime = data.getOpeningTime();
                    closingTime = data.getClosingTime();
                    interval = data.getInterval();

                    clearCharts();
                    setupLineChart();
                    setupBarChart();
                } catch (Exception e) {
                    BistroUtilities.showMessage("Bistro Restaurant", "No data to show.");
                }
            }
            case SCHEDULES_REPORT_ERROR -> {
                BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to get the report.");
            }
        }
    }

    /**
     * Handles clicks on the "Go Back" button.
     *
     * <p>Clears all charts and navigates back to the staff main screen.</p>
     *
     * @param event the action event
     * @throws IOException if FXML cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearCharts();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    /**
     * Handles selection changes in the combo box to show a single day or all days.
     *
     * @param event the action event
     */
    @FXML
    private void onComboBoxTrackValueSelected(ActionEvent event) {
        XYChart.Series<String, Number> selectedSeries = comboBoxTrack.getValue();
        lineChartArrivalsDepartures.getData().clear();

        if (selectedSeries != null) {
            if ("All Days".equals(selectedSeries.getName())) {
                lineChartArrivalsDepartures.getData().addAll(days);
            } else {
                lineChartArrivalsDepartures.getData().add(selectedSeries);
            }
        }
    }

    /**
     * Sets up the line chart with customer arrivals and departures per time slot.
     */
    private void setupLineChart() {
        days.clear();
        lineChartArrivalsDepartures.getData().clear();

        List<String> hours = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime tempTime = openingTime;
        while (!tempTime.isAfter(closingTime)) {
            hours.add(tempTime.format(formatter));
            tempTime = tempTime.plusMinutes(interval);
        }
        xAxis.setCategories(FXCollections.observableArrayList(hours));
        xAxis.setLabel("Hour");
        yAxis.setLabel("Number of People");
        yAxis.setAutoRanging(true);

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

    /**
     * Configures the combo box to select "All Days" or individual days.
     */
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
            public XYChart.Series<String, Number> fromString(String string) { return null; }
        });

        comboBoxTrack.getSelectionModel().selectFirst();
    }

    /**
     * Sets up the bar chart showing late arrivals and delays per day.
     */
    private void setupBarChart() {
        barChartLatesDelays.getData().clear();

        XYChart.Series<String, Number> lateSeries = new XYChart.Series<>();
        lateSeries.setName("Late");
        XYChart.Series<String, Number> delaySeries = new XYChart.Series<>();
        delaySeries.setName("Delay");

        for (LocalDate date : customerArriveDeparture.keySet()) {
            String dayLabel = String.valueOf(date.getDayOfMonth());
            lateSeries.getData().add(new XYChart.Data<>(dayLabel, customerLate.getOrDefault(date, 0)));
            delaySeries.getData().add(new XYChart.Data<>(dayLabel, customerDelay.getOrDefault(date, 0)));
        }

        barChartLatesDelays.getData().addAll(lateSeries, delaySeries);

        List<String> daysOfMonth = customerArriveDeparture.keySet().stream()
                .map(d -> String.valueOf(d.getDayOfMonth()))
                .toList();
        xDayAxis.setCategories(FXCollections.observableArrayList(daysOfMonth));
        xDayAxis.setLabel("Day of Month");
        yPeopleAxis.setLabel("Number of People");
        yPeopleAxis.setAutoRanging(true);
    }

    /**
     * Clears all charts.
     */
    private void clearCharts() {
        lineChartArrivalsDepartures.getData().clear();
        barChartLatesDelays.getData().clear();
        days.clear();
    }

    /**
     * Requests the schedules report from the server when the scene is shown.
     */
    private void requestScheduleReportWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        controller.requestSchedulesReport();
                    }
                });
            }
        });
    }

    /**
     * Returns the current month as a full string (e.g., "January").
     *
     * @return current month name
     */
    public static String getCurrentMonth() {
        return LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
}