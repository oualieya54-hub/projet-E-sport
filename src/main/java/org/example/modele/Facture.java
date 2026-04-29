package org.example.modele;

import java.time.LocalDateTime;

public class Facture {
    private int id;
    private String numeroFacture;
    private int commandeId;
    private LocalDateTime date;
    private LocalDateTime dateEnvoi;
    private double montantTotal;
    private double montantTVA;
    private double montantHT;
    private String statut; // generee, envoyee, remboursee, annulee
    private String adresseLivraison;
    private boolean envoyeeEmail;
    private String cheminPDF;

    public Facture() {}

    public Facture(int commandeId, double montantTotal, String adresseLivraison) {
        this.commandeId = commandeId;
        this.montantTotal = montantTotal;
        this.adresseLivraison = adresseLivraison;
        this.date = LocalDateTime.now();
        this.numeroFacture = "FAC-" + commandeId + "-" + System.currentTimeMillis();
        this.statut = "generee";
        this.envoyeeEmail = false;
        calculerTVA();
    }

    private void calculerTVA() {
        this.montantTVA = montantTotal * 0.20; // 20% TVA
        this.montantHT = montantTotal - montantTVA;
    }

    // Getters
    public int getId() { return id; }
    public String getNumeroFacture() { return numeroFacture; }
    public int getCommandeId() { return commandeId; }
    public LocalDateTime getDate() { return date; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public double getMontantTotal() { return montantTotal; }
    public double getMontantTVA() { return montantTVA; }
    public double getMontantHT() { return montantHT; }
    public String getStatut() { return statut; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public boolean isEnvoyeeEmail() { return envoyeeEmail; }
    public String getCheminPDF() { return cheminPDF; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }
    public void setCommandeId(int commandeId) { this.commandeId = commandeId; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; calculerTVA(); }
    public void setStatut(String statut) { this.statut = statut; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
    public void setEnvoyeeEmail(boolean envoyeeEmail) { this.envoyeeEmail = envoyeeEmail; }
    public void setCheminPDF(String cheminPDF) { this.cheminPDF = cheminPDF; }

    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", numeroFacture='" + numeroFacture + '\'' +
                ", commandeId=" + commandeId +
                ", montantTotal=" + montantTotal +
                ", statut='" + statut + '\'' +
                '}';
    }
}