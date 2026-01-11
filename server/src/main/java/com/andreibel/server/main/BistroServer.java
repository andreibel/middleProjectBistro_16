package com.andreibel.server.main;

import com.andreibel.server.controller.Serve;
import com.andreibel.server.services.OrderClosingScheduler;
import com.andreibel.server.services.OrderTimeoutScheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.andreibel.server.utils.TUI.*;

public class BistroServer extends Application {

    public static String PORT = "8080";
    public static String DB_URL = "jdbc:mysql://localhost:3306/bistro";
    public static String DB_USER = "root";
    public static String DB_PASSWORD = "root";


    public void startScheduler() {
        OrderTimeoutScheduler scheduler = OrderTimeoutScheduler.getInstance();
        scheduler.start();
        OrderClosingScheduler closingScheduler = OrderClosingScheduler.getInstance();
        closingScheduler.start();
    }

    @Override
    public void start(Stage stage) throws IOException {



        var params = getParameters().getNamed();
        PORT = System.getenv().getOrDefault("PORT",PORT);
        DB_URL = System.getenv().getOrDefault("DB_URL",DB_URL);
        DB_USER = System.getenv().getOrDefault("DB_USER",DB_USER);
        DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", DB_PASSWORD);
        conf(PORT, DB_URL, DB_USER, DB_PASSWORD);
        startLog();
        FXMLLoader fxmlLoader = new FXMLLoader(BistroServer.class.getResource("BistroServerGUI.fxml"));
        Serve sv = new Serve(8080);
        Parent load = fxmlLoader.load();
        startScheduler();
        sv.setGUIController(fxmlLoader.getController());
        sv.listen();

        Scene scene = new Scene(load, 320, 240);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            try {
                sv.close();
                OrderClosingScheduler.getInstance().stop();
                OrderTimeoutScheduler.getInstance().stop();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close client connection", e);
            }
        });
        stage.show();
    }
}
