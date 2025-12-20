package com.andreibel.client.Main;

import com.andreibel.client.Client.BistroClientController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFormGUIController {

    @FXML
    private Label lblMainTitle;
    @FXML
    private Label lblHoverDetails;
    @FXML
    private Button btnOrder;
    @FXML
    private Button btnArrived;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSubscriber;
    @FXML
    private Button btnWorker;
    private BistroClientController controller;

    @FXML
    public void initialize() {
    }

    public void setController(BistroClientController controller) {
        this.controller = controller;
    }
    @FXML
    public void onOrderButtonClicked(ActionEvent event) throws IOException {
        // Load the new FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Order/OrderForm.fxml"));
        Parent root = (Parent) loader.load();

        // Get current stage (window) from the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set new scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("OrderFromGUIController");
        stage.show();

    }

    public void onArrivedButtonClicked(ActionEvent event) throws IOException{
        // Load the new FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Table/GetTableForm.fxml"));
        Parent root = (Parent) loader.load();
        // Get current stage (window) from the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set new scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("GetTableForm");
        stage.show();

    }
    @FXML
    public void onCancelButtonClicked(ActionEvent event) throws IOException{
        // Load the new FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Order/CancelOrderForm.fxml"));
        Parent root = (Parent) loader.load();

        // Get current stage (window) from the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set new scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("CancelOrderForm");
        stage.show();

    }
    public void onSubscriberButtonClicked(ActionEvent event) throws IOException{
        // Load the new FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Subscriber/SubscriberLoginForm.fxml"));
        Parent root = (Parent) loader.load();

        // Get current stage (window) from the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set new scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("SubscriberLoginForm");
        stage.show();

    }
    public void onWorkerButtonClicked(ActionEvent event) throws IOException{
        // Load the new FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Worker/WorkerLoginForm.fxml"));
        Parent root = (Parent) loader.load();

        // Get current stage (window) from the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set new scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("WorkerLoginForm");
        stage.show();

    }

}


/* Pattern to switch between forms
// Inside your controller or wherever you handle the button click
@FXML
private void goToNextForm(ActionEvent event) throws IOException {
    // Load the new FXML
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main/NextForm.fxml"));
    Parent root = loader.load();

    // Get current stage (window) from the event source
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    // Set new scene
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Next Form");
    stage.show();
}
 */