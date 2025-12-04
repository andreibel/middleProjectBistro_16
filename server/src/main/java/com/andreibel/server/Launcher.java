package com.andreibel.server;

import com.andreibel.server.controller.Serve;
import javafx.application.Application;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
        Application.launch(BistroServer.class, args);
    }
}
