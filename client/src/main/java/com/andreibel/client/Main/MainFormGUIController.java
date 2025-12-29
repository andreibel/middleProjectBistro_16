package com.andreibel.client.Main;

import com.andreibel.client.Client.BistroClientController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;
import com.andreibel.client.util.ScreenTransfer;

import java.io.IOException;

public class MainFormGUIController {

    @FXML
    private Label lblMainTitle;
    @FXML
    private Label lblHoverDetails;
    @FXML
    private Button btnOrder;
    @FXML
    private Button btnArrived;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSubscriber;
    @FXML
    private Button btnWorker;
    private BistroClientController controller;

    @FXML
    public void initialize() {
    }

    public void setController(BistroClientController controller) {
        this.controller = controller;
    }

    @FXML
    public void onOrderButtonClicked(ActionEvent event) throws IOException {
        ScreenTransfer.switchScreen(
                event,
                "/Order/OrderForm.fxml",
                "OrderFromGUIController"
        );
    }


    @FXML
    public void onArrivedButtonClicked(ActionEvent event) throws IOException {
        ScreenTransfer.switchScreen(
            event,
            "/Table/GetTableForm.fxml",
            "GetTableForm"
    );
}


@FXML
    public void onCancelButtonClicked(ActionEvent event) throws IOException {
    ScreenTransfer.switchScreen(
            event,
            "/Order/CancelOrderForm.fxml",
            "CancelOrderForm"
    );
}

    @FXML
    public void onSubscriberButtonClicked(ActionEvent event) throws IOException {
        ScreenTransfer.switchScreen(
                event,
                "/Subscriber/SubscriberLoginForm.fxml",
                "Subscriber Login"
        );
    }
    @FXML
    public void onWorkerButtonClicked(ActionEvent event) throws IOException {
        ScreenTransfer.switchScreen(
                event,
                "/Worker/WorkerLoginForm.fxml",
                "Worker Login"
        );
    }

}
