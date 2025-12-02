package com.andreibel.server;

import com.andreibel.server.controller.Serve;
import javafx.application.Application;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
        Serve sv = new Serve(8080);
        sv.listen();
        System.out.println("Server is listening on port " + sv.getPort());
        Application.launch(HelloApplication.class, args);
    }
}
