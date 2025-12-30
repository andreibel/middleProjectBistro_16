package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CurrentDiningFormGUIController implements IServerResponseListener {

    @FXML
    private TableView<OrderResponse> tblViewCurrentDining;
    @FXML
    private TableColumn<OrderResponse, Integer> colOrderNumber;
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

    private BistroClientController controller;


    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        setTableView();
        onSceneShown();
        tblViewCurrentDining.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
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

    @Override
    public void onServerResponse(Message message){

    }

    private void setTableView(){


    }

    private void onSceneShown(){

    }


}
