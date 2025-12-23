package com.andreibel.client.Main;

import com.andreibel.client.BistroClientGUI;
import com.andreibel.client.Client.BistroClient;
import com.andreibel.client.Client.BistroClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainForm extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainForm.class.getResource("MainForm.fxml")
        );

        var params = getParameters().getNamed();

        String host = params.getOrDefault("host", "localhost");
        int port = Integer.parseInt(params.getOrDefault("port", "8080"));
        System.out.println("Connecting to " + host + ":" + port);
        Parent root = fxmlLoader.load();
        MainFormGUIController guiController = fxmlLoader.getController();

        // create client + controller + wire them
        BistroClient client = new BistroClient(host, port); // adjust port/host as needed
        BistroClientController appController = new BistroClientController(guiController);
        appController.attachClient(client);
        guiController.setController(appController);

        client.connectToServer();

        Scene scene = new Scene(root, 600, 411);
        stage.setTitle("Bistro Restaurant");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
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
