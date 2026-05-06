package org.example;

import org.example.Service.CommandeService;
import org.example.Model.Commande;
import org.example.service.PaiementService;
import org.example.service.FacturationService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class testPaiement {
    public static void main(String[] args) {
        System.out.println("=== TEST PAIEMENT & FACTURATION ===\n");

        // Initialiser les services
        PaiementService paiementService = new PaiementService();
        FacturationService factuationService = new FacturationService();
        CommandeService commandeDAO = new CommandeService();

        try {
            // ============================================
            // 1️⃣ CRÉER UNE COMMANDE
            // ============================================
            System.out.println("📦 1️⃣ CRÉER UNE COMMANDE");
            System.out.println("=====================================");

            Commande cmd = new Commande(
                    1,
                    BigDecimal.valueOf(150.00),
                    0,
                    "123 Rue de la Paix, 75000 Paris",
                    "carte_bancaire"
            );

            // Créer des articles pour la commande
            List<int[]> lignes = new ArrayList<>();
            lignes.add(new int[]{1, 2, 5000}); // produit 1, qty 2, prix 50€
            lignes.add(new int[]{2, 1, 10000});

            boolean commandeCreee = commandeDAO.passerCommande(cmd, lignes);

            if (!commandeCreee) {
                System.err.println("❌ Erreur création commande. Arrêt du test.");
                return;
            }

            int idCommande = cmd.getIdCommande();
            System.out.println("✅ Commande créée - ID: " + idCommande + "\n");


            System.out.println("💳 2️⃣ TRAITER PAIEMENT STRIPE");
            System.out.println("=====================================");

            boolean paiementApprouve = paiementService.traiterPaiementStripe(
                    idCommande,
                    "tok_visa_4242",
                    BigDecimal.valueOf(150.00)
            );

            if (paiementApprouve) {
                System.out.println("✅ Paiement Stripe approuvé!\n");
            } else {
                System.out.println("❌ Paiement Stripe refusé!\n");
                return;
            }


            System.out.println("🔍 3️⃣ VÉRIFIER STATUT PAIEMENT");
            System.out.println("=====================================");

            String statut = paiementService.getStatutPaiement(idCommande);
            System.out.println("Statut du paiement: " + statut);

            boolean estApprouve = paiementService.isPaiementApprouve(idCommande);
            System.out.println("Paiement approuvé? " + (estApprouve ? "✅ OUI" : "❌ NON") + "\n");


            System.out.println("📄 4️⃣ GÉNÉRER FACTURE PDF");
            System.out.println("=====================================");

            String cheminFacture = factuationService.genererFacturePDF(idCommande);

            if (cheminFacture != null) {
                System.out.println("✅ Facture générée avec succès!");
                System.out.println("📁 Chemin du fichier: " + cheminFacture + "\n");
            } else {
                System.out.println("❌ Erreur génération facture\n");
            }


            System.out.println("📧 5️⃣ ENVOYER EMAILS");
            System.out.println("=====================================");

            String emailClient = "client@example.com";

            boolean confirmationEnvoyee = factuationService.confirmerCommandeEmail(idCommande, emailClient);
            System.out.println(confirmationEnvoyee ? "✅ Email de confirmation envoyé" : "❌ Erreur envoi confirmation");

            boolean factureEnvoyee = factuationService.envoyerFactureEmail(idCommande, emailClient);
            System.out.println(factureEnvoyee ? "✅ Email de facture envoyé" : "❌ Erreur envoi facture");
            System.out.println();


            System.out.println("📋 6️⃣ AFFICHER LES INFORMATIONS");
            System.out.println("=====================================");

            PaiementDAO paiementDAO = new PaiementDAO();
            Paiement p = paiementDAO.getByIdCommande(idCommande);
            System.out.println("🔹 Paiement:");
            System.out.println(p);

            FactureDAO factureDAO = new FactureDAO();
            Facture f = factureDAO.getByIdCommande(idCommande);
            System.out.println("\n🔹 Facture:");
            System.out.println(f);
            System.out.println("   Montant HT: " + f.getMontantHT());
            System.out.println("   Montant TVA: " + f.getMontantTVA());
            System.out.println("   Montant TTC: " + f.getMontantTotal());
            System.out.println();


            System.out.println("📊 7️⃣ STATISTIQUES");
            System.out.println("=====================================");

            BigDecimal montantTotal = factuationService.getMontantTotalFacture();
            System.out.println("Montant total facturé: " + montantTotal + "€");

            int nbFactures = factuationService.getNombreFacturesGenerees();
            System.out.println("Nombre de factures générées: " + nbFactures);

            BigDecimal montantApprouve = paiementService.getMontantTotalApprouve();
            System.out.println("Montant total paiements approuvés: " + montantApprouve + "€\n");


            System.out.println("💰 8️⃣ TEST REMBOURSEMENT (OPTIONNEL)");
            System.out.println("=====================================");
            System.out.println("⚠️  Attention: Cette action annulera la commande et la remboursera");
            System.out.println("Pour tester, décommenter les lignes ci-dessous:\n");



            System.out.println("\n✅ TEST TERMINÉ AVEC SUCCÈS!");

        } catch (Exception e) {
            System.err.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}