package com.andreibel.client.Main;

import com.andreibel.client.util.BistroUtilities;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point of the Bistro Restaurant JavaFX client application.
 *
 * <p>This class is responsible for:
 * <ul>
 *     <li>Initializing the JavaFX application</li>
 *     <li>Loading the main FXML layout</li>
 *     <li>Creating and connecting the client to the server</li>
 *     <li>Configuring the primary application stage</li>
 * </ul>
 * </p>
 *
 * <p>Connection parameters (host and port) can be passed as
 * named application arguments.</p>
 */
public class ConnectForm extends Application {

    /**
     * Starts the JavaFX application.
     *
     * <p>This method loads the main UI, initializes the client-server
     * connection, and prepares the primary stage.</p>
     *
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws IOException if loading the FXML or connecting to the server fails
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            ConnectForm.class.getResource("ConnectForm.fxml")
        );
        Parent root = fxmlLoader.load();
        ConnectFormGUIController controller = fxmlLoader.getController();
        controller.setMainStage(stage);

        // Create and register scene
        Scene scene = new Scene(root, 1060, 600);
        BistroUtilities.addToSceneManager(scene, "/Main/ConnectForm.fxml");

        // Configure stage
        stage.setTitle("Bistro Restaurant");
        stage
            .getIcons()
            .add(
                new Image(
                    Objects.requireNonNull(
                        getClass().getResourceAsStream("/img/logo.png")
                    )
                )
            );
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }
}
