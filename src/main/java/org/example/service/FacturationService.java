package org.example.service;

import org.example.connexion.Connexion;
import org.example.dao.CommandeDAO;
import org.example.dao.FactureDAO;
import org.example.dao.PanierDAO;
import org.example.modele.Commande;
import org.example.modele.Facture;
import org.example.modele.Panier;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FacturationService {
    private FactureDAO factureDAO;
    private CommandeDAO commandeDAO;
    private PanierDAO panierDAO;

    public FacturationService(Connexion connexion) {
        this.factureDAO = new FactureDAO(connexion);
        this.commandeDAO = new CommandeDAO(connexion);
        this.panierDAO = new PanierDAO(connexion);
    }

    // 🔥 GÉNÉRER FACTURE PDF
    public String genererFacturePDF(int commandeId) throws SQLException {
        Commande commande = commandeDAO.getById(commandeId);

        if (commande == null) {
            throw new IllegalArgumentException("Commande non trouvée");
        }

        Facture facture = new Facture(commandeId, commande.getMontant(), commande.getAdresse());

        // Ajouter les articles du panier
        List<Panier> articles = panierDAO.getByCommandeId(commandeId);

        // Générer le fichier PDF
        String cheminPDF = genererPDF(facture, articles);
        facture.setCheminPDF(cheminPDF);

        factureDAO.add(facture);

        System.out.println("✅ Facture PDF générée: " + facture.getNumeroFacture());
        System.out.println("📁 Chemin: " + cheminPDF);

        return cheminPDF;
    }

    // 🔥 ENVOYER FACTURE PAR EMAIL
    public boolean envoyerFactureEmail(int commandeId, String emailClient) throws SQLException {
        Facture facture = factureDAO.getByCommandeId(commandeId);

        if (facture == null) {
            System.err.println("Facture non trouvée pour la commande #" + commandeId);
            return false;
        }

        try {
            // Simuler envoi email
            System.out.println("📧 Email envoyé à: " + emailClient);
            System.out.println("Sujet: Votre facture - " + facture.getNumeroFacture());
            System.out.println("Pièce jointe: " + facture.getCheminPDF());

            facture.setEnvoyeeEmail(true);
            facture.setDateEnvoi(LocalDateTime.now());
            factureDAO.update(facture);

            return true;
        } catch (Exception e) {
            System.err.println("Erreur envoi email: " + e.getMessage());
            return false;
        }
    }

    // 🔥 CONFIRMER COMMANDE PAR EMAIL
    public boolean confirmerCommandeEmail(int commandeId, String emailClient) throws SQLException {
        Commande commande = commandeDAO.getById(commandeId);

        if (commande == null) {
            throw new IllegalArgumentException("Commande non trouvée");
        }

        try {
            String sujet = "Confirmation de votre commande #" + commandeId;
            String corps = "Merci pour votre commande!\n" +
                    "Montant: " + commande.getMontant() + "€\n" +
                    "Adresse: " + commande.getAdresse();

            System.out.println("✅ Email de confirmation envoyé à: " + emailClient);
            System.out.println("Sujet: " + sujet);

            return true;
        } catch (Exception e) {
            System.err.println("Erreur envoi confirmation: " + e.getMessage());
            return false;
        }
    }

    // 🔥 REMBOURSEMENT AUTOMATIQUE
    public boolean traiterRembouissement(int commandeId) throws SQLException {
        Facture facture = factureDAO.getByCommandeId(commandeId);

        if (facture == null) {
            throw new IllegalArgumentException("Facture non trouvée");
        }

        facture.setStatut("remboursee");
        factureDAO.update(facture);

        Commande commande = commandeDAO.getById(commandeId);
        commande.setStatut("annulee");
        commandeDAO.update(commande);

        System.out.println("💰 Remboursement automatique de " + facture.getMontantTotal() + "€");

        return true;
    }

    // 🔥 MONTANT TOTAL FACTURÉ
    public double getMontantTotalFacture() throws SQLException {
        return factureDAO.getMontantTotal();
    }

    // 🔥 NOMBRE DE FACTURES GÉNÉRÉES
    public int getNombreFacturesGenerees() throws SQLException {
        List<Facture> factures = factureDAO.getByStatut("generee");
        return factures.size();
    }

    private String genererPDF(Facture facture, List<Panier> articles) {
        try {
            String nomFichier = facture.getNumeroFacture() + ".html";
            String cheminComplet = "factures/" + nomFichier;

            // Créer dossier s'il n'existe pas
            new File("factures").mkdirs();

            // Créer fichier HTML (simulant PDF)
            FileWriter writer = new FileWriter(cheminComplet);
            writer.write("<html><body>");
            writer.write("<h1>Facture: " + facture.getNumeroFacture() + "</h1>");
            writer.write("<p>Date: " + facture.getDate() + "</p>");
            writer.write("<p>Montant Total: " + facture.getMontantTotal() + "€</p>");
            writer.write("<p>Montant HT: " + facture.getMontantHT() + "€</p>");
            writer.write("<p>TVA (20%): " + facture.getMontantTVA() + "€</p>");
            writer.write("<p>Adresse: " + facture.getAdresseLivraison() + "</p>");
            writer.write("</body></html>");
            writer.close();

            System.out.println("📄 Facture générée: " + cheminComplet);
            return cheminComplet;
        } catch (IOException e) {
            System.err.println("Erreur génération PDF: " + e.getMessage());
            return null;
        }
    }
}
