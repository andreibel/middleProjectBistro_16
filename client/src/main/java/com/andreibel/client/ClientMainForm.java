package com.andreibel.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientMainForm extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMainForm.class.getResource("ClientOrdersDesign.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Bistro Restaurant Alpha Build");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }

    private void establishConnection() {

    }
}
