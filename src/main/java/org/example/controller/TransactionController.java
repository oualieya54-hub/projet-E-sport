package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Model.Transaction;
import org.example.Service.TransactionService;

public class TransactionController {

    @FXML private TableView<Transaction> transactionTable;
    private TransactionService transactionService;

    @FXML
    public void initialize() {
        transactionService = new TransactionService();
        loadTransactions();
    }

    @FXML
    public void loadTransactions() {
        try {
            transactionTable.getItems().setAll(transactionService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (transactionService.supprimer(selected.getIdTransaction())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Transaction supprimée.");
                alert.showAndWait();
                loadTransactions();
            }
        }
    }
}
