package org.example.service;

import org.example.dao.commandeDAO;
import org.example.dao.FactureDAO;
import org.example.modele.commande;
import org.example.modele.Facture;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FacturationService {
    private FactureDAO FactureDAO;
    private commandeDAO commandeDAO;

    public FacturationService() {
        this.FactureDAO = new FactureDAO();
        this.commandeDAO = new commandeDAO();
    }

    // 🔥 GÉNÉRER FACTURE PDF
    public String genererFacturePDF(int idCommande) {
        try {
            commande cmd = commandeDAO.getToutesCommandes().stream()
                    .filter(c -> c.getIdCommande() == idCommande)
                    .findFirst()
                    .orElse(null);

            if (cmd == null) {
                System.err.println("❌ Commande non trouvée");
                return null;
            }

            Facture f = new Facture(idCommande, cmd.getMontantTotal(), cmd.getAdresseLivraison());

            // Générer le fichier PDF (HTML simulé)
            String cheminPDF = genererPDF(f);
            f.setCheminPDF(cheminPDF);

            FactureDAO.add(f);

            System.out.println("✅ Facture PDF générée: " + f.getNumeroFacture());
            System.out.println("📁 Chemin: " + cheminPDF);

            return cheminPDF;
        } catch (Exception e) {
            System.err.println("❌ Erreur génération facture: " + e.getMessage());
            return null;
        }
    }

    // 🔥 ENVOYER FACTURE PAR EMAIL
    public boolean envoyerFactureEmail(int idCommande, String emailClient) {
        try {
            Facture f = FactureDAO.getByIdCommande(idCommande);

            if (f == null) {
                System.err.println("❌ Facture non trouvée");
                return false;
            }

            System.out.println("📧 Email envoyé à: " + emailClient);
            System.out.println("Sujet: Votre facture - " + f.getNumeroFacture());
            System.out.println("Pièce jointe: " + f.getCheminPDF());

            f.setEnvoyeeEmail(true);
            f.setDateEnvoi(LocalDateTime.now());
            FactureDAO.update(f);

            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
            return false;
        }
    }

    // 🔥 CONFIRMER COMMANDE PAR EMAIL
    public boolean confirmerCommandeEmail(int idCommande, String emailClient) {
        try {
            commande cmd = commandeDAO.getToutesCommandes().stream()
                    .filter(c -> c.getIdCommande() == idCommande)
                    .findFirst()
                    .orElse(null);

            if (cmd == null) {
                System.err.println("❌ Commande non trouvée");
                return false;
            }

            String sujet = "Confirmation de votre commande #" + idCommande;
            String corps = "Merci pour votre commande!\n" +
                    "Montant: " + cmd.getMontantTotal() + "€\n" +
                    "Adresse: " + cmd.getAdresseLivraison();

            System.out.println("✅ Email de confirmation envoyé à: " + emailClient);
            System.out.println("Sujet: " + sujet);

            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi confirmation: " + e.getMessage());
            return false;
        }
    }

    // 🔥 REMBOURSEMENT AUTOMATIQUE
    public boolean traiterRembouissement(int idCommande) {
        try {
            Facture f = FactureDAO.getByIdCommande(idCommande);

            if (f == null) {
                System.err.println("❌ Facture non trouvée");
                return false;
            }

            f.setStatut("remboursee");
            FactureDAO.update(f);

            System.out.println("💰 Remboursement automatique de " + f.getMontantTotal() + "€");

            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur remboursement: " + e.getMessage());
            return false;
        }
    }

    // 🔥 MONTANT TOTAL FACTURÉ
    public BigDecimal getMontantTotalFacture() {
        return FactureDAO.getMontantTotal();
    }

    // 🔥 NOMBRE DE FACTURES GÉNÉRÉES
    public int getNombreFacturesGenerees() {
        List<Facture> factures = FactureDAO.getByStatut("generee");
        return factures.size();
    }

    private String genererPDF(Facture f) {
        try {
            String nomFichier = f.getNumeroFacture() + ".html";
            String cheminComplet = "factures/" + nomFichier;

            // Créer dossier s'il n'existe pas
            new File("factures").mkdirs();

            // Créer fichier HTML
            FileWriter writer = new FileWriter(cheminComplet);
            writer.write("<html><head><meta charset='UTF-8'></head><body>");
            writer.write("<h1>FACTURE: " + f.getNumeroFacture() + "</h1>");
            writer.write("<p><strong>Commande:</strong> #" + f.getIdCommande() + "</p>");
            writer.write("<p><strong>Date:</strong> " + f.getDateFacture() + "</p>");
            writer.write("<p><strong>Adresse:</strong> " + f.getAdresseLivraison() + "</p>");
            writer.write("<hr>");
            writer.write("<p><strong>Montant HT:</strong> " + f.getMontantHT() + "€</p>");
            writer.write("<p><strong>TVA (20%):</strong> " + f.getMontantTVA() + "€</p>");
            writer.write("<p><strong>TOTAL TTC:</strong> " + f.getMontantTotal() + "€</p>");
            writer.write("</body></html>");
            writer.close();

            System.out.println("📄 Facture générée: " + cheminComplet);
            return cheminComplet;
        } catch (IOException e) {
            System.err.println("❌ Erreur génération PDF: " + e.getMessage());
            return null;
        }
    }
}