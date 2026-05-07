package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import org.example.Service.ProduitService;
import org.example.Model.Produit;

import java.time.LocalDateTime;

public class ProduitController {

    @FXML private ListView<Produit> produitList;
    @FXML private TextField nomInput;
    @FXML private TextField descInput;
    @FXML private TextField prixInput;
    @FXML private TextField stockInput;
    @FXML private ComboBox<String> categorieInput;
    @FXML private ComboBox<String> typeInput;
    @FXML private ComboBox<String> statutInput;
    @FXML private TextField promoPctInput;

    private ProduitService produitDAO;

    @FXML
    public void initialize() {
        produitDAO = new ProduitService();
        setupListView();
        loadProduits();

        // Listener pour remplir le formulaire lors de la sélection
        produitList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillForm(newSelection);
            }
        });
    }

    private void setupListView() {
        produitList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox box = new HBox(15);
                    box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    box.setPadding(new javafx.geometry.Insets(10));

                    VBox details = new VBox(5);
                    Label nameLabel = new Label(p.getNom());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00f3ff;");

                    Label catLabel = new Label(p.getCategorie() + " | " + p.getTypeProduit());
                    catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #cccccc;");

                    details.getChildren().addAll(nameLabel, catLabel);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    VBox pricing = new VBox(2);
                    pricing.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

                    Label priceLabel = new Label(String.format("%.2f €", p.getPrix()));
                    if (p.isEnPromo()) {
                        priceLabel.setStyle("-fx-text-decoration: line-through; -fx-text-fill: #ff4d4d; -fx-font-size: 11px;");
                        Label promoLabel = new Label(String.format("%.2f €", p.getPrixPromo()));
                        promoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff00; -fx-font-size: 14px;");
                        pricing.getChildren().addAll(priceLabel, promoLabel);
                    } else {
                        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #00f3ff; -fx-font-size: 14px;");
                        pricing.getChildren().add(priceLabel);
                    }

                    Label stockLabel = new Label("Stock: " + p.getStock());
                    stockLabel.setStyle(p.getStock() < 5 ? "-fx-text-fill: #ff4d4d; -fx-font-weight: bold;" : "-fx-text-fill: #ffffff;");

                    box.getChildren().addAll(details, spacer, pricing, stockLabel);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadProduits() {
        try {
            produitList.getItems().setAll(produitDAO.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des produits : " + e.getMessage());
        }
    }

    private void fillForm(Produit p) {
        nomInput.setText(p.getNom());
        descInput.setText(p.getDescription());
        prixInput.setText(String.valueOf(p.getPrix()));
        stockInput.setText(String.valueOf(p.getStock()));
        categorieInput.setValue(p.getCategorie());
        typeInput.setValue(p.getTypeProduit());
        statutInput.setValue(p.getStatut());
    }

    @FXML
    private void clearForm() {
        nomInput.clear();
        descInput.clear();
        prixInput.clear();
        stockInput.clear();
        categorieInput.setValue(null);
        typeInput.setValue(null);
        statutInput.setValue(null);
        produitList.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAddProduit() {
        try {
            if (nomInput.getText().isEmpty() || prixInput.getText().isEmpty()) {
                showError("Veuillez remplir les champs obligatoires (Nom, Prix).");
                return;
            }

            Produit p = new Produit();
            p.setNom(nomInput.getText());
            p.setDescription(descInput.getText());
            p.setPrix(Double.parseDouble(prixInput.getText()));
            p.setStock(Integer.parseInt(stockInput.getText()));
            p.setCategorie(categorieInput.getValue() != null ? categorieInput.getValue() : "Général");
            p.setTypeProduit(typeInput.getValue() != null ? typeInput.getValue() : "physique");
            p.setStatut(statutInput.getValue() != null ? statutInput.getValue() : "disponible");
            // Points gagnés : par exemple 10% du prix en points
            p.setPointsGagnes((int) (p.getPrix() * 0.10));

            if (produitDAO.ajouter(p)) {
                showInfo("Produit ajouté avec succès !");
                loadProduits();
                clearForm();
            } else {
                showError("Erreur lors de l'ajout.");
            }
        } catch (NumberFormatException e) {
            showError("Prix ou stock invalide.");
        }
    }

    @FXML
    private void handleUpdateProduit() {
        Produit selected = produitList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un produit à modifier.");
            return;
        }

        try {
            selected.setNom(nomInput.getText());
            selected.setDescription(descInput.getText());
            selected.setPrix(Double.parseDouble(prixInput.getText()));
            selected.setStock(Integer.parseInt(stockInput.getText()));
            selected.setCategorie(categorieInput.getValue());
            selected.setTypeProduit(typeInput.getValue());
            selected.setStatut(statutInput.getValue());

            if (produitDAO.modifier(selected)) {
                showInfo("Produit modifié avec succès !");
                loadProduits();
                clearForm();
            } else {
                showError("Erreur lors de la modification.");
            }
        } catch (NumberFormatException e) {
            showError("Prix ou stock invalide.");
        }
    }

    @FXML
    private void handleDeleteProduit() {
        Produit selected = produitList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un produit à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Voulez-vous vraiment supprimer le produit : " + selected.getNom() + " ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (produitDAO.supprimer(selected.getIdProduit())) {
                showInfo("Produit supprimé !");
                loadProduits();
                clearForm();
            } else {
                showError("Erreur de suppression.");
            }
        }
    }

    @FXML
    private void handleAppliquerPromo() {
        Produit selected = produitList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélectionnez un produit pour lui appliquer une promotion.");
            return;
        }

        try {
            double pct = Double.parseDouble(promoPctInput.getText());
            if (pct <= 0 || pct >= 100) {
                showError("Le pourcentage doit être entre 1 et 99.");
                return;
            }

            double prixPromo = selected.getPrix() * (1 - (pct / 100));
            String debut = LocalDateTime.now().toString().replace("T", " ").substring(0, 19);
            String fin = LocalDateTime.now().plusDays(7).toString().replace("T", " ").substring(0, 19);

            if (produitDAO.appliquerPromo(selected.getIdProduit(), prixPromo, debut, fin)) {
                showInfo("🔥 Promotion de " + pct + "% appliquée ! Nouveau prix : " + String.format("%.2f", prixPromo) + " €");
                loadProduits();
            } else {
                showError("Erreur lors de l'application de la promo.");
            }
        } catch (NumberFormatException e) {
            showError("Pourcentage invalide.");
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
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
