package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import org.example.Model.Commande;
import org.example.Model.Produit;
import org.example.Model.Transaction;
import org.example.Service.CommandeService;
import org.example.Service.ProduitService;
import org.example.Service.TransactionService;

import java.util.List;

public class BoutiqueController {

    @FXML private FlowPane produitsContainer;
    @FXML private ListView<String> panierListView;
    @FXML private ListView<String> wishlistListView;

    @FXML private TextField clientUserIdInput;
    @FXML private TextField adresseInput;
    @FXML private TextField paiementInput;
    @FXML private TextField pointsInput;

    private ProduitService produitService;
    private TransactionService transactionService;
    private CommandeService commandeService;

    @FXML
    public void initialize() {
        produitService = new ProduitService();
        transactionService = new TransactionService();
        commandeService = new CommandeService();
        loadData();
    }

    @FXML
    public void loadData() {
        loadProduits();
        loadPanier();
        loadWishlist();
    }

    private void loadProduits() {
        produitsContainer.getChildren().clear();
        List<Produit> produits = produitService.findDisponibles();

        for (Produit p : produits) {
            VBox card = new VBox(10);
            card.getStyleClass().add("product-card");
            card.setPrefWidth(220);
            card.setAlignment(Pos.CENTER);

            // Gestion de l'Image (Vraie photo ou Placeholder)
            ImageView imgView = new ImageView();
            imgView.setFitWidth(180);
            imgView.setFitHeight(180);
            imgView.setPreserveRatio(true);

            try {
                // 1. On cherche d'abord s'il y a une vraie image dans le dossier 'images' à la racine du projet
                java.io.File filePng = new java.io.File("images/" + p.getIdProduit() + ".png");
                java.io.File fileJpg = new java.io.File("images/" + p.getIdProduit() + ".jpg");

                if (filePng.exists()) {
                    imgView.setImage(new Image(filePng.toURI().toString(), true));
                } else if (fileJpg.exists()) {
                    imgView.setImage(new Image(fileJpg.toURI().toString(), true));
                } else {
                    // 2. Sinon on génère l'image avec l'initiale
                    String initial = p.getNom().substring(0, 1).toUpperCase();
                    Image img = new Image("https://placehold.co/200x200/1f2937/00f3ff.png?text=" + initial, true);
                    imgView.setImage(img);
                }
            } catch (Exception e) {
                // Ignore image load error
            }

            Label nomLabel = new Label(p.getNom());
            nomLabel.getStyleClass().add("product-title");
            nomLabel.setWrapText(true);

            HBox priceBox = new HBox(10);
            priceBox.setAlignment(Pos.CENTER);
            if (p.isEnPromo()) {
                Label oldPrice = new Label(String.format("%.2f€", p.getPrix()));
                oldPrice.getStyleClass().add("product-price-strike");
                Label newPrice = new Label(String.format("%.2f€", p.getPrixEffectif()));
                newPrice.getStyleClass().add("product-price");
                priceBox.getChildren().addAll(oldPrice, newPrice);
            } else {
                Label price = new Label(String.format("%.2f€", p.getPrix()));
                price.getStyleClass().add("product-price");
                priceBox.getChildren().add(price);
            }

            HBox actions1 = new HBox(5);
            actions1.setAlignment(Pos.CENTER);
            Button btnPanier = new Button("🛒 Ajouter");
            btnPanier.getStyleClass().addAll("btn", "btn-primary");
            btnPanier.setOnAction(e -> handleAjouterPanier(p));

            Button btnWishlist = new Button("💖");
            btnWishlist.getStyleClass().addAll("btn", "btn-warning");
            btnWishlist.setOnAction(e -> handleAjouterWishlist(p));
            actions1.getChildren().addAll(btnPanier, btnWishlist);

            Button btnAvis = new Button("⭐ Laisser un Avis");
            btnAvis.getStyleClass().addAll("btn", "btn-outline");
            btnAvis.setMaxWidth(Double.MAX_VALUE);
            btnAvis.setOnAction(e -> handleLaisserAvis(p));

            card.getChildren().addAll(imgView, nomLabel, priceBox, actions1, btnAvis);
            produitsContainer.getChildren().add(card);
        }
    }

    private void loadPanier() {
        panierListView.getItems().clear();
        try {
            int userId = Integer.parseInt(clientUserIdInput.getText());
            List<Transaction> panier = transactionService.findPanierUser(userId);
            double total = 0;
            for (Transaction t : panier) {
                Produit p = produitService.findById(t.getIdProduit());
                if (p != null) {
                    double prix = p.isEnPromo() ? p.getPrixPromo() : p.getPrix();
                    total += prix * t.getQuantite();
                    panierListView.getItems().add(t.getQuantite() + "x " + p.getNom() + " - " + String.format("%.2f€", prix * t.getQuantite()));
                }
            }
            if (!panier.isEmpty()) {
                panierListView.getItems().add("------------------------");
                panierListView.getItems().add("TOTAL : " + String.format("%.2f€", total));
            } else {
                panierListView.getItems().add("Votre panier est vide.");
            }
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    private void loadWishlist() {
        wishlistListView.getItems().clear();
        try {
            int userId = Integer.parseInt(clientUserIdInput.getText());
            List<Transaction> wishlist = transactionService.findWishlistUser(userId);
            for (Transaction t : wishlist) {
                Produit p = produitService.findById(t.getIdProduit());
                if (p != null) {
                    wishlistListView.getItems().add("💖 " + p.getNom());
                }
            }
            if (wishlist.isEmpty()) {
                wishlistListView.getItems().add("Wishlist vide.");
            }
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    private void handleAjouterPanier(Produit p) {
        try {
            int userId = Integer.parseInt(clientUserIdInput.getText());
            if (transactionService.ajouterAuPanier(userId, p.getIdProduit(), 1)) {
                loadPanier();
            } else {
                showError("Erreur lors de l'ajout au panier.");
            }
        } catch (NumberFormatException e) {
            showError("Veuillez entrer un ID Utilisateur valide en haut.");
        }
    }

    private void handleAjouterWishlist(Produit p) {
        try {
            int userId = Integer.parseInt(clientUserIdInput.getText());
            if (transactionService.ajouterWishlist(userId, p.getIdProduit())) {
                loadWishlist();
            } else {
                showInfo("Ce produit est déjà dans votre Wishlist !");
            }
        } catch (NumberFormatException e) {
            showError("Veuillez entrer un ID Utilisateur valide en haut.");
        }
    }

    private void handleLaisserAvis(Produit p) {
        try {
            int userId = Integer.parseInt(clientUserIdInput.getText());
            TextInputDialog dialog = new TextInputDialog("5");
            dialog.setTitle("Note du produit");
            dialog.setHeaderText("Donnez une note sur 5 pour " + p.getNom());
            dialog.setContentText("Note (1 à 5):");

            dialog.showAndWait().ifPresent(noteStr -> {
                try {
                    int note = Integer.parseInt(noteStr);
                    if (note >= 1 && note <= 5) {
                        transactionService.posterAvis(userId, p.getIdProduit(), note, "Avis depuis la boutique client");
                        showInfo("Avis publié avec succès ! Note moyenne : " + transactionService.getNoteMoyenneProduit(p.getIdProduit()));
                    } else {
                        showError("La note doit être entre 1 et 5.");
                    }
                } catch (NumberFormatException e) {
                    showError("Note invalide.");
                }
            });
        } catch (NumberFormatException e) {
            showError("Veuillez entrer un ID Utilisateur valide en haut.");
        }
    }

    @FXML
    private void handleCommander() {
        try {
            int userId = Integer.parseInt(clientUserIdInput.getText());
            String adresse = adresseInput.getText();
            String paiement = paiementInput.getText();
            int points = pointsInput.getText().isEmpty() ? 0 : Integer.parseInt(pointsInput.getText());

            if (adresse.isEmpty() || paiement.isEmpty()) {
                showError("L'adresse et le paiement sont obligatoires.");
                return;
            }

            Commande c = commandeService.passerCommande(userId, adresse, paiement, points);
            if (c != null) {
                showInfo("🎉 Félicitations ! Commande passée avec succès. ID : " + c.getIdCommande() + "\nMontant payé: " + c.getMontantTotal() + "€");
                loadData();
                pointsInput.setText("0");
            } else {
                showError("Impossible de passer la commande (Panier vide ou stock insuffisant).");
            }
        } catch (NumberFormatException e) {
            showError("Les points et l'ID doivent être des nombres.");
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
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
