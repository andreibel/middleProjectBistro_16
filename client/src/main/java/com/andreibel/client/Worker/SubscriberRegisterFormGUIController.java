package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.SubscriberRequest;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * GUI controller for registering new subscribers in the Bistro system.
 *
 * <p>This form allows staff to input subscriber details (name, email, phone number),
 * validate them, and submit a request to the server. Feedback messages are displayed
 * upon success or failure.</p>
 */
public class SubscriberRegisterFormGUIController implements IServerResponseListener {

    /** TextField for entering subscriber name. */
    @FXML
    private TextField txtFieldName;

    /** TextField for entering subscriber email. */
    @FXML
    private TextField txtFieldEmail;

    /** TextField for entering subscriber phone number. */
    @FXML
    private TextField txtFieldPhoneNumber;

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /**
     * Initializes the controller after FXML is loaded.
     *
     * <p>Registers this controller as a server listener and clears the input form.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        clearForm();
    }

    /**
     * Handles responses from the server related to subscriber creation.
     *
     * @param message the server message
     */
    @Override
    public void onServerResponse(Message message) throws Exception{
        switch (message.getType()) {
            case CREATE_SUBSCRIBER_RESPONSE -> {
                clearForm();
                showQrAlert(((SubscriberResponse)message.getData()).getSubscriberId(), generateQrCode(((SubscriberResponse)message.getData()).getSubscriberId()));
            }
            case CREATE_SUBSCRIBER_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to register new subscriber.");
        }
    }

    /**
     * Handles the Register button click.
     *
     * <p>Validates the input fields and sends a registration request to the server
     * if all inputs are valid.</p>
     *
     * @param event the action event
     */
    @FXML
    private void onRegisterButtonClicked(ActionEvent event) {
        String name = txtFieldName.getText();
        String email = txtFieldEmail.getText();
        String phone = txtFieldPhoneNumber.getText();

        if (name.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter subscriber name.");
            return;
        }
        if (email.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter subscriber email.");
            return;
        }
        if (phone.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter phone number.");
            return;
        }

        if (!BistroUtilities.isValidFullName(name)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid subscriber name.");
            return;
        }
        if (!BistroUtilities.isValidEmail(email)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid subscriber email.");
            return;
        }
        if (!BistroUtilities.isValidPhoneNumber(phone)) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid phone number.");
            return;
        }

        controller.requestRegisterNewSubscriber(new SubscriberRequest(null, email, name, phone));
    }
    /**
     * Generates a QR code image file for the given subscriber ID.
     *
     * <p>The QR code image is saved in a "qrcodes" folder inside the current working directory.
     * The image is a PNG file named "subscriber_<ID>.png".</p>
     *
     * @param subscriberId the ID of the subscriber
     * @return the generated QR code image file
     * @throws Exception if an error occurs during QR code generation or file writing
     */
    private File generateQrCode(Integer subscriberId) throws Exception {
        String qrText = subscriberId.toString();

        int size = 250;
        var bitMatrix = new com.google.zxing.qrcode.QRCodeWriter()
                .encode(qrText, com.google.zxing.BarcodeFormat.QR_CODE, size, size);

        Path outputDir = Paths.get(System.getProperty("user.dir"), "qrcodes");
        Files.createDirectories(outputDir);

        File qrFile = outputDir.resolve("subscriber_" + subscriberId + ".png").toFile();
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrFile.toPath());

        return qrFile;
    }
    /**
     * Displays an informational alert showing the subscriber's QR code.
     *
     * <p>The alert includes the subscriber ID as content and the QR code image as a graphic.
     * The QR code image is resized to fit 250x250 pixels while preserving its aspect ratio.</p>
     *
     * @param subscriberId the ID of the registered subscriber
     * @param qrFile       the QR code image file to display
     */
    private void showQrAlert(Integer subscriberId, File qrFile) {
        ImageView imageView = new ImageView(new Image(qrFile.toURI().toString()));
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bistro Restaurant");
        alert.setHeaderText("Subscriber registered successfully!\n" +
                "Your QR code is: ");
        alert.setContentText("Subscriber ID: " + subscriberId);
        alert.getDialogPane().setGraphic(imageView);

        alert.showAndWait();
    }

    /**
     * Handles the Go Back button click.
     *
     * <p>Clears the form and navigates back to the staff main screen.</p>
     *
     * @param event the action event
     * @throws IOException if FXML cannot be loaded
     */
    @FXML
    private void onButtonGoBackClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearForm() {
        txtFieldName.clear();
        txtFieldEmail.clear();
        txtFieldPhoneNumber.clear();
    }
}