package com.andreibel.server.utils;

import com.andreibel.message.Message;
import com.andreibel.server.entity.Order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Text User Interface (TUI) utility class for server console output.
 * <p>
 * Provides formatted ASCII art and logging methods for displaying
 * server status, configuration, messages, and invoices in the console.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public class TUI {

    /**
     * Shortens a string to the specified maximum length, adding ellipsis if truncated.
     *
     * @param value the string to shorten
     * @param max   the maximum allowed length
     * @return the shortened string with "..." suffix if truncated
     */
    private static String shorten(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, max - 3) + "...";
    }

    /**
     * Prints the Bistro Server ASCII art title banner to the console.
     */
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

    /**
     * Prints the server configuration details in a formatted box.
     *
     * @param PORT        the server port number
     * @param DB_URL      the database connection URL
     * @param DB_USER     the database username
     * @param DB_PASSWORD the database password (will be masked in output)
     */
    public static void conf(String PORT, String DB_URL, String DB_USER, String DB_PASSWORD) {
        String maskedPassword = "*".repeat(Math.min(DB_PASSWORD.length(), 8));
        System.out.printf("""
                                        │                                 ─    BISTRO SERVER CONFIGURATION    ─                              │
                                        │                     ┌──────────────────────────────────────────────────────────┐                     │
                                        │                     │  ─  Port        │ %-38s │                     │
                                        │                     │  ─  DB URL      │ %-38s │                     │
                                        │                     │  ─  DB User     │ %-38s │                     │
                                        │                     │  ─  DB Password │ %-38s │                     │
                                        │                     └──────────────────────────────────────────────────────────┘                     │
                                        └──────────────────────────────────────────────────────────────────────────────────────────────────────┘
                %n""", PORT, shorten(DB_URL, 38), DB_USER, maskedPassword);
    }

    /**
     * Prints the log section header to the console.
     */
    public static void startLog() {
        System.out.println("┌────────  LOG  " + "──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐");
    }

    /**
     * Logs an incoming request message from a client to the console.
     *
     * @param message the message received from the client
     */
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

    /**
     * Logs a sent confirmation code to the console.
     *
     * @param message the confirmation code message to display
     */
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
    /**
     * Prints a formatted invoice for an order to the console.
     *
     * @param conformationCode the order confirmation code
     * @param numberOfGuests   the number of guests in the order
     * @param isSub            whether the customer is a subscriber (10% discount applies)
     */
    public static void printPrice(UUID conformationCode, int numberOfGuests, boolean isSub) {
        double totalPrice = numberOfGuests * 100;
        if (isSub) totalPrice =  totalPrice * 0.9;

        System.out.printf(
                """
       │                                                         ┌──────── Bistro--invoice ────────┐                                                         │
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
       """, conformationCode.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                numberOfGuests,
                numberOfGuests * 100,
                totalPrice
        );
    }

    /**
     * Prints a notification when a waiting list customer is seated at a table.
     *
     * @param confirmationCode the customer's confirmation code
     * @param numberOfGuests   the number of guests
     * @param tableSize        the capacity of the assigned table
     */
    public static void printWaitingListSeated(UUID confirmationCode, int numberOfGuests, int tableSize) {
        System.out.printf(
                """
       │ ┌────── WAITING LIST --> TABLE ASSIGNED  ──────┐                                                                                                      │
       │ ├──────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │  Confirmation: %-36s  |  Guests: %3d  |  Table Size: %3d  |  Time: %-19s                             │ │
       │ └───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
       """,
                confirmationCode.toString(),
                numberOfGuests,
                tableSize,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * Prints a notification when an order is cancelled due to late arrival.
     *
     * @param confirmationCode the order confirmation code
     * @param numberOfGuests   the number of guests in the cancelled order
     * @param orderDateTime    the original order date and time
     */
    public static void printOrderCancelled(UUID confirmationCode, int numberOfGuests, LocalDateTime orderDateTime) {
        System.out.printf(
                """
       │ ┌────── ORDER CANCELLED --> LATE ARRIVAL  ──────┐                                                                                                     │
       │ ├───────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │  Confirmation: %-36s  |  Guests: %3d  |  Order Time: %-19s  |  Cancelled: %-19s        │ │
       │ └───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
       """,
                confirmationCode.toString(),
                numberOfGuests,
                orderDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * Prints a notification when an order is cancelled due to late arrival.
     *
     * @param order dated order
     */
    public static void deletedOrders(Order order) {
        System.out.printf(
                """
       │ ┌───── ORDER DELETED CHANGELING OPEN TIME  ─────┐                                                                                                     │
       │ ├───────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │  Confirmation: %-36s  |  Guests: %3d  |  Order Time: %-19s  |  Deleted: %-19s          │ │
       │ └───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
       """,
                order.getConformationCode().toString(),
                order.getNumberOfGuests(),
                order.getOrderDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * Logs an outgoing response message to a client to the console.
     *
     * @param message the response message being sent to the client
     */
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
       │ ┌──────── response: to --> client ────────┐                                                                                                         │
       │ ├───────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
       │ │ %-41s │ %s │ │
       │ └───────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
       """, message.getType().toString(),
                messageString
        );
    }
    private static final String RESET = "\u001B[0m";

    /**
     * Generates an ANSI-colored character string.
     *
     * @param r  red color component (0-255)
     * @param g  green color component (0-255)
     * @param b  blue color component (0-255)
     * @param ch the character to colorize
     * @return the ANSI escape sequence for the colored character
     */
    private static String c(int r, int g, int b, char ch) {
        return "\u001B[38;2;" + r + ";" + g + ";" + b + "m" + ch + RESET;
    }

    /**
     * Builds a 101-character wide error line with gradient-colored block characters.
     *
     * @return the formatted error line string with ANSI color codes
     */
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
        return result;
    }

}

