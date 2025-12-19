package com.andreibel.server;

import com.andreibel.server.controller.Serve;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BistroServer extends Application {

    public static String PORT = "8080";
    public static String DB_URL = "jdbc:mysql://localhost:3306/bistro";
    public static String DB_USER = "";
    public static String DB_PASSWORD = "";


    @Override
    public void start(Stage stage) throws IOException {


        var params = getParameters().getNamed();
        PORT = params.getOrDefault("port", PORT);
        DB_URL = params.getOrDefault("db_url", DB_URL);
        DB_USER = params.getOrDefault("db_user", "root");
        DB_PASSWORD = params.getOrDefault("db_password", "tikraetzeM4!");


        FXMLLoader fxmlLoader = new FXMLLoader(BistroServer.class.getResource("BistroServerGUI.fxml"));
        Serve sv = new Serve(8080);
        Parent load = fxmlLoader.load();
        sv.setGUIController(fxmlLoader.getController());
        sv.listen();
        Scene scene = new Scene(load, 320, 240);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }
}
