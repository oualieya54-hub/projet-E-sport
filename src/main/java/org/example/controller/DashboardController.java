package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.Service.CommandeService;
import org.example.Service.ProduitService;
import org.example.Service.TransactionService;

public class DashboardController {

    @FXML private Label lblTotalProduits;
    @FXML private Label lblTotalCommandes;
    @FXML private Label lblTotalTransactions;

    private ProduitService produitService;
    private CommandeService commandeService;
    private TransactionService transactionService;

    @FXML
    public void initialize() {
        produitService = new ProduitService();
        commandeService = new CommandeService();
        transactionService = new TransactionService();
        loadStats();
    }

    @FXML
    private void loadStats() {
        try {
            int totalProduits = produitService.findAll().size();
            int totalCommandes = commandeService.findAll().size();
            int totalTransactions = transactionService.findAll().size();

            lblTotalProduits.setText(String.valueOf(totalProduits));
            lblTotalCommandes.setText(String.valueOf(totalCommandes));
            lblTotalTransactions.setText(String.valueOf(totalTransactions));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
