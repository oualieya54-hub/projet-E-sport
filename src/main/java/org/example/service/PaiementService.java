package org.example.service;

import org.example.dao.commandeDAO;
import org.example.dao.PaiementDAO;
import org.example.modele.commande;
import org.example.modele.Paiement;
import java.math.BigDecimal;
import java.util.List;

public class PaiementService {
    private PaiementDAO PaiementDAO;
    private commandeDAO commandeDAO;

    public PaiementService() {
        this.PaiementDAO = new PaiementDAO();
        this.commandeDAO = new commandeDAO();
    }

    // 🔥 TRAITER PAIEMENT STRIPE
    public boolean traiterPaiementStripe(int idCommande, String carteToken, BigDecimal montant) {
        commande cmd = commandeDAO.getToutesCommandes().stream()
                .filter(c -> c.getIdCommande() == idCommande)
                .findFirst()
                .orElse(null);

        if (cmd == null) {
            System.err.println("❌ Commande non trouvée");
            return false;
        }

        if (cmd.getMontantTotal().compareTo(montant) != 0) {
            System.err.println("❌ Montant incorrect");
            return false;
        }

        try {
            // Simuler appel API Stripe
            boolean paiementReussi = simulerPaiementStripe(carteToken, montant);

            if (paiementReussi) {
                Paiement p = new Paiement(idCommande, montant, "carte_bancaire", "approuve");
                p.setTokenStripe(carteToken);
                p.setReferenceTransaction("TXN-" + System.currentTimeMillis());

                PaiementDAO.add(p);

                // Mettre à jour statut commande
                commandeDAO.changerStatut(idCommande, "confirmee");

                System.out.println("✅ Paiement Stripe approuvé - Commande #" + idCommande);
                return true;
            } else {
                System.out.println("❌ Paiement Stripe refusé");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur Stripe: " + e.getMessage());
            return false;
        }
    }

    // 🔥 TRAITER PAIEMENT SIMPLE
    public boolean traiterPaiement(int idCommande, String methode, BigDecimal montant) {
        try {
            Paiement p = new Paiement(idCommande, montant, methode, "en_attente");
            return PaiementDAO.add(p);
        } catch (Exception e) {
            System.err.println("❌ Erreur traitement paiement: " + e.getMessage());
            return false;
        }
    }

    // 🔥 REMBOURSER COMMANDE
    public boolean rembourserCommande(int idCommande) {
        try {
            commande cmd = commandeDAO.getToutesCommandes().stream()
                    .filter(c -> c.getIdCommande() == idCommande)
                    .findFirst()
                    .orElse(null);

            if (cmd == null) {
                System.err.println("❌ Commande non trouvée");
                return false;
            }

            if (cmd.getStatut().equals("livree")) {
                System.err.println("❌ Impossible de rembourser une commande livrée");
                return false;
            }

            Paiement p = PaiementDAO.getByIdCommande(idCommande);

            if (p == null) {
                System.err.println("❌ Aucun paiement trouvé");
                return false;
            }

            p.setStatut("rembourse");
            PaiementDAO.update(p);

            commandeDAO.changerStatut(idCommande, "annulee");

            System.out.println("💰 Remboursement approuvé: " + p.getMontant() + "€");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur remboursement: " + e.getMessage());
            return false;
        }
    }

    // 🔥 VÉRIFIER SI PAIEMENT APPROUVÉ
    public boolean isPaiementApprouve(int idCommande) {
        return PaiementDAO.isPaiementApprouve(idCommande);
    }

    // 🔥 OBTENIR STATUT PAIEMENT
    public String getStatutPaiement(int idCommande) {
        Paiement p = PaiementDAO.getByIdCommande(idCommande);
        return p != null ? p.getStatut() : "non_trouve";
    }

    // 🔥 MONTANT TOTAL PAIEMENTS APPROUVÉS
    public BigDecimal getMontantTotalApprouve() {
        List<Paiement> paiements = PaiementDAO.getByStatut("approuve");
        return paiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean simulerPaiementStripe(String carteToken, BigDecimal montant) {
        return !carteToken.isEmpty() && montant.compareTo(BigDecimal.ZERO) > 0;
    }
}