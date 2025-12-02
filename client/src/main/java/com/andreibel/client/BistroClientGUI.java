package com.andreibel.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import com.andreibel.client.message.DTO.*;
import java.util.List;

public class BistroClientGUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {
//        BistroClient client = new BistroClient("localhost", 8080, new BistroClientController());
//        client.connectToServer();
//        //Thread.sleep(1000);
//        BistroClientController.requestOrders();
//        //Thread.sleep(1000);
//        List<OrderResponse> orders = BistroClientController.getListOfOrders();
        FXMLLoader fxmlLoader = new FXMLLoader(BistroClientGUI.class.getResource("ClientOrdersGUI.fxml"));
//        BistroClientGUIController controller = fxmlLoader.getController();
//        controller.setOrdersToGUI(orders);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Bistro Restaurant Alpha Build");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }
}
