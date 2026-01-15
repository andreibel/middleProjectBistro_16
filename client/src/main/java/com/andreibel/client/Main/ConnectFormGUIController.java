package com.andreibel.client.Main;

import com.andreibel.client.Client.BistroClient;
import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.util.BistroUtilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

public class ConnectFormGUIController {

    @FXML
    private TextField txtHost;

    @FXML
    private TextField txtPort;

    @Setter
    @FXML
    private Stage mainStage;

    @FXML
    private void onConnectClicked(ActionEvent event) throws Exception {
        String host = txtHost.getText().trim();
        String portText = txtPort.getText().trim();

        if (host.isEmpty() || portText.isEmpty()) {
            BistroUtilities.showMessage("Bistro Restaurant", "Please enter a valid Server IP Address and Port.");
            return;
        }
        int port = Integer.parseInt(portText);

        // Read optional launch parameters
//        var params = getParameters().getNamed();
//        String host = params.getOrDefault("host", "localhost");
//        int port = Integer.parseInt(params.getOrDefault("port", "8080"));

        // creating client and log in
        System.out.println("Connecting to " + host + ":" + port);
        BistroClient client = new BistroClient(host, port);
        BistroClientController.getInstance().attachClient(client);
        client.connectToServer();

        mainStage.setOnCloseRequest(windowEvent -> {
            try {
                client.closeConnection();
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to close client connection", e);
            }
        });
        //transition to main form
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Main/MainForm.fxml",
                "Bistro Restaurant"
        );
    }
}
