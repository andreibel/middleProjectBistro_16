package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.OrderResponse;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GUI controller for the "Active Orders" screen.
 *
 * <p>This controller displays all currently active orders for the staff to view.
 * It shows order details such as subscriber ID, number of guests, contact information,
 * and the order date/time. It also allows navigation back to the staff main menu.</p>
 */
public class ActiveOrdersFormGUIController implements IServerResponseListener {

    /** Label displaying the screen title and current date. */
    @FXML
    private Label lblTitle;

    /** Table view showing all active orders. */
    @FXML
    private TableView<OrderResponse> tblActiveOrders;

    /** Table column displaying the number of guests for each order. */
    @FXML
    private TableColumn<OrderResponse, Integer> colNumberOfGuests;

    /** Table column displaying the subscriber ID for each order. */
    @FXML
    private TableColumn<OrderResponse, Integer> colSubscriberId;

    /** Table column displaying the email address for each order. */
    @FXML
    private TableColumn<OrderResponse, String> colEmail;

    /** Table column displaying the phone number for each order. */
    @FXML
    private TableColumn<OrderResponse, String> colPhoneNumber;

    /** Table column displaying the order date and time. */
    @FXML
    private TableColumn<OrderResponse, String> colOrderDateTime;

    /** Root pane of the screen used to attach scene listeners. */
    @FXML
    private AnchorPane rootPane;

    /** Observable list storing the active orders displayed in the table. */
    private ObservableList<OrderResponse> activeList;

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>Sets up the table columns, initializes the active orders list,
     * displays the current date in the title, and requests active orders when the scene is shown.</p>
     */
    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        lblTitle.setText("Active Orders for " + getCurrentDate());

        activeList = FXCollections.observableArrayList();
        tblActiveOrders.setItems(activeList);
        tblActiveOrders.setEditable(false);

        initializeTableColumns();
        requestActiveOrdersWhenSceneIsShown();
    }

    /**
     * Handles server responses for active orders.
     *
     * <p>Populates the table with active orders on success or shows an error message
     * if the request fails.</p>
     *
     * @param message the message received from the server
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_ALL_ACTIVE_RESPONSE -> populateTable((List<OrderResponse>) message.getData());
            case GET_ALL_ACTIVE_ERROR -> BistroUtilities.showMessage(
                    "Bistro Restaurant",
                    "Due to server error, we're unable to get the active orders for " + getCurrentDate()
            );
        }
    }

    /**
     * Populates the table with the list of active orders.
     *
     * @param data the list of active orders
     */
    private void populateTable(List<OrderResponse> data) {
        if (data == null) return;
        activeList.setAll(data);
    }

    /**
     * Handles clicks on the "Go Back" button.
     *
     * <p>Clears the active orders list and navigates back to the staff main menu.</p>
     *
     * @param event the action event triggered by clicking the button
     * @throws IOException if the FXML cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        activeList.clear();

        BistroUtilities.switchScreen(
                (Node) event.getSource(),
                "/Worker/WorkerForm.fxml",
                "Bistro Restaurant - Staff Area"
        );
    }

    /**
     * Initializes the table columns with the correct property values.
     */
    private void initializeTableColumns() {
        colNumberOfGuests.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        colSubscriberId.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colOrderDateTime.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
    }

    /**
     * Requests active orders from the server when the scene is shown.
     *
     * <p>This method attaches a listener to the root pane to trigger the server request
     * once the window is fully loaded.</p>
     */
    private void requestActiveOrdersWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        controller.requestActiveOrders();
                    }
                });
            }
        });
    }

    /**
     * Returns the current date formatted as yyyy-MM-dd.
     *
     * @return the current date string
     */
    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}