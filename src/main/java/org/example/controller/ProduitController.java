package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Service.ProduitService;
import org.example.Model.Produit;

public class ProduitController {

    @FXML private TableView<Produit> produitTable;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private ProduitService produitDAO;
    private Produit selectedProduit;

    @FXML
    public void initialize() {
        produitDAO = new ProduitService();
        loadProduits();
    }

    private void loadProduits() {
        System.out.println("Chargement des produits depuis la BDD...");
        try {
            produitTable.getItems().setAll(produitDAO.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des produits : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProduit() {
        System.out.println("Ajout d'un produit...");
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
