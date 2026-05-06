package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Model.Transaction;
import org.example.Service.TransactionService;

public class TransactionController {

    @FXML private TableView<Transaction> transactionTable;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private TransactionService transactionDAO;
    private Transaction selectedTransaction;

    @FXML
    public void initialize() {
        transactionDAO = new TransactionService();
        loadTransactions();
    }

    private void loadTransactions() {
        System.out.println("Chargement des transactions depuis la BDD...");
        try {
            // transactionTable.getItems().setAll(transactionDAO.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedTransaction != null) {
            transactionDAO.supprimer(selectedTransaction.getIdTransaction());
            loadTransactions();
        }
    }
}
