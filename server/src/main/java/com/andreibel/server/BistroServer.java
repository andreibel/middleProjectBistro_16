package com.andreibel.server;

import com.andreibel.server.controller.Serve;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BistroServer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BistroServer.class.getResource("BistroServerGUI.fxml"));
        Serve sv = new Serve(8080);
        sv.setGUIController(fxmlLoader.getController());
        sv.listen();
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }
}
