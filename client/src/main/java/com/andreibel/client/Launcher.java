package com.andreibel.client;

import com.andreibel.client.Main.MainForm;
import com.sun.tools.javac.Main;
import javafx.application.Application;

import java.util.Arrays;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("Starting client... + args:" + Arrays.toString(args));
        Application.launch(MainForm.class, args);
    }
}
