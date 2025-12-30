package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.OrderResponse;
import com.andreibel.message.Message;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;


public class EditRestaurantLayoutFormGUIController implements IServerResponseListener {

    @FXML
    private TableView<Table> tblViewRestaurantLayout;
    @FXML
    private TableColumn<Table, Integer> colTableNumber;
    @FXML
    private TableColumn<Table, Integer> colCapacity;
    @FXML
    private TableColumn<Table, Integer> colQuantity;
    @FXML
    private Button btnAddTable;
    @FXML
    private Button btnRemoveTable;
    @FXML
    private Button btnConfirmChanges;
    @FXML
    private Button btnGoBack;


    private ObservableList<Table> tableList;


    private BistroClientController controller;

    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);
        btnAddTable.setDisable(true);
        onSceneShown();

        tblViewRestaurantLayout.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(event -> {
                            onSceneShown();
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onServerResponse(Message message) {
        //Getting the list of tables from the db
        //Setting the observable list to the data received from the db
        tblViewRestaurantLayout.setItems(tableList);
        btnAddTable.setDisable(false);
        btnRemoveTable.setDisable(false);
        btnConfirmChanges.setDisable(false);
    }

    @FXML
    private void onAddTableButtonClicked(ActionEvent event) {
        Table newTable = new Table(0, 0, 0); // default values
        tblViewRestaurantLayout.getItems().add(newTable);
        tblViewRestaurantLayout.getSelectionModel().select(newTable);
        tblViewRestaurantLayout.scrollTo(newTable);
    }

    @FXML
    private void onRemoveTableButtonClicked(ActionEvent event) {
        // Get the selected table
        Table selectedTable = tblViewRestaurantLayout.getSelectionModel().getSelectedItem();

        if (selectedTable != null) {
            // Remove from the observable list
            tableList.remove(selectedTable);

            // Optional: clear selection so no table remains selected
            tblViewRestaurantLayout.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onConfirmChangesButtonClicked(ActionEvent event) {
        //Need a TableRequest and APICallType UPDATE_TABLES
        //List<TableRequest> tablesUpdated = new ArrayList<>(tableList);
        //controller.requestUpdateTables();
    }

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    private void onSceneShown() {
        btnRemoveTable.setDisable(true);
        btnConfirmChanges.setDisable(true);
        tableList = FXCollections.observableArrayList();
        setTableView();
        setupRowSelection();
        //controller.requestTables();
    }

    private void onSelectedTableFromTableView(Table t) {
        btnRemoveTable.setDisable(t == null);
    }

    private void setTableView() {
        colTableNumber.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void setupRowSelection() {
        tblViewRestaurantLayout.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue != null) {
                        onSelectedTableFromTableView(newValue);
                    }
                });
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class Table {
        private SimpleIntegerProperty tableId;
        private SimpleIntegerProperty capacity;
        private SimpleIntegerProperty quantity;

        public Table(int tableId, int capacity, int quantity) {
            this.tableId = new SimpleIntegerProperty(tableId);
            this.capacity = new SimpleIntegerProperty(capacity);
            this.quantity = new SimpleIntegerProperty(quantity);
        }

        // Getters for TableView binding
        public int getTableId() {
            return tableId.get();
        }

        public int getCapacity() {
            return capacity.get();
        }

        public int getQuantity() {
            return quantity.get();
        }

        // Setters for TableView binding
        public void setTableId(int tableId) {
            this.tableId.set(tableId);
        }

        public void setCapacity(int capacity) {
            this.capacity.set(capacity);
        }

        public void setQuantity(int quantity) {
            this.quantity.set(quantity);
        }
    }
}
