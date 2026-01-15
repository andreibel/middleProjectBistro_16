package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.client.util.CustomerStateManager;
import com.andreibel.message.DTO.WaitingListResponse;
import com.andreibel.message.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI controller for displaying the current waiting list in the restaurant.
 *
 * <p>This controller allows staff to monitor all customers currently on the waiting list.
 * It displays information including the waiting number, order number, number of guests, subscriber ID,
 * email, and phone number.</p>
 */
public class CurrentWaitingFormGUIController implements IServerResponseListener {

    /** Label displaying the current date for the waiting list. */
    @FXML
    private Label lblTitle;

    @FXML
    private AnchorPane rootPane;

    /** TableView displaying the current waiting list data. */
    @FXML
    private TableView<WaitingListUpdated> tblViewCurrentWaiting;

    /** TableColumn displaying the waiting number of the customer. */
    @FXML
    private TableColumn<WaitingListUpdated, Integer> colWaitingNumber;

    /** TableColumn displaying the number of guests for the waiting order. */
    @FXML
    private TableColumn<WaitingListUpdated, Integer> colNumberOfGuests;

    /** TableColumn displaying the subscriber ID if available. */
    @FXML
    private TableColumn<WaitingListUpdated, String> colSubscriberID;

    /** TableColumn displaying the subscriber's email address. */
    @FXML
    private TableColumn<WaitingListUpdated, String> colEmail;

    /** TableColumn displaying the subscriber's phone number. */
    @FXML
    private TableColumn<WaitingListUpdated, String> colPhoneNumber;

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /** Observable list holding the current waiting list data for the table view. */
    private final ObservableList<WaitingListUpdated> waitingList = FXCollections.observableArrayList();

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>Sets up the table view, initializes columns, sets the title to the current date,
     * and requests the current waiting list when the scene is shown.</p>
     */
    @FXML
    private void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        tblViewCurrentWaiting.setItems(waitingList);
        tblViewCurrentWaiting.setEditable(false);

        initializeTableColumns();
        lblTitle.setText("Current Waiting List for: " + getCurrentDate());
        requestWaitingListWhenSceneIsShown();

    }

    /**
     * Initializes the table columns with the corresponding properties of {@link WaitingListResponse}.
     */
    private void initializeTableColumns() {
        colWaitingNumber.setCellValueFactory(new PropertyValueFactory<>("waitingNumber"));
        colNumberOfGuests.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        colSubscriberID.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    /**
     * Handles server responses related to the current waiting list.
     *
     * @param message the message received from the server
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_WAITING_LIST_RESPONSE ->
                    populateTable((List<WaitingListResponse>) message.getData());
            case GET_WAITING_LIST_ERROR ->
                    BistroUtilities.showMessage(
                            "Bistro Restaurant",
                            "Due to server error, unable to fetch current waiting list."
                    );
        }
    }

    /**
     * Populates the table view with the given waiting list data.
     *
     * @param data a list of {@link WaitingListResponse} objects to display
     */
    private void populateTable(List<WaitingListResponse> data) {
        List<WaitingListUpdated> updated = new ArrayList<>();
        for (WaitingListResponse waitingListResponse : data) {
            updated.add(new WaitingListUpdated(waitingListResponse.getWaitingNumber(),
                    waitingListResponse.getNumberOfGuests(),
                     waitingListResponse.getSubscriberId().toString(),
                    waitingListResponse.getEmail(),
                    waitingListResponse.getPhoneNumber()));
        }
        waitingList.setAll(updated);
    }

    /**
     * Requests the current waiting list from the server when the scene is shown.
     */
    private void requestWaitingListWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        controller.requestCurrentWaitingList();
                    }
                });
            }
        });
    }

    /**
     * Handles clicks on the "Go Back" button.
     *
     * <p>Clears the table and navigates back to the staff main screen.</p>
     *
     * @param event the action event triggered by clicking the button
     * @throws IOException if the FXML cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        waitingList.clear();
        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    /**
     * Returns the current date formatted as yyyy-MM-dd.
     *
     * @return the current date string
     */
    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * Represents an updated entry in the waiting list for the Bistro system.
     *
     * <p>This class stores the waiting number, number of guests, and subscriber information.
     * If subscriber details are not available (null), they can be represented as "N/A" in the UI.</p>
     */
    @Getter
    @Setter
    public static class WaitingListUpdated{
        private Integer waitingNumber;
        private Integer numberOfGuests;
        private String subscriberId;
        private String email;
        private String phoneNumber;
        public WaitingListUpdated(Integer waitingNumber, Integer numberOfGuests, String subscriberId, String email, String phoneNumber) {
            this.waitingNumber = waitingNumber;
            this.numberOfGuests = numberOfGuests;
            this.subscriberId = (subscriberId == null || !subscriberId.equals("0")) ? "N/A" : subscriberId;
            this.email = email != null ? email : "N/A";
            this.phoneNumber = phoneNumber != null ? phoneNumber : "N/A";
        }
    }
}