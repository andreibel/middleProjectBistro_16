package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.TableRequest;
import com.andreibel.message.DTO.TableResponse;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.IntegerStringConverter;
import javafx.beans.property.IntegerProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI controller for editing the restaurant's table layout.
 *
 * <p>This controller allows staff to view, add, remove, and edit tables in the restaurant.
 * Each table has a capacity and quantity. Changes can be applied to the server after validation.</p>
 */
public class EditRestaurantLayoutFormGUIController implements IServerResponseListener {

    /** TableView displaying all restaurant tables. */
    @FXML
    private TableView<Table> tblViewRestaurantLayout;

    /** Column displaying table capacity. */
    @FXML
    private TableColumn<Table, Integer> colCapacity;

    /** Column displaying table quantity. */
    @FXML
    private TableColumn<Table, Integer> colQuantity;

    /** Button for adding a new table to the layout. */
    @FXML
    private Button btnAddTable;

    /** Button for removing a selected table from the layout. */
    @FXML
    private Button btnRemoveTable;

    /** Button for confirming all layout changes to the server. */
    @FXML
    private Button btnConfirmChanges;

    /** Root anchor pane of the form. */
    @FXML
    private AnchorPane rootPane;

    /** Observable list holding the table data for the TableView. */
    private ObservableList<Table> tableList = FXCollections.observableArrayList();

    /** Singleton instance of the Bistro client controller for server communication. */
    private BistroClientController controller;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>Disables buttons initially, sets up the table view, enables editing,
     * sets row selection behavior, and requests table data from the server when the scene is shown.</p>
     */
    @FXML
    public void initialize() {
        controller = BistroClientController.getInstance();
        controller.addListener(this);

        btnAddTable.setDisable(true);
        btnRemoveTable.setDisable(true);
        btnConfirmChanges.setDisable(true);

        setTableView();
        setupRowSelection();
        tblViewRestaurantLayout.setItems(tableList);
        enableEditing();
        requestTablesWhenSceneIsShown();
    }

    /**
     * Handles server responses related to restaurant tables.
     *
     * @param message the message received from the server
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onServerResponse(Message message) {
        switch (message.getType()) {
            case GET_ALL_TABLES_RESPONSE -> {
                List<TableResponse> tables = (List<TableResponse>) message.getData();
                tableList.clear();
                // Convert TableResponse -> Table for editable TableView
                for (TableResponse t : tables) {
                    tableList.add(new Table(t.getCapacity(), t.getQuantity()));
                }
                btnAddTable.setDisable(false);
                btnRemoveTable.setDisable(true);
                btnConfirmChanges.setDisable(tableList.isEmpty());
            }
            case GET_ALL_TABLES_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to retrieve the tables.");
            case EDIT_BISTRO_LAYOUT_RESPONSE ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Successfully updated table layout.");
            case EDIT_BISTRO_LAYOUT_ERROR ->
                    BistroUtilities.showMessage("Bistro Restaurant", "Due to server error, it was unable to update table layout.");
        }
    }

    /**
     * Handles clicks on the "Add Table" button.
     *
     * <p>Adds a new table with default values (0 capacity, 0 quantity) and selects it in the table view.</p>
     *
     * @param event the action event
     */
    @FXML
    private void onAddTableButtonClicked(ActionEvent event) {
        Table newTable = new Table(0, 0);
        tableList.add(newTable);
        tblViewRestaurantLayout.getSelectionModel().select(newTable);
        tblViewRestaurantLayout.scrollTo(newTable);
        btnConfirmChanges.setDisable(false);
    }

    /**
     * Handles clicks on the "Remove Table" button.
     *
     * <p>Removes the selected table from the table view and updates button states.</p>
     *
     * @param event the action event
     */
    @FXML
    private void onRemoveTableButtonClicked(ActionEvent event) {
        Table selectedTable = tblViewRestaurantLayout.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            tableList.remove(selectedTable);
            tblViewRestaurantLayout.getSelectionModel().clearSelection();
        }
        btnRemoveTable.setDisable(tblViewRestaurantLayout.getSelectionModel().getSelectedItem() == null);
        btnConfirmChanges.setDisable(tableList.isEmpty());
    }

    /**
     * Handles clicks on the "Confirm Changes" button.
     *
     * <p>Validates all tables and sends the updated layout to the server.</p>
     *
     * @param event the action event
     */
    @FXML
    private void onConfirmChangesButtonClicked(ActionEvent event) {
        if (!isAllTablesValid()) {
            BistroUtilities.showMessage("Bistro Restaurant", "All tables must have capacity and quantity greater than 0.");
            return;
        }

        List<TableRequest> reqs = new ArrayList<>();
        for (Table table : tableList) {
            reqs.add(new TableRequest(table.getCapacity(), table.getQuantity()));
        }
        controller.requestApplyLayoutChanges(reqs);
    }

    /**
     * Handles clicks on the "Go Back" button.
     *
     * <p>Clears the form and navigates back to the staff main screen.</p>
     *
     * @param event the action event
     * @throws IOException if the FXML cannot be loaded
     */
    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    /**
     * Updates button states when a table is selected in the TableView.
     *
     * @param t the selected table
     */
    private void onSelectedTableFromTableView(Table t) {
        btnRemoveTable.setDisable(t == null);
        btnConfirmChanges.setDisable(tableList.isEmpty());
    }

    /**
     * Sets up the TableView with cell value factories.
     */
    private void setTableView() {
        colCapacity.setCellValueFactory(cell -> cell.getValue().capacityProperty().asObject());
        colQuantity.setCellValueFactory(cell -> cell.getValue().quantityProperty().asObject());
    }

    /**
     * Enables editing for table capacity and quantity columns.
     */
    private void enableEditing() {
        tblViewRestaurantLayout.setEditable(true);

        colCapacity.setEditable(true);
        colQuantity.setEditable(true);

        colCapacity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colCapacity.setOnEditCommit(event -> event.getRowValue().setCapacity(event.getNewValue()));
        colQuantity.setOnEditCommit(event -> event.getRowValue().setQuantity(event.getNewValue()));
    }

    /**
     * Clears the table view and disables buttons.
     */
    private void clearForm() {
        tableList.clear();
        btnRemoveTable.setDisable(true);
        btnConfirmChanges.setDisable(true);
    }

    /**
     * Sets up the listener for row selection changes in the TableView.
     */
    private void setupRowSelection() {
        tblViewRestaurantLayout.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onSelectedTableFromTableView(newVal)
        );
    }

    /**
     * Validates that all tables have capacity and quantity greater than 0.
     *
     * @return true if all tables are valid, false otherwise
     */
    private boolean isAllTablesValid() {
        return tableList.stream().allMatch(t -> t.getCapacity() > 0 && t.getQuantity() > 0);
    }

    /**
     * Requests the table data from the server when the scene is shown.
     */
    private void requestTablesWhenSceneIsShown() {
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        controller.requestTables();
                    }
                });
            }
        });
    }

    /**
     * Inner class representing a table in the restaurant layout.
     */
    public static class Table {
        private final IntegerProperty capacity =
                new SimpleIntegerProperty();
        private final IntegerProperty quantity =
                new SimpleIntegerProperty();

        public Table(int capacity, int quantity) {
            this.capacity.set(capacity);
            this.quantity.set(quantity);
        }

        public IntegerProperty capacityProperty() { return capacity; }

        public javafx.beans.property.IntegerProperty quantityProperty() { return quantity; }

        public int getCapacity() { return capacity.get(); }

        public void setCapacity(int capacity) { this.capacity.set(capacity); }

        public int getQuantity() { return quantity.get(); }

        public void setQuantity(int quantity) { this.quantity.set(quantity); }
    }
}