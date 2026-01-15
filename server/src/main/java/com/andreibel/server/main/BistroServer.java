package com.andreibel.server.main;

import com.andreibel.server.controller.Serve;
import com.andreibel.server.services.OrderClosingScheduler;
import com.andreibel.server.services.OrderTimeoutScheduler;
import com.andreibel.server.services.WaitingListArriveScheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.andreibel.server.utils.TUI.*;

public class BistroServer extends Application {

    public static String PORT = "8080";
    public static String DB_URL = "jdbc:mysql://db-bistro-g16.cbe862egq27l.eu-north-1.rds.amazonaws.com:3306";
    public static String DB_USER = "admin";
    public static String DB_PASSWORD = "TOKEN";


    public void startScheduler() {
        OrderTimeoutScheduler scheduler = OrderTimeoutScheduler.getInstance();
        scheduler.start();
        OrderClosingScheduler closingScheduler = OrderClosingScheduler.getInstance();
        closingScheduler.start();
        WaitingListArriveScheduler waitingListArriveScheduler = WaitingListArriveScheduler.getInstance();
        waitingListArriveScheduler.start();
    }

    @Override
    public void start(Stage stage) throws IOException {
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
                WaitingListArriveScheduler.getInstance().stop();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close client connection", e);
            }
        });
        stage.show();
    }
}
