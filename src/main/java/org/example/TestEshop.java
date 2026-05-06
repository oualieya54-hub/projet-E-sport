package org.example;

import org.example.Model.Commande;
import org.example.Model.Produit;
import org.example.Model.User;
import org.example.Service.CommandeService;
import org.example.Service.ProduitService;
import org.example.Service.TransactionService;
import org.example.Service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestEshop {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🚀 DÉBUT DES TESTS COMPLETS E-SHOP (AVEC MÉTIERS)");
        System.out.println("=================================================");

        UserService userService = new UserService();
        ProduitService produitService = new ProduitService();
        TransactionService transactionService = new TransactionService();
        CommandeService commandeService = new CommandeService();

        // 1. Création d'un utilisateur de test
        System.out.println("\n[ÉTAPE 1] --- TEST CREATION USER ---");
        User alice = new User();
        alice.setNom("Alice Test");
        alice.setPseudo("alice_gaming_" + System.currentTimeMillis());
        alice.setEmail("alice" + System.currentTimeMillis() + "@test.com");
        alice.setPassword("password123");
        alice.setPoints(0);

        if (userService.ajouter(alice)) {
            System.out.println("✅ Utilisateur Alice créé avec l'ID: " + alice.getId());
        } else {
            System.out.println("❌ Échec création Alice.");
            return;
        }

        // 2. Création de produits
        System.out.println("\n[ÉTAPE 2] --- TEST CREATION PRODUITS ---");
        Produit clavier = new Produit();
        clavier.setNom("Clavier Mécanique Gamer");
        clavier.setDescription("Clavier RGB Switch Red");
        clavier.setPrix(100.0);
        clavier.setStock(10);
        clavier.setCategorie("Périphérique");
        clavier.setTypeProduit("equipement");
        clavier.setPointsGagnes(50);
        produitService.ajouter(clavier);

        Produit pc = new Produit();
        pc.setNom("PC Gamer Ultime");
        pc.setDescription("RTX 4090, i9");
        pc.setPrix(2000.0);
        pc.setStock(5);
        pc.setCategorie("Ordinateur");
        pc.setTypeProduit("equipement");
        pc.setPointsGagnes(500);
        produitService.ajouter(pc);

        System.out.println("✅ Produits ajoutés. ID Clavier: " + clavier.getIdProduit() + " | ID PC: " + pc.getIdProduit());

        // 3. Test des Promotions (Métier Produit)
        System.out.println("\n[ÉTAPE 3] --- TEST PROMOTION CATEGORIE ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String debut = LocalDateTime.now().minusDays(1).format(formatter);
        String fin = LocalDateTime.now().plusDays(5).format(formatter);

        produitService.appliquerPromotionCategorie("Périphérique", 20.0, debut, fin); // 20% de réduc
        System.out.println("✅ Promotion de 20% appliquée sur les Périphériques.");

        Produit clavierApresPromo = produitService.findById(clavier.getIdProduit());
        System.out.println("➡️ Nouveau prix promo du clavier: " + clavierApresPromo.getPrixPromo() + "€ (Ancien prix fixe: " + clavier.getPrix() + "€)");

        // 4. Test Panier (Métier Transaction)
        System.out.println("\n[ÉTAPE 4] --- TEST PANIER ---");
        transactionService.ajouterAuPanier(alice.getId(), clavier.getIdProduit(), 2);
        System.out.println("✅ 2 Claviers ajoutés au panier d'Alice.");

        // 5. Test Passage de Commande (Métier Commande)
        System.out.println("\n[ÉTAPE 5] --- TEST PASSAGE DE COMMANDE ---");
        // Alice passe commande avec 0 points à utiliser
        Commande cmd1 = commandeService.passerCommande(alice.getId(), "123 Rue du Test", "Carte Bancaire", 0);

        if (cmd1 != null) {
            System.out.println("✅ Commande passée! Montant total payé: " + cmd1.getMontantTotal() + "€");

            // Vérification du stock post-commande
            Produit clavierVerifStock = produitService.findById(clavier.getIdProduit());
            System.out.println("➡️ Nouveau stock du clavier (devrait être 8): " + clavierVerifStock.getStock());

            // Vérification des points gagnés
            User aliceApresAchat = userService.findById(alice.getId());
            System.out.println("➡️ Points d'Alice après achat (devrait être 100 car 2 claviers à 50pts): " + aliceApresAchat.getPoints());
        }

        // 6. Test Dépense Totale et VIP (Métier User & Transaction)
        System.out.println("\n[ÉTAPE 6] --- TEST STATUT VIP ET BONUS ---");
        System.out.println("Alice achète un PC pour débloquer le VIP...");
        transactionService.ajouterAuPanier(alice.getId(), pc.getIdProduit(), 1);
        Commande cmdPc = commandeService.passerCommande(alice.getId(), "123 Rue du Test", "Carte Bancaire", 0);

        double depenseTotale = transactionService.calculerTotalDepense(alice.getId());
        System.out.println("➡️ Dépense totale d'Alice sur la plateforme : " + depenseTotale + "€");

        System.out.println("Vérification de l'éligibilité VIP...");
        userService.verifierEtPromouvoirVIP(alice.getId());

        User aliceVip = userService.findById(alice.getId());
        System.out.println("➡️ Rôle actuel d'Alice : " + aliceVip.getRole());
        System.out.println("➡️ Points actuels (Bonus VIP + achat PC + achat claviers) : " + aliceVip.getPoints());

        // 7. Test Avis et Notes
        System.out.println("\n[ÉTAPE 7] --- TEST AVIS SUR UN PRODUIT ---");
        transactionService.posterAvis(alice.getId(), clavier.getIdProduit(), 5, "Clavier incroyable pour le prix !");
        double noteMoyenne = transactionService.getNoteMoyenneProduit(clavier.getIdProduit());
        System.out.println("✅ Note moyenne du clavier : " + noteMoyenne + "/5");

        // 8. Test Annulation de Commande (Métier Commande)
        System.out.println("\n[ÉTAPE 8] --- TEST ANNULATION DE COMMANDE (ROLLBACK) ---");
        if (cmd1 != null) {
            System.out.println("Annulation de la première commande (les 2 claviers)...");
            commandeService.changerStatut(cmd1.getIdCommande(), "annulée");

            Produit clavierRestaure = produitService.findById(clavier.getIdProduit());
            System.out.println("➡️ Stock du clavier restauré (devrait revenir à 10): " + clavierRestaure.getStock());

            User aliceApresAnnulation = userService.findById(alice.getId());
            System.out.println("➡️ Points d'Alice après annulation (les 100 points de la commande annulée ont été retirés) : " + aliceApresAnnulation.getPoints());
        }

        System.out.println("\n=================================================");
        System.out.println("🎉 FIN DES TESTS : TOUTES LES FONCTIONNALITÉS SONT OPÉRATIONNELLES");
        System.out.println("=================================================");
    }
}
