package com.andreibel.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BistroClientGUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                BistroClientGUI.class.getResource("ClientOrdersGUI.fxml")
        );

        Parent root = fxmlLoader.load();
        BistroClientGUIController guiController = fxmlLoader.getController();

        // create client + controller + wire them
        BistroClient client = new BistroClient("localhost", 8080); // adjust port/host as needed
        BistroClientController appController = new BistroClientController(guiController);
        appController.attachClient(client);
        guiController.setController(appController);

        client.connectToServer();
        appController.requestOrders();   // load initial data

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Bistro Restaurant Alpha Build");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            try {
                client.closeConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        stage.show();
    }
}