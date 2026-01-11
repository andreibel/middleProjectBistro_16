package com.andreibel.server;

import javafx.application.Application;

import java.io.IOException;

import static com.andreibel.server.utils.TUI.title;

public class Launcher {
    public static void main(String[] args) throws IOException {
        title();
        Application.launch(BistroServer.class, args);
    }
}
