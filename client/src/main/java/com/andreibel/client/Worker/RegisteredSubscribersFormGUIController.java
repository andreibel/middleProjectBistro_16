package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.SubscriberResponse;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

/**
 * GUI controller for displaying all registered subscribers in the restaurant system.
 *
 * <p>This controller allows staff to view subscriber information including ID, name, email, and phone number.</p>
 */
public class RegisteredSubscribersFormGUIController implements IServerResponseListener {

    /** TableView showing all registered subscribers. */
    @FXML
    private TableView<SubscriberResponse> tblSubscribers;

    /** Column displaying subscriber ID. */
    @FXML
    private TableColumn<SubscriberResponse, Integer> colSubscriberId;

    /** Column displaying subscriber name. */
    @FXML
    private TableColumn<SubscriberResponse, String> colName;

    /** Column displaying subscriber email. */
    @FXML
    private TableColumn<SubscriberResponse, String> colEmail;

    /** Column displaying subscriber phone number. */
    @FXML
    private TableColumn<SubscriberResponse, String> colPhone;

    /** Root pane of the form. */
    @FXML
    private AnchorPane rootPane;

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /** Observable list holding the subscriber data for the TableView. */
    private final ObservableList<SubscriberResponse> subscribersList = FXCollections.observableArrayList();

    /**
     * Initializes the controller after the FXML is loaded.
     *
     * <p>Sets up the TableView, binds it to the observable list, and requests subscriber data
     * from the server when the scene is shown.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        tblSubscribers.setItems(subscribersList);
        tblSubscribers.setEditable(false);

        initializeTableColumns();
        requestSubscribersWhenSceneIsShown();
    }

    /**
     * Initializes TableView columns with proper property mappings.
     */
    private void initializeTableColumns() {
        colSubscriberId.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    /**
     * Handles responses received from the server.
     *
     * @param message the server message
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_ALL_SUBSCRIBERS_RESPONSE -> {
                List<SubscriberResponse> subscribers = (List<SubscriberResponse>) message.getData();
                if (subscribers != null) populateTable(subscribers);
            }
            case GET_ALL_SUBSCRIBERS_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, unable to fetch all subscribers.");
        }
    }

    /**
     * Populates the TableView with subscriber data.
     *
     * @param subscribers list of subscribers
     */
    private void populateTable(List<SubscriberResponse> subscribers) {
        subscribersList.setAll(subscribers);
    }

    /**
     * Requests all subscriber information from the server when the scene is shown.
     */
    private void requestSubscribersWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        controller.requestAllSubscribersInfo();
                    }
                });
            }
        });
    }

    /**
     * Handles clicks on the "Go Back" button.
     *
     * <p>Clears the subscriber list and navigates back to the staff main screen.</p>
     *
     * @param event the action event
     * @throws IOException if FXML cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        subscribersList.clear();

        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }
}