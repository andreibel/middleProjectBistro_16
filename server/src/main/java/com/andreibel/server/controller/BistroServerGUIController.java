package com.andreibel.server.controller;

import com.lloseng.ocsf.server.ConnectionToClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.InetAddress;

/**
 * JavaFX controller for the Bistro server GUI.
 *
 * <p>This controller manages a {@link TableView} that displays currently known client connections
 * (typically OCSF {@link ConnectionToClient} instances). Each row is represented by a
 * {@link ClientConnection} model containing the client's id, IP address and a human-readable status.</p>
 *
 * <p><b>Lifecycle:</b> the {@link #initialize()} method is invoked automatically by the JavaFX
 * {@code FXMLLoader} after the FXML fields are injected. It initializes the observable list,
 * binds it to the table view, and configures the table columns.</p>
 *
 * <p><b>Threading note:</b> JavaFX UI updates must run on the JavaFX Application Thread. If the server
 * notifies connection events from a background thread, wrap calls to {@link #addNewConnection(ConnectionToClient)}
 * and {@link #editConnection(ConnectionToClient)} using {@code Platform.runLater(...)}.</p>
 */
public class BistroServerGUIController {

    /** Table that displays all client connections shown in the GUI. */
    @FXML
    private TableView<ClientConnection> connectionsTableView;

    /** Column that shows the client's {@link InetAddress}. */
    @FXML
    private TableColumn<ClientConnection, InetAddress> ipAddressColumn;

    /** Column that shows the connection status (e.g., "Open", "closed"). */
    @FXML
    private TableColumn<ClientConnection, String> statusColumn;

    /**
     * Backing list for {@link #connectionsTableView}. Modifying this list updates the table view.
     */
    private ObservableList<ClientConnection> connections;

    /**
     * Initializes the controller after FXML loading and field injection.
     *
     * <p>Creates the observable list that backs the table, sets it on the table view,
     * and configures the cell value factories for the columns.</p>
     */
    @FXML
    private void initialize() {
        connections = FXCollections.observableArrayList();
        connectionsTableView.setItems(connections);
        setTableView();
    }

    /**
     * Adds a new connected client row to the table.
     *
     * <p>The created row uses the client's {@code id} and {@code InetAddress}. The default status
     * is set to {@code "Open"}.</p>
     *
     * @param client the connected OCSF client
     */
    public void addNewConnection(ConnectionToClient client) {
        connections.add(new ClientConnection(client.getId(), client.getInetAddress(), "Open"));
    }

    /**
     * Updates an existing connection row to mark it as closed.
     *
     * <p>This method finds the row by client id and sets its status to {@code "closed"}.
     * After updating, the table view is refreshed to ensure the UI reflects the change.</p>
     *
     * @param client the client whose connection status should be updated
     */
    public void editConnection(ConnectionToClient client) {
        for (ClientConnection connection : connections) {
            if (connection.getId() == client.getId()) {
                connection.setStatus("closed");
                connectionsTableView.refresh();
                break;
            }
        }
    }

    /**
     * Configures the table columns to read values from {@link ClientConnection}.
     *
     * <p>Uses {@link PropertyValueFactory} with the property names:
     * {@code ipAddress} and {@code status}. This requires {@link ClientConnection}
     * to expose corresponding getters (e.g., {@code getIpAddress()}, {@code getStatus()}).</p>
     */
    private void setTableView() {
        ipAddressColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
}