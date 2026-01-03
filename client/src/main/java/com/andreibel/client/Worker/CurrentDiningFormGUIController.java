package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
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

public class CurrentDiningFormGUIController implements IServerResponseListener {

    @FXML
    private Label lblTitle;
    //@FXML
    //private TableView<OrderResponse> tblViewCurrentDining;
    @FXML
    private TableColumn<OrderResponse, LocalDateTime> colOrderDate;
    @FXML
    private TableColumn<OrderResponse, LocalDateTime> colOrderTime;
    @FXML
    private TableColumn<OrderResponse, Integer> colNumberOfPeople;
    @FXML
    private TableColumn<OrderResponse, LocalDateTime> colTimeArrived;
    @FXML
    private Button btnGoBack;

    // ObservableList<WaitingListResponse> diningList;
    private BistroClientController controller;


    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

//        diningList = FXCollections.observableArrayList();
//        tblViewCurrentDining.setItems(diningList);
//        tblViewCurrentDining.setEditable(false);

        initializeTableColumns();
        lblTitle.setText("Current Waiting List for: " + getCurrentDate());
        requestCurrentDiningWhenSceneIsShown();
        controller.requestCurrentWaitingList();

    }

    @Override
    public void onServerResponse(Message message){

    }

    private void initializeTableColumns(){

    }

    private void requestCurrentDiningWhenSceneIsShown(){
//        tblViewCurrentDining.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
//            if (newScene != null) {
//                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
//                    if (newWindow != null) {
//                        newWindow.setOnShown(event -> {
//                            controller.requestCurrentWaitingList();
//                        });
//                    }
//                });
//            }
//        });
    }
    private String getCurrentDate() {
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
