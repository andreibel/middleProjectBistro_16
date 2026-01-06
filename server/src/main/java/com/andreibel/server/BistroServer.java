package com.andreibel.server;

import com.andreibel.message.DTO.WorkerAuth;
import com.andreibel.message.Message;
import com.andreibel.server.controller.Serve;
import com.andreibel.server.controller.WorkerController;
import com.andreibel.server.services.OrderClosingScheduler;
import com.andreibel.server.services.OrderTimeoutScheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.andreibel.message.APICallType.WORKER_LOGIN;
import static com.andreibel.server.utils.TUI.*;

public class BistroServer extends Application {

    public static String PORT = "8080";
    public static String DB_URL = "jdbc:mysql://localhost:3306/bistro";
    public static String DB_USER = "";
    public static String DB_PASSWORD = "";


    public void startScheduler() {
        OrderTimeoutScheduler scheduler = OrderTimeoutScheduler.getInstance();
        scheduler.start();
        OrderClosingScheduler closingScheduler = OrderClosingScheduler.getInstance();
        closingScheduler.start();
    }

    @Override
    public void start(Stage stage) throws IOException {


        var params = getParameters().getNamed();
        PORT = params.getOrDefault("port", PORT);
        DB_URL = params.getOrDefault("db_url", DB_URL);
        DB_USER = params.getOrDefault("db_user", "root");
        DB_PASSWORD = params.getOrDefault("db_password", "tikraetzeM4!");
        conf(PORT, DB_URL, DB_USER, DB_PASSWORD);
        startLog();
        Message msg  = new Message(WORKER_LOGIN, new WorkerAuth("andrei", "Andrei1234567890"));
        serverOutputLog(msg);
        Message res = WorkerController.getInstance().login(msg);
        serverOutputLog(res);
        FXMLLoader fxmlLoader = new FXMLLoader(BistroServer.class.getResource("BistroServerGUI.fxml"));
        Serve sv = new Serve(8080);
        Parent load = fxmlLoader.load();
        startScheduler();
        sv.setGUIController(fxmlLoader.getController());
        sv.listen();

        Scene scene = new Scene(load, 320, 240);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }
}
