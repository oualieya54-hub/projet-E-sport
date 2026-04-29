package org.example.modele;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Paiement {
    private int idPaiement;
    private int idCommande;
    private BigDecimal montant;
    private String methode;         // carte_bancaire, paypal, virement, especes
    private String statut;          // approuve, refuse, en_attente, rembourse
    private LocalDateTime datePaiement;
    private String tokenStripe;
    private String referenceTransaction;

    public Paiement() {}

    public Paiement(int idCommande, BigDecimal montant, String methode, String statut) {
        this.idCommande = idCommande;
        this.montant = montant;
        this.methode = methode;
        this.statut = statut;
        this.datePaiement = LocalDateTime.now();
    }

    // Getters
    public int getIdPaiement()                      { return idPaiement; }
    public int getIdCommande()                      { return idCommande; }
    public BigDecimal getMontant()                  { return montant; }
    public String getMethode()                      { return methode; }
    public String getStatut()                       { return statut; }
    public LocalDateTime getDatePaiement()          { return datePaiement; }
    public String getTokenStripe()                  { return tokenStripe; }
    public String getReferenceTransaction()         { return referenceTransaction; }

    // Setters
    public void setIdPaiement(int id)               { this.idPaiement = id; }
    public void setIdCommande(int id)               { this.idCommande = id; }
    public void setMontant(BigDecimal m)            { this.montant = m; }
    public void setMethode(String m)                { this.methode = m; }
    public void setStatut(String s)                 { this.statut = s; }
    public void setDatePaiement(LocalDateTime d)    { this.datePaiement = d; }
    public void setTokenStripe(String t)            { this.tokenStripe = t; }
    public void setReferenceTransaction(String r)   { this.referenceTransaction = r; }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + idPaiement +
                ", commande=" + idCommande +
                ", montant=" + montant +
                ", methode='" + methode + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}