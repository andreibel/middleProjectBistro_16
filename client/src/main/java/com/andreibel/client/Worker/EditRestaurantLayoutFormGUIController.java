package com.andreibel.client.Worker;

import com.andreibel.client.Client.BistroClientController;
import com.andreibel.client.Client.IServerResponseListener;
import com.andreibel.client.util.BistroUtilities;
import com.andreibel.message.DTO.TableRequest;
import com.andreibel.message.DTO.TableResponse;
import com.andreibel.message.Message;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditRestaurantLayoutFormGUIController implements IServerResponseListener {

    @FXML
    private TableView<Table> tblViewRestaurantLayout;
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
    private AnchorPane rootPane;
    @FXML
    private Button btnGoBack;

    private ObservableList<Table> tableList = FXCollections.observableArrayList();
    private BistroClientController controller;

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
                btnRemoveTable.setDisable(tableList.isEmpty());
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

    @FXML
    private void onAddTableButtonClicked(ActionEvent event) {
        Table newTable = new Table(0, 0);
        tableList.add(newTable);
        tblViewRestaurantLayout.getSelectionModel().select(newTable);
        tblViewRestaurantLayout.scrollTo(newTable);
        btnConfirmChanges.setDisable(false);
    }

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

    @FXML
    private void onGoBackButtonClicked(ActionEvent event) throws IOException {
        clearForm();
        BistroUtilities.switchScreen((Node) event.getSource(), "/Worker/WorkerForm.fxml", "Bistro Restaurant - Staff Area");
    }

    private void onSelectedTableFromTableView(Table t) {
        btnRemoveTable.setDisable(t == null);
        btnConfirmChanges.setDisable(tableList.isEmpty());
    }

    private void setTableView() {
        colCapacity.setCellValueFactory(cell -> cell.getValue().capacityProperty().asObject());
        colQuantity.setCellValueFactory(cell -> cell.getValue().quantityProperty().asObject());
    }

    private void enableEditing() {
        tblViewRestaurantLayout.setEditable(true);

        colCapacity.setEditable(true);
        colQuantity.setEditable(true);

        colCapacity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colCapacity.setOnEditCommit(event -> event.getRowValue().setCapacity(event.getNewValue()));
        colQuantity.setOnEditCommit(event -> event.getRowValue().setQuantity(event.getNewValue()));
    }

    private void clearForm() {
        tableList.clear();
        btnRemoveTable.setDisable(true);
        btnConfirmChanges.setDisable(true);
    }

    private void setupRowSelection() {
        tblViewRestaurantLayout.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onSelectedTableFromTableView(newVal)
        );
    }

    private boolean isAllTablesValid() {
        return tableList.stream().allMatch(t -> t.getCapacity() > 0 && t.getQuantity() > 0);
    }

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

    public static class Table {
        private final javafx.beans.property.IntegerProperty capacity =
                new javafx.beans.property.SimpleIntegerProperty();
        private final javafx.beans.property.IntegerProperty quantity =
                new javafx.beans.property.SimpleIntegerProperty();

        public Table(int capacity, int quantity) {
            this.capacity.set(capacity);
            this.quantity.set(quantity);
        }

        public javafx.beans.property.IntegerProperty capacityProperty() {return capacity;}

        public javafx.beans.property.IntegerProperty quantityProperty() {return quantity;}

        public int getCapacity() {return capacity.get();}

        public void setCapacity(int capacity) {this.capacity.set(capacity);}

        public int getQuantity() {return quantity.get();}

        public void setQuantity(int quantity) {this.quantity.set(quantity);}
    }
}