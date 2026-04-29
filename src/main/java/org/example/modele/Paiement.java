package org.example.modele;

import java.time.LocalDateTime;

public class Paiement {
    private int id;
    private int commandeId;
    private double montant;
    private String methode; // carte_bancaire, paypal, virement, especes
    private String statut;  // approuve, refuse, en_attente, rembourse
    private LocalDateTime date;
    private String tokenStripe;
    private String referenceTransaction;

    public Paiement() {}

    public Paiement(int commandeId, double montant, String methode, String statut) {
        this.commandeId = commandeId;
        this.montant = montant;
        this.methode = methode;
        this.statut = statut;
        this.date = LocalDateTime.now();
    }

    // Getters
    public int getId() { return id; }
    public int getCommandeId() { return commandeId; }
    public double getMontant() { return montant; }
    public String getMethode() { return methode; }
    public String getStatut() { return statut; }
    public LocalDateTime getDate() { return date; }
    public String getTokenStripe() { return tokenStripe; }
    public String getReferenceTransaction() { return referenceTransaction; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCommandeId(int commandeId) { this.commandeId = commandeId; }
    public void setMontant(double montant) { this.montant = montant; }
    public void setMethode(String methode) { this.methode = methode; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setTokenStripe(String tokenStripe) { this.tokenStripe = tokenStripe; }
    public void setReferenceTransaction(String referenceTransaction) { this.referenceTransaction = referenceTransaction; }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", commandeId=" + commandeId +
                ", montant=" + montant +
                ", methode='" + methode + '\'' +
                ", statut='" + statut + '\'' +
                ", date=" + date +
                '}';
    }
}