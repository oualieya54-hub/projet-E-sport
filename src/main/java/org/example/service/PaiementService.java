package org.example.service;

import org.example.connexion.Connexion;
import org.example.dao.CommandeDAO;
import org.example.dao.PaiementDAO;
import org.example.modele.Commande;
import org.example.modele.Paiement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PaiementService {
    private PaiementDAO paiementDAO;
    private CommandeDAO commandeDAO;

    public PaiementService(Connexion connexion) {
        this.paiementDAO = new PaiementDAO(connexion);
        this.commandeDAO = new CommandeDAO(connexion);
    }

    // 🔥 TRAITER PAIEMENT STRIPE
    public boolean traiterPaiementStripe(int commandeId, String carteToken, double montant) throws SQLException {
        Commande commande = commandeDAO.getById(commandeId);

        if (commande == null) {
            throw new IllegalArgumentException("Commande non trouvée");
        }

        if (Math.abs(commande.getMontant() - montant) > 0.01) {
            throw new IllegalArgumentException("Montant incorrect");
        }

        try {
            // Simuler appel API Stripe
            boolean paiementReussi = simulerPaiementStripe(carteToken, montant);

            if (paiementReussi) {
                Paiement paiement = new Paiement(commandeId, montant, "carte_bancaire", "approuve");
                paiement.setTokenStripe(carteToken);
                paiement.setReferenceTransaction("TXN-" + System.currentTimeMillis());

                paiementDAO.add(paiement);

                // Mettre à jour statut commande
                commande.setStatut("confirmee");
                commandeDAO.update(commande);

                System.out.println("✅ Paiement Stripe approuvé - Commande #" + commandeId);
                return true;
            } else {
                System.out.println("❌ Paiement Stripe refusé");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Erreur Stripe: " + e.getMessage());
            return false;
        }
    }

    // 🔥 TRAITER PAIEMENT SIMPLE
    public void traiterPaiement(int commandeId, String methode, double montant) throws SQLException {
        Commande commande = commandeDAO.getById(commandeId);

        if (commande == null) {
            throw new IllegalArgumentException("Commande non trouvée");
        }

        Paiement paiement = new Paiement(commandeId, montant, methode, "en_attente");
        paiementDAO.add(paiement);

        System.out.println("⏳ Paiement en attente: " + methode);
    }

    // 🔥 REMBOURSER COMMANDE
    public boolean rembourserCommande(int commandeId) throws SQLException {
        Commande commande = commandeDAO.getById(commandeId);

        if (commande == null) {
            throw new IllegalArgumentException("Commande non trouvée");
        }

        if (commande.getStatut().equals("livree")) {
            throw new IllegalArgumentException("Impossible de rembourser une commande livrée");
        }

        Paiement paiement = paiementDAO.getByCommandeId(commandeId);

        if (paiement == null) {
            throw new IllegalArgumentException("Aucun paiement trouvé");
        }

        paiement.setStatut("rembourse");
        paiementDAO.update(paiement);

        commande.setStatut("annulee");
        commandeDAO.update(commande);

        System.out.println("💰 Remboursement approuvé: " + paiement.getMontant() + "€");
        return true;
    }

    // ��� VÉRIFIER SI PAIEMENT APPROUVÉ
    public boolean isPaiementApprouve(int commandeId) throws SQLException {
        return paiementDAO.isPaiementApprouve(commandeId);
    }

    // 🔥 OBTENIR STATUT PAIEMENT
    public String getStatutPaiement(int commandeId) throws SQLException {
        Paiement paiement = paiementDAO.getByCommandeId(commandeId);
        return paiement != null ? paiement.getStatut() : "non_trouve";
    }

    // 🔥 MONTANT TOTAL PAIEMENTS APPROUVÉS
    public double getMontantTotalApprouve() throws SQLException {
        List<Paiement> paiements = paiementDAO.getByStatut("approuve");
        return paiements.stream().mapToDouble(Paiement::getMontant).sum();
    }

    private boolean simulerPaiementStripe(String carteToken, double montant) {
        return !carteToken.isEmpty() && montant > 0;
    }
}
