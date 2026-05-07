package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Model.Commande;
import org.example.Service.CommandeService;

public class CommandeController {

    @FXML private TableView<Commande> commandeTable;
    @FXML private ComboBox<String> statutInput;

    private CommandeService commandeService;

    @FXML
    public void initialize() {
        commandeService = new CommandeService();
        loadCommandes();
    }

    @FXML
    public void loadCommandes() {
        try {
            commandeTable.getItems().setAll(commandeService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangerStatut() {
        Commande selected = commandeTable.getSelectionModel().getSelectedItem();
        if (selected == null || statutInput.getValue() == null) {
            showError("Sélectionnez une commande et un statut.");
            return;
        }

        String nouveauStatut = statutInput.getValue();
        if (commandeService.changerStatut(selected.getIdCommande(), nouveauStatut)) {
            if (nouveauStatut.equals("annulée")) {
                showInfo("Commande annulée (Rollback de stock et points effectué).");
            } else {
                showInfo("Statut mis à jour !");
            }
            loadCommandes();
        } else {
            showError("Erreur lors du changement de statut.");
        }
    }

    @FXML
    private void handleDelete() {
        Commande selected = commandeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (commandeService.supprimer(selected.getIdCommande())) {
                showInfo("Commande supprimée.");
                loadCommandes();
            } else {
                showError("Erreur de suppression.");
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(message);
        alert.showAndWait();
    }
}