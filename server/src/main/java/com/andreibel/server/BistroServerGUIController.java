package com.andreibel.server;

import com.mysql.cj.xdevapi.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import ocsf.server.ConnectionToClient;

import java.net.InetAddress;

public class BistroServerGUIController {
    @FXML
    private TableView<Object> connectionsTableView;
    @FXML
    private TableColumn<ConnectionToClient, InetAddress> ipAddressColumn;
    @FXML
    private TableColumn<ConnectionToClient, String> statusColumn;

    private ObservableList<ClientConnection> connections;

    @FXML
    private void initialize() {
        connections = FXCollections.observableArrayList();
        setTableView();
    }

    public void addNewConnection(ConnectionToClient client) {
        connections.add(new ClientConnection(client.getInetAddress(), "Open"));
        connectionsTableView.getItems().add(connections.get(connections.size() - 1));
    }

    public void editConnection(ConnectionToClient client) {
        for (ClientConnection connection : connections) {
            if (connection.getIpAddress() == client.getInetAddress()) {
                connection.setIpAddress(client.getInetAddress());
                connectionsTableView.getSelectionModel().select(connection);
                break;
            }
        }
    }

    private void setTableView() {
        ipAddressColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    @Data
    @AllArgsConstructor
    class ClientConnection {
        private InetAddress ipAddress;
        private String status;

    }
}
