package com.andreibel.client.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class BistroUtilities {
    private static final Map<String, Scene> sceneManager = new HashMap<>();

    // Private constructor to prevent instantiation
    private BistroUtilities() {
    }


    /**
     * Switches the current window to a different screen by loading (or reusing)
     * an FXML-based scene.
     *
     * <p>This method uses a scene cache to avoid reloading FXML files and recreating
     * scenes multiple times. Each scene is stored using the base FXML file name
     * (without extension) as a unique key.</p>
     *
     * <p>If the requested scene already exists in the cache, it is reused.
     * Otherwise, the FXML file is loaded, a new {@link Scene} is created,
     * and stored for future use.</p>
     *
     * <p>The screen switch is performed on the current {@link Stage} obtained
     * from the provided source node.</p>
     *
     * @param source   a JavaFX node that belongs to the current scene
     *                 (typically {@code event.getSource()})
     * @param fxmlPath the relative path to the FXML file
     *                 (e.g. {@code "/Order/OrderForm.fxml"})
     * @param title    the title to set on the application window
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void switchScreen(Node source, String fxmlPath, String title) throws IOException {
        String sceneKey = getFXMLName(fxmlPath); // use base FXML name as key
        Scene scene;

        // If scene already exists in cache, reuse it
        if (sceneManager.containsKey(sceneKey)) {
            scene = sceneManager.get(sceneKey);
        } else {
            // Load FXML and create a new scene
            FXMLLoader loader = new FXMLLoader(
                    BistroUtilities.class.getResource("/com/andreibel/client" + fxmlPath)
            );
            Parent root = loader.load();
            scene = new Scene(root);

            // Save scene in cache
            sceneManager.put(sceneKey, scene);
        }

        // Get the current stage from the source node
        Stage stage = (Stage) source.getScene().getWindow();

        // Set the scene and title
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    /**
     * Adds a {@link Scene} to the scene cache using the base FXML file name as the key.
     *
     * <p>This method allows scenes to be manually registered in the scene manager,
     * enabling reuse of already created scenes without reloading their associated
     * FXML files.</p>
     *
     * <p>The scene is stored using the FXML file name (without the ".fxml" extension)
     * extracted from the provided path.</p>
     *
     * @param scene    the {@link Scene} instance to store in the scene cache
     * @param fxmlPath the relative path to the FXML file associated with the scene
     *                 (e.g. {@code "/Order/OrderForm.fxml"})
     */
    public static void addToSceneManager(Scene scene, String fxmlPath) {
        sceneManager.put(getFXMLName(fxmlPath), scene);
    }

    // Regex pattern for a valid phone number
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(?:\\+?\\d{1,3}?[- .]?\\(?\\d{1,4}?\\)?[- .]?\\d{1,4}[- .]?\\d{1,9})?$"
    );

    /**
     * Checks if the given string is a valid phone number.
     * @param phone the string to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Z][a-z]+( [A-Z][a-z]+)*$"
    );

    /**
     * Checks if the given string is a valid full name.
     * Example: "John Doe", "Alice Mary Smith"
     * @param name the string to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidFullName(String name) {
        if (name == null || name.isEmpty()) return false;
        return NAME_PATTERN.matcher(name).matches();
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?:[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+)?$\n"
    );

    /**
     * Checks if the given string is a valid email.
     * Example: "example@gmail.com"
     * @param email the string to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Regex for digits only
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("^\\d+$");

    /**
     * Checks if the given string contains only numeric digits (0-9).
     * Does NOT allow decimal points, negative signs, or spaces.
     *
     * @param str the string to check
     * @return true if the string contains only digits, false otherwise
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        return DIGITS_ONLY_PATTERN.matcher(str).matches();
    }
    //Regex for password validation
    private static final Pattern VALID_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    /**
     * Validates whether a given password meets the application's password requirements.
     *
     * <p>A valid password must satisfy <b>all</b> of the following rules:</p>
     * <ul>
     *     <li>Contains at least <b>8 characters</b></li>
     *     <li>Contains at least <b>one uppercase letter</b> (A–Z)</li>
     *     <li>Contains at least <b>one lowercase letter</b> (a–z)</li>
     *     <li>Contains at least <b>one digit</b> (0–9)</li>
     *     <li><b>No special characters</b> are required</li>
     *     <li><b>No spaces</b> are allowed</li>
     * </ul>
     *
     * <p>If the password is {@code null}, empty, or does not match the validation
     * pattern, this method returns {@code false}.</p>
     *
     * @param password the password string to validate
     * @return {@code true} if the password meets all the requirements;
     *         {@code false} otherwise
     */

    public static boolean isPasswordValid(String password) {
        if (password == null || password.isEmpty()) return false;
        return VALID_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Displays an information alert dialog with the specified title and message.
     * <p>
     * This method creates a JavaFX {@link Alert} of type {@link Alert.AlertType#INFORMATION},
     * sets its title and content text, and then shows it modally using {@link Alert#showAndWait()}.
     * </p>
     *
     * @param title   the title of the alert dialog window
     * @param message the message to display in the alert content
     */
    public static void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Extracts the base name of an FXML file from a given path,
     * without the ".fxml" extension.
     * <p>
     * This method handles paths with forward slashes ("/") and
     * ignores the case of the ".fxml" extension. If the path does
     * not contain a file name or extension, it returns the last
     * segment of the path as-is.
     * </p>
     *
     * <p>Examples:</p>
     * <pre>
     * getFXMLName("/Order/OrderForm.fxml")  // returns "OrderForm"
     * getFXMLName("MainForm.fxml")          // returns "MainForm"
     * getFXMLName("/A/B/C.fxml")            // returns "C"
     * getFXMLName("/Test/NoExtension")      // returns "NoExtension"
     * </pre>
     *
     * @param path the full path to the FXML file
     * @return the base name of the FXML file without the ".fxml" extension,
     *         or an empty string if the path is null or empty
     */
    private static String getFXMLName(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        // Get file name after the last slash
        int lastSlashIndex = path.lastIndexOf('/');
        String fileName = lastSlashIndex >= 0 ? path.substring(lastSlashIndex + 1) : path;

        // Remove the ".fxml" extension if present
        if (fileName.toLowerCase().endsWith(".fxml")) {
            fileName = fileName.substring(0, fileName.length() - 5);
        }

        return fileName;
    }
}
