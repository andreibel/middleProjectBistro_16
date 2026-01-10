package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CurrentDiningFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;
    @FXML
    private TableView<Dining> tblViewCurrentDining;
    @FXML
    private TableColumn<Dining, LocalDate> colOrderDate;
    @FXML
    private TableColumn<Dining, LocalTime> colOrderTime;
    @FXML
    private TableColumn<Dining, Integer> colNumberOfPeople;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button btnGoBack;

    private final ObservableList<Dining> diningList = FXCollections.observableArrayList();
    private BistroClientController controller;

    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        tblViewCurrentDining.setItems(diningList);
        tblViewCurrentDining.setEditable(false);

        initializeTableColumns();
        lblTitle.setText("Current Dining List for: " + getCurrentDate());
        requestCurrentDiningWhenSceneIsShown();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_ALL_ARRIVED_AND_NOT_COMPLETE_RESPONSE ->{
                    List<Dining> dining = new ArrayList<>();
                    for (OrderResponse order : (List<OrderResponse>) message.getData())
                        dining.add(new Dining(order.getOrderDateTime().toLocalDate(), order.getOrderDateTime().toLocalTime(), order.getNumberOfGuests()));
                    populateTable(dining);
            }
            case GET_ALL_ARRIVED_AND_NOT_COMPLETE_ERROR ->
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            "Due to server error, it was unable to retrieve the current dining list."
                    );
        }
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        diningList.clear();
        populateTable(diningList);
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    private void initializeTableColumns() {
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOrderTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colNumberOfPeople.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
    }

    private void populateTable(List<Dining> data) {
        diningList.setAll(data);
    }

    private void requestCurrentDiningWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        controller.requestCurrentDiningList();
                    }
                });
            }
        });
    }

    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Dining{
        private LocalDate date;
        private LocalTime time;
        private Integer numberOfGuests;
    }
}
