package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.APICallType;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CurrentDiningFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;
    @FXML
    private TableView<OrderResponse> tblViewCurrentDining;
    @FXML
    private TableColumn<OrderResponse, LocalDateTime> colOrderDateTime;
    @FXML
    private TableColumn<OrderResponse, Integer> colNumberOfPeople;
    @FXML
    private Button btnGoBack;

     ObservableList<OrderResponse> diningList;
    private BistroClientController controller;


    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        diningList = FXCollections.observableArrayList();
        tblViewCurrentDining.setItems(diningList);
        tblViewCurrentDining.setEditable(false);

        initializeTableColumns();
        lblTitle.setText("Current Waiting List for: " + getCurrentDate());
        requestCurrentDiningWhenSceneIsShown();
        controller.requestCurrentDiningList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message){
        if (message.getType() == APICallType.GET_ALL_ARRIVED_AND_NOT_COMPLETE_RESPONSE) {
            populateTable((List<OrderResponse>) message.getData());
        }
        else if (message.getType() == APICallType.GET_ALL_ARRIVED_AND_NOT_COMPLETE_ERROR) {
            BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to retrieve the current dining list.");
        }
    }

    private void initializeTableColumns() {
        colOrderDateTime.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
        colNumberOfPeople.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
    }


    private void populateTable(List<OrderResponse> data) {
        diningList.clear();
        diningList.addAll(data);
    }

    private void requestCurrentDiningWhenSceneIsShown(){
        tblViewCurrentDining.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            controller.requestCurrentDiningList();
                        });
                    }
                });
            }
        });
    }
    private String getCurrentDate() {
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
