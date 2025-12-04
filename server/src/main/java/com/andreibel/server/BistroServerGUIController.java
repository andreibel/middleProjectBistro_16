package com.andreibel.server;

import com.andreibel.server.controller.ClientConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ocsf.server.ConnectionToClient;

import java.net.InetAddress;

public class BistroServerGUIController {
    @FXML
    private TableView<ClientConnection> connectionsTableView;
    @FXML
    private TableColumn<ClientConnection, InetAddress> ipAddressColumn;
    @FXML
    private TableColumn<ClientConnection, String> statusColumn;

    private ObservableList<ClientConnection> connections;

    @FXML
    private void initialize() {
        connections = FXCollections.observableArrayList();
        connectionsTableView.setItems(connections);
        setTableView();
    }

    public void addNewConnection(ConnectionToClient client) {
        connections.add(new ClientConnection(client.getId(), client.getInetAddress(), "Open"));
    }

    public void editConnection(ConnectionToClient client) {
        for (ClientConnection connection : connections) {
            if (connection.getId() == client.getId()) {
                connection.setStatus("closed");
                connectionsTableView.refresh();
                break;
            }
        }
    }

    private void setTableView() {
        ipAddressColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }


}
