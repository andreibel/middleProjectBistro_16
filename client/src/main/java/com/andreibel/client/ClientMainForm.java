package com.andreibel.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class ClientMainForm extends Application {



    //A Client instance
   // private static BistroClient client = new BistroClient();
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMainForm.class.getResource("ClientOrdersDesign.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Bistro Restaurant Alpha Build");
        stage.setScene(scene);
        pushOrdersFromServer();
        stage.setResizable(false);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }

    public static List<Object> pushOrdersFromServer(){

    }
}
