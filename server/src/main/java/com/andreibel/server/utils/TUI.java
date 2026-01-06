package com.andreibel.server.utils;

import com.andreibel.message.Message;

public class TUI {
    private static String shorten(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, max - 3) + "...";
    }

    public static void title() {
        System.out.print("""
                                                ┌──────────────────────────────────────────────────────────────────────────────────────────────────────┐
                                                │ ██████╗ ██╗███████╗████████╗██████╗  ██████╗      ███████╗███████╗██████╗ ██╗   ██╗███████╗██████╗   │
                                                │ ██╔══██╗██║██╔════╝╚══██╔══╝██╔══██╗██╔═══██╗     ██╔════╝██╔════╝██╔══██╗██║   ██║██╔════╝██╔══██╗  │
                                                │ ██████╔╝██║███████╗   ██║   ██████╔╝██║   ██║     ███████╗█████╗  ██████╔╝██║   ██║█████╗  ██████╔╝  │
                                                │ ██╔══██╗██║╚════██║   ██║   ██╔══██╗██║   ██║     ╚════██║██╔══╝  ██╔══██╗╚██╗ ██╔╝██╔══╝  ██╔══██╗  │
                                                │ ██████╔╝██║███████║   ██║   ██║  ██║╚██████╔╝     ███████║███████╗██║  ██║ ╚████╔╝ ███████╗██║  ██║  │
                                                │ ╚═════╝ ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝      ╚══════╝╚══════╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝╚═╝  ╚═╝  │
                                                ├──────────────────────────────────────────────────────────────────────────────────────────────────────┤
                        """);
    }

    public static void conf(String PORT, String DB_URL, String DB_USER, String DB_PASSWORD) {
        String maskedPassword = "*".repeat(Math.min(DB_PASSWORD.length(), 8));
        System.out.printf("""
                                        │                                 ─     BISTRO SERVER CONFIGURATION  ─                              │
                                        │                     ┌──────────────────────────────────────────────────────────┐                     │
                                        │                     │    Port        │ %-38s │                     │
                                        │                     │    DB URL      │ %-38s │                     │
                                        │                     │    DB User     │ %-38s │                     │
                                        │                     │  󰟵  DB Password │ %-38s │                     │
                                        │                     └──────────────────────────────────────────────────────────┘                     │
                                        └──────────────────────────────────────────────────────────────────────────────────────────────────────┘
                %n""", PORT, shorten(DB_URL, 38), DB_USER, maskedPassword);
    }

    public static void startLog() {
        System.out.println("┌────────  LOG  " + "──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐");
    }

    public static void serverInputLog(Message message) {
        System.out.printf(
                """
       │ ┌────── request: to  --> server ──────┐                                                                                                             │
       │ ├───────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │ %-37s │ %-105s │ │
       │ └───────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
        """, message.getType().toString(),
                shorten(message.getData().toString(), 105)
        );
    }

    public static void UUID_sent(String message) {
        System.out.printf(
                """
       │ ┌────── sent conformation code  ──────┐                                                                                                             │
       │ ├───────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │ %-145s │ │
       │ └───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
        """, shorten(message, 145)
        );
    }

    public static void serverOutputLog(Message message) {

        String ERROR_LINE = buildErrorLine105();
        String messageString;
        if (!message.getType().toString().contains("ERROR"))
            messageString = String.format("%-105s", shorten(message.getData().toString(), 105));
        else
            messageString = ERROR_LINE;
        System.out.printf(
                """
       │ ┌───── response: to --> client ─────┐                                                                                                             │
       │ ├───────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │ %-37s │ %s │ │
       │ └───────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
        """, message.getType().toString(),
                messageString
        );
    }
    private static final String RESET = "\u001B[0m";
    private static final java.util.regex.Pattern ANSI =
            java.util.regex.Pattern.compile("\u001B\\[[;\\d]*m");

    private static String c(int r, int g, int b, char ch) {
        return "\u001B[38;2;" + r + ";" + g + ";" + b + "m" + ch + RESET;
    }

    private static int visibleLen(String s) {
        return ANSI.matcher(s).replaceAll("").length();
    }

    private static String buildErrorLine105() {
        final int total = 105;
        final String centerPlain = " ERROR ";
        final String center = "\u001B[38;2;255;0;0m" + centerPlain + RESET;

        final int side = (total - centerPlain.length()) / 2; // 49

        char[] blocks = {'█', '▓', '▒', '░'};
        int[][] colors = {
                {255, 60, 60},
                {255, 90, 60},
                {255, 120, 60},
                {255, 150, 60}
        };

        StringBuilder left = new StringBuilder(600);
        for (int i = 0; i < side; i++) {
            int idx = i % blocks.length;
            int[] col = colors[idx];
            left.append(c(col[0], col[1], col[2], blocks[idx]));
        }

        StringBuilder right = new StringBuilder(600);
        for (int i = 0; i < side; i++) {
            int idx = (blocks.length - 1) - (i % blocks.length); // mirror
            int[] col = colors[idx];
            right.append(c(col[0], col[1], col[2], blocks[idx]));
        }

        String result = left + center + right;

        // sanity check: visible length must be exactly 105
        // (optional) System.out.println("visible=" + visibleLen(result));
        return result;
    }
}
