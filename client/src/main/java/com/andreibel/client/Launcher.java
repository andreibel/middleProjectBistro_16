package com.andreibel.client;

import com.andreibel.client.Main.ConnectForm;
import javafx.application.Application;

import java.util.Arrays;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("Starting client... + args:" + Arrays.toString(args));
        Application.launch(ConnectForm.class, args);
    }
}
