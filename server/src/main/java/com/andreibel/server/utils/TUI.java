package com.andreibel.server.utils;

import com.andreibel.message.DTO.OrderResponse;
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
        String  str = message.getData() == null ? null : shorten(message.getData().toString(), 105);

        System.out.printf(
                """
       │ ┌────── request: to  --> server ──────┐                                                                                                             │
       │ ├───────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │ %-37s │ %-105s │ │
       │ └───────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
        """, message.getType().toString(),
                str
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
    public static void printPrice(OrderResponse printOrder) {
        double totalPrice = printOrder.getNumberOfGuests() * 100;
        if (printOrder.getSubscriberId() != 0) totalPrice =  totalPrice * 0.9;

        System.out.printf(
                """
       │                                                         ┌─────── Bistro--invoice ───────┐                                                         │
       │ ┌───────────────────────────────────────────────────────┴───────────────────────────────────┴───────────────────────────────────────────────────────┐ │
       │ │                                        ┌────────────────────────────────────────────────────────────────┐                                         │ │
       │ │                                        │          thank you for choosing our bistro restaurant          │                                         │ │
       │ │                                        │             this is invoice for conformation code:             │                                         │ │
       │ │                                        │             ───────────────────────────────────────            │                                         │ │
       │ │                                        │              %-36s              │                                         │ │
       │ │                                        ├────────────────────────────────────────────────────────────────┤                                         │ │
       │ │                                        │             order date: %-39s│                                         │ │
       │ │                                        ├────────────────────────────────────────────────────────────────┤                                         │ │
       │ │                                        │             every meal is 100$  X  %3d guests                  │                                         │ │
       │ │                                        │             subtotal price: $%4d                              │                                         │ │
       │ │                                        │             ───────────────────────────────────────            │                                         │ │
       │ │                                        │             total price: $%4.0f                                 │                                         │ │
       │ │                                        └────────────────────────────────────────────────────────────────┘                                         │ │
       │ └───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
       """, printOrder.getConformationCode().toString(),
                printOrder.getOrderDateTime().toString(),
                printOrder.getNumberOfGuests(),
                printOrder.getNumberOfGuests() * 100,
                totalPrice
        );
    }

    public static void serverOutputLog(Message message) {
        if(message == null) return;
        String ERROR_LINE = buildErrorLine101();
        String messageString;
        if (!message.getType().toString().contains("ERROR"))
            messageString = String.format("%-101s", shorten(message.getData().toString(), 101));
        else
            messageString = ERROR_LINE;
        System.out.printf(
                """
       │ ┌─────── response: to --> client ───────┐                                                                                                         │
       │ ├───────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │ %-41s │ %s │ │
       │ └───────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
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

    private static String buildErrorLine101() {
        final int total = 101;
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

