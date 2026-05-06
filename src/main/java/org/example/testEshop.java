package org.example;

import org.example.Model.Produit;
import org.example.Model.Commande;
import org.example.Model.Transaction;
import org.example.Service.ProduitService;
import org.example.Service.CommandeService;
import org.example.Service.TransactionService;

import java.util.List;

public class testEshop {
    public static void main(String[] args) {
        System.out.println("============== TEST DU CRUD ==============");

        // 1. Initialisation des services
        ProduitService ps = new ProduitService();
        CommandeService cs = new CommandeService();
        TransactionService ts = new TransactionService();

        // 2. Test Produit (CREATE et READ)
        System.out.println("\n--- TEST PRODUIT ---");
        Produit p = new Produit("Souris Gamer PRO", 49.99, 10, "equipement", "Peripherique", 50);
        ps.ajouter(p);

        System.out.println("Liste des produits :");
        List<Produit> produits = ps.findAll();
        for (Produit prod : produits) {
            System.out.println(prod);
        }

        // 3. Test Commande (CREATE et READ)
        System.out.println("\n--- TEST COMMANDE ---");
        Commande c = new Commande(1, 49.99, "123 Rue de la Victoire", "Carte Bancaire");
        cs.ajouter(c);

        System.out.println("Liste des commandes :");
        for (Commande cmd : cs.findAll()) {
            System.out.println(cmd);
        }

        // 4. Test Transaction (CREATE et READ)
        System.out.println("\n--- TEST TRANSACTION ---");
        if (!produits.isEmpty()) {
            // On poste un avis sur le premier produit ajouté
            int idProduitGénéré = produits.get(0).getIdProduit();
            ts.posterAvis(1, idProduitGénéré, 5, "Super article !");
        }

        System.out.println("Liste des transactions :");
        for (Transaction trans : ts.findAll()) {
            System.out.println(trans);
        }

        System.out.println("\n============== FIN DU TEST ==============");
    }
}
