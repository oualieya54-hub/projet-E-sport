package org.example;

import org.example.connexion.connexionDB;
import org.example.dao.commandeDAO;
import org.example.dao.panierDAO;
import org.example.dao.produitDAO;
import org.example.modele.commande;
import org.example.modele.produit;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class testEshop {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("  TEST MODULE E-SHOP — E-Sport Project  ");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ── Test 1 : Connexion ─────────────────────────────────
        System.out.println("▶ TEST 1 : Connexion MySQL");
        boolean connecte = org.example.connexion.connexionDB.testerConnexion();
        System.out.println(connecte ? "   ✅ Connexion OK !\n" : "   ❌ Connexion échouée !\n");
        if (!connecte) {
            System.out.println("   → Lance WAMP et vérifie que la base 'esport_eshop' existe.");
            return;
        }

        // ── Test 2 : Lire tous les produits ───────────────────
        System.out.println("▶ TEST 2 : Lecture des produits");
        org.example.dao.produitDAO produitDAO = new org.example.dao.produitDAO();
        List<org.example.modele.produit> produits = produitDAO.getTousProduits();
        System.out.println("   Nombre de produits trouvés : " + produits.size());
        produits.forEach(p ->
                System.out.println("   • " + p.getNom() + " — " + p.getPrix() + " TND" +
                        " [stock: " + p.getStock() + "]")
        );
        System.out.println();

        // ── Test 3 : Recherche de produits ────────────────────
        System.out.println("▶ TEST 3 : Recherche 'Gaming'");
        List<org.example.modele.produit> resultats = produitDAO.rechercherProduits("Gaming");
        System.out.println("   Résultats : " + resultats.size());
        resultats.forEach(p -> System.out.println("   → " + p.getNom()));
        System.out.println();

        // ── Test 4 : Flash sales ───────────────────────────────
        System.out.println("▶ TEST 4 : Flash Sales actives");
        List<org.example.modele.produit> flashSales = produitDAO.getProduitsEnFlashSale();
        if (flashSales.isEmpty()) {
            System.out.println("   Aucune flash sale active.");
        } else {
            flashSales.forEach(p -> System.out.println("   🔥 " + p.getNom()));
        }
        System.out.println();

        // ── Test 5 : Ajouter un produit ───────────────────────
        System.out.println("▶ TEST 5 : Ajout d'un produit");
        org.example.modele.produit nouveau = new org.example.modele.produit(
                1,
                "Manette Pro Controller RGB",
                "Manette filaire, 8 boutons programmables, vibration",
                new BigDecimal("59.99"),
                30,
                "/images/manette.png",
                "disponible",
                "equipement",
                60
        );
        boolean ajoute = produitDAO.ajouterProduit(nouveau);
        System.out.println(ajoute
                ? "   ✅ Produit ajouté avec ID = " + nouveau.getIdProduit()
                : "   ❌ Échec ajout produit");
        System.out.println();

        // ── Test 6 : Panier ────────────────────────────────────
        System.out.println("▶ TEST 6 : Gestion du Panier (user id=1)");
        org.example.dao.panierDAO panierDAO = new org.example.dao.panierDAO();
        panierDAO.ajouterAuPanier(1, 1, 1); // Souris
        panierDAO.ajouterAuPanier(1, 7, 2); // Clé Steam x2
        List<Object[]> panier = panierDAO.getPanierUser(1);
        System.out.println("   Articles dans le panier :");
        panier.forEach(item -> {
            org.example.modele.produit p   = (org.example.modele.produit) item[0];
            int     qte = (Integer) item[1];
            System.out.println("   • " + p.getNom() + " × " + qte +
                    " = " + p.getPrix().multiply(BigDecimal.valueOf(qte)) + " TND");
        });
        System.out.println();

        // ── Test 7 : Wishlist ──────────────────────────────────
        System.out.println("▶ TEST 7 : Wishlist (user id=1)");
        panierDAO.ajouterWishlist(1, 2); // Casque
        panierDAO.ajouterWishlist(1, 3); // Clavier
        List<org.example.modele.produit> wishlist = panierDAO.getWishlistUser(1);
        System.out.println("   Produits favoris :");
        wishlist.forEach(p -> System.out.println("   ❤️ " + p.getNom()));
        System.out.println();

        // ── Test 8 : Points fidélité ───────────────────────────
        System.out.println("▶ TEST 8 : Points fidélité (user id=1)");
        org.example.dao.commandeDAO commandeDAO = new org.example.dao.commandeDAO();
        int solde = commandeDAO.getSoldePoints(1);
        System.out.println("   Solde actuel : " + solde + " points\n");

        // ── Test 9 : Passer une commande ───────────────────────
        System.out.println("▶ TEST 9 : Passer une commande");
        org.example.modele.commande commande = new org.example.modele.commande(
                1,                           // id_user
                new BigDecimal("104.98"),    // montant total
                0,                           // points utilisés
                "15 Rue de la République, Tunis",
                "carte_bancaire"
        );
        // {id_produit, quantite, prix*100}
        List<int[]> lignes = Arrays.asList(
                new int[]{1, 1, 7999},  // Souris × 1 = 79.99
                new int[]{7, 1, 2499}   // Clé Steam × 1 = 24.99
        );
        boolean commande_ok = commandeDAO.passerCommande(commande, lignes);
        System.out.println(commande_ok
                ? "   ✅ Commande #" + commande.getIdCommande() + " passée avec succès !"
                : "   ❌ Échec de la commande");
        System.out.println();

        // ── Test 10 : Vérifier le nouveau solde points ─────────
        System.out.println("▶ TEST 10 : Nouveau solde points après achat");
        int nouveauSolde = commandeDAO.getSoldePoints(1);
        System.out.println("   Solde avant : " + solde + " pts");
        System.out.println("   Solde après : " + nouveauSolde + " pts");
        System.out.println("   Points gagnés : +" + (nouveauSolde - solde) + " pts\n");

        // ── Fin ────────────────────────────────────────────────
        org.example.connexion.connexionDB.fermer();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("   TOUS LES TESTS TERMINÉS              ");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
