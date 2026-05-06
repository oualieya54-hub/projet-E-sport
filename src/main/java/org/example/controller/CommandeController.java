package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Utils.MyDatabase;
import org.example.Service.CommandeService;
import org.example.Model.Commande;
import java.sql.SQLException;

public class CommandeController {

    @FXML private TableView<Commande> commandeTable;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button paiementStripButton;
    @FXML private Button annulerRemboursementButton;

    private CommandeService commandeDAO;
    private Commande selectedCommande;

    @FXML
    public void initialize() {
        commandeDAO = new CommandeService();
        loadCommandes();
    }

    private void loadCommandes() {
        try {
            System.out.println("Chargement des commandes depuis BDD...");
            // commandeTable.getItems().setAll(commandeDAO.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePaiementStripe() {
        if (selectedCommande == null) {
            showError("Sélectionnez une commande!");
            return;
        }

        // TODO: Implémenter logique Stripe plus tard
        showInfo("✅ Simulation de paiement Stripe !");
    }

    @FXML
    private void handleAnnulerAvecRemboursement() {
        if (selectedCommande == null) {
            showError("Sélectionnez une commande!");
            return;
        }

        // TODO: Implémenter le remboursement plus tard
        showInfo("💰 Simulation de Remboursement effectué!");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}