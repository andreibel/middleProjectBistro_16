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
 * <p>This controller manages the visualization of daily schedules, including:
 * <ul>
 *     <li>A line chart showing customer arrivals and departures per time slot.</li>
 *     <li>A bar chart showing late arrivals and delays per day of the month.</li>
 * </ul>
 * Staff can select individual days or view all days using the combo box.</p>
 */
public class ScheduleReportsFormGUIController implements IServerResponseListener {

    /** Label showing the report title (e.g., month name). */
    @FXML
    private Label lblTitle;

    /** ComboBox to select which day's data to display in the line chart. */
    @FXML
    private ComboBox<XYChart.Series<String, Number>> comboBoxTrack;

    /** LineChart displaying arrivals and departures by hour. */
    @FXML
    private LineChart<String, Number> lineChartArrivalsDepartures;

    /** X-axis for the line chart representing hours. */
    @FXML
    private CategoryAxis xAxis;

    /** Y-axis for the line chart representing number of people. */
    @FXML
    private NumberAxis yAxis;

    /** BarChart showing late arrivals and delays per day. */
    @FXML
    private BarChart<String, Number> barChartLatesDelays;

    /** X-axis for the bar chart representing days of the month. */
    @FXML
    private CategoryAxis xDayAxis;

    /** Y-axis for the bar chart representing number of people. */
    @FXML
    private NumberAxis yPeopleAxis;

    /** Root pane of the form, used for scene detection and event listeners. */
    @FXML
    private AnchorPane rootPane;

    /** Map storing customer arrivals and departures per date and time. */
    private Map<LocalDate, Map<LocalTime, Integer>> customerArriveDeparture;

    /** Map storing the number of late arrivals per date. */
    private Map<LocalDate, Integer> customerLate;

    /** Map storing the number of delayed arrivals per date. */
    private Map<LocalDate, Integer> customerDelay;

    /** Opening time of the restaurant. */
    private LocalTime openingTime;

    /** Closing time of the restaurant. */
    private LocalTime closingTime;

    /** Interval in minutes between time slots in the line chart. */
    private int interval;

    /** List of series representing each day, used for the line chart and combo box. */
    private List<XYChart.Series<String, Number>> days = new ArrayList<>();

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>Sets up the title label, registers this controller as a server listener,
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
     * <p>Depending on the message type, this method updates the charts with new data
     * or shows an error message if the server failed to provide the report.</p>
     *
     * @param message the server message
     */
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case SCHEDULES_REPORT_RESPONSE -> {
                SchedulesReportResponse data = (SchedulesReportResponse) message.getData();
                try {
                    customerArriveDeparture = reverseMapOrder(data.getCustomerArriveDeparture());
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
     * @throws IOException if the FXML cannot be loaded
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
        if (selectedSeries == null) return;

        if ("All Days".equals(selectedSeries.getName())) {
            addSeriesToChart(days);
        } else {
            addSeriesToChart(List.of(selectedSeries));
        }
    }

    /**
     * Configures and populates the line chart with customer arrivals and departures.
     *
     * <p>Each date gets its own series, and the X-axis is labeled by hours.
     * The series are also added to the combo box for day selection.</p>
     */
    private void setupLineChart() {
        lineChartArrivalsDepartures.getData().clear();
        days.clear();

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

        int dayCounter = 1;
        for (LocalDate date : customerArriveDeparture.keySet()) {
            Map<LocalTime, Integer> times = customerArriveDeparture.get(date);
            if (times == null) continue;

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(String.valueOf(dayCounter));

            List<LocalTime> sortedTimes = new ArrayList<>(times.keySet());
            sortedTimes.sort(Comparator.naturalOrder());

            for (LocalTime time : sortedTimes) {
                series.getData().add(new XYChart.Data<>(time.format(formatter), times.get(time)));
            }

            days.add(series);
            dayCounter++;
        }

        addSeriesToChart(days);
        setupComboBox();
    }

    /**
     * Adds one or more series to the line chart and dynamically adjusts the Y-axis bounds.
     *
     * @param seriesList the series to display
     */
    private void addSeriesToChart(List<XYChart.Series<String, Number>> seriesList) {
        lineChartArrivalsDepartures.getData().clear();
        double maxY = 0;

        for (XYChart.Series<String, Number> s : seriesList) {
            if (s == null) continue;

            XYChart.Series<String, Number> copy = copySeries(s);
            lineChartArrivalsDepartures.getData().add(copy);

            for (XYChart.Data<String, Number> data : copy.getData()) {
                if (data.getYValue() != null) {
                    maxY = Math.max(maxY, data.getYValue().doubleValue());
                }
            }
        }

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxY * 1.2);
        yAxis.setTickUnit(Math.max(1, Math.ceil(maxY / 10.0)));
    }

    /**
     * Sets up the combo box to allow selection of "All Days" or individual days.
     */
    private void setupComboBox() {
        XYChart.Series<String, Number> allDays = new XYChart.Series<>();
        allDays.setName("All Days");

        List<XYChart.Series<String, Number>> comboItems = new ArrayList<>();
        comboItems.add(allDays);
        comboItems.addAll(days.stream().filter(Objects::nonNull).toList());

        comboBoxTrack.setItems(FXCollections.observableArrayList(comboItems));

        comboBoxTrack.setConverter(new StringConverter<>() {
            @Override
            public String toString(XYChart.Series<String, Number> series) {
                return series != null ? series.getName() : "";
            }

            @Override
            public XYChart.Series<String, Number> fromString(String string) {
                return null;
            }
        });

        comboBoxTrack.getSelectionModel().selectFirst();

        comboBoxTrack.setOnAction(event -> {
            XYChart.Series<String, Number> selectedSeries = comboBoxTrack.getValue();
            if (selectedSeries == null) return;

            if ("All Days".equals(selectedSeries.getName())) {
                addSeriesToChart(days);
            } else {
                addSeriesToChart(List.of(selectedSeries));
            }
        });
    }

    /**
     * Creates a deep copy of a chart series, including all data points.
     *
     * @param original the original series to copy
     * @return a new copy of the series
     */
    private XYChart.Series<String, Number> copySeries(XYChart.Series<String, Number> original) {
        XYChart.Series<String, Number> copy = new XYChart.Series<>();
        copy.setName(original != null ? original.getName() : "");
        if (original != null && original.getData() != null) {
            for (XYChart.Data<String, Number> data : original.getData()) {
                if (data != null) {
                    copy.getData().add(new XYChart.Data<>(data.getXValue(), data.getYValue()));
                }
            }
        }
        return copy;
    }

    /**
     * Configures the bar chart to show late arrivals and delays per day.
     */
    private void setupBarChart() {
        barChartLatesDelays.getData().clear();

        XYChart.Series<String, Number> lateSeries = new XYChart.Series<>();
        lateSeries.setName("Late");
        XYChart.Series<String, Number> delaySeries = new XYChart.Series<>();
        delaySeries.setName("Delay");

        for (LocalDate date : customerArriveDeparture.keySet()) {
            lateSeries.getData().add(new XYChart.Data<>(String.valueOf(date.getDayOfMonth()), customerLate.getOrDefault(date, 0)));
            delaySeries.getData().add(new XYChart.Data<>(String.valueOf(date.getDayOfMonth()), customerDelay.getOrDefault(date, 0)));
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
     * Clears all charts and the combo box selection.
     */
    private void clearCharts() {
        lineChartArrivalsDepartures.getData().clear();
        barChartLatesDelays.getData().clear();
        days.clear();

        if (comboBoxTrack != null) {
            comboBoxTrack.getItems().clear();
            comboBoxTrack.setValue(null);
        }
    }

    /**
     * Requests schedule report data from the server once the scene is shown.
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
     * Reverses the order of a map of LocalDate to time-value mappings.
     *
     * <p>This is used to display the latest dates first in the charts.</p>
     *
     * @param map the original map
     * @return a new map with reversed order of entries
     */
    private Map<LocalDate, Map<LocalTime, Integer>> reverseMapOrder(Map<LocalDate, Map<LocalTime, Integer>> map) {
        Map<LocalDate, Map<LocalTime, Integer>> result = new TreeMap<>();
        List<Map.Entry<LocalDate, Map<LocalTime, Integer>>> list = new ArrayList<>(map.entrySet());
        Collections.reverse(list);
        for (Map.Entry<LocalDate, Map<LocalTime, Integer>> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Returns the current month as a full string (e.g., "January").
     *
     * @return the current month name
     */
    public static String getCurrentMonth() {
        return LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
}