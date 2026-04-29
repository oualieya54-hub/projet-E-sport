package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.connexion.Connexion;
import org.example.dao.CommandeDAO;
import org.example.dao.PanierDAO;
import org.example.modele.Commande;
import org.example.service.PaiementService;
import org.example.service.FacturationService;
import java.sql.SQLException;

public class CommandeController {

    @FXML private TableView<Commande> commandeTable;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button paiementStripButton;
    @FXML private Button annulerRemboursementButton;

    private CommandeDAO commandeDAO;
    private PanierDAO panierDAO;
    private PaiementService paiementService;
    private FacturationService factuationService;
    private Connexion connexion;
    private Commande selectedCommande;

    @FXML
    public void initialize() {
        connexion = new Connexion();
        commandeDAO = new CommandeDAO(connexion);
        panierDAO = new PanierDAO(connexion);
        paiementService = new PaiementService(connexion);
        factuationService = new FacturationService(connexion);

        loadCommandes();
    }

    private void loadCommandes() {
        try {
            // À implémenter selon votre logique
            System.out.println("Chargement des commandes...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePaiementStripe() throws SQLException {
        if (selectedCommande == null) {
            showError("Sélectionnez une commande!");
            return;
        }

        boolean success = paiementService.traiterPaiementStripe(
                selectedCommande.getId(),
                "tok_visa_4242",
                selectedCommande.getMontant()
        );

        if (success) {
            factuationService.genererFacturePDF(selectedCommande.getId());
            showInfo("✅ Paiement approuvé!");
        }
    }

    @FXML
    private void handleAnnulerAvecRemboursement() throws SQLException {
        if (selectedCommande == null) {
            showError("Sélectionnez une commande!");
            return;
        }

        paiementService.rembourserCommande(selectedCommande.getId());
        factuationService.traiterRembouissement(selectedCommande.getId());
        showInfo("💰 Remboursement effectué!");
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