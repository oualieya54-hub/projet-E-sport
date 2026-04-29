package org.example.modele;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Facture {
    private int idFacture;
    private String numeroFacture;
    private int idCommande;
    private LocalDateTime dateFacture;
    private LocalDateTime dateEnvoi;
    private BigDecimal montantTotal;
    private BigDecimal montantTVA;
    private BigDecimal montantHT;
    private String statut;          // generee, envoyee, remboursee, annulee
    private String adresseLivraison;
    private boolean envoyeeEmail;
    private String cheminPDF;

    public Facture() {
        this.dateFacture = LocalDateTime.now();
        this.statut = "generee";
        this.envoyeeEmail = false;
    }

    public Facture(int idCommande, BigDecimal montantTotal, String adresseLivraison) {
        this();
        this.idCommande = idCommande;
        this.montantTotal = montantTotal;
        this.adresseLivraison = adresseLivraison;
        this.numeroFacture = "FAC-" + idCommande + "-" + System.currentTimeMillis();
        calculerTVA();
    }

    private void calculerTVA() {
        this.montantTVA = montantTotal.multiply(BigDecimal.valueOf(0.20)); // 20% TVA
        this.montantHT = montantTotal.subtract(montantTVA);
    }

    // Getters
    public int getIdFacture()                       { return idFacture; }
    public String getNumeroFacture()                { return numeroFacture; }
    public int getIdCommande()                      { return idCommande; }
    public LocalDateTime getDateFacture()           { return dateFacture; }
    public LocalDateTime getDateEnvoi()             { return dateEnvoi; }
    public BigDecimal getMontantTotal()             { return montantTotal; }
    public BigDecimal getMontantTVA()               { return montantTVA; }
    public BigDecimal getMontantHT()                { return montantHT; }
    public String getStatut()                       { return statut; }
    public String getAdresseLivraison()             { return adresseLivraison; }
    public boolean isEnvoyeeEmail()                 { return envoyeeEmail; }
    public String getCheminPDF()                    { return cheminPDF; }

    // Setters
    public void setIdFacture(int id)                { this.idFacture = id; }
    public void setNumeroFacture(String n)          { this.numeroFacture = n; }
    public void setIdCommande(int id)               { this.idCommande = id; }
    public void setDateFacture(LocalDateTime d)     { this.dateFacture = d; }
    public void setDateEnvoi(LocalDateTime d)       { this.dateEnvoi = d; }
    public void setMontantTotal(BigDecimal m)       { this.montantTotal = m; calculerTVA(); }
    public void setMontantTVA(BigDecimal m)         { this.montantTVA = m; }
    public void setMontantHT(BigDecimal m)          { this.montantHT = m; }
    public void setStatut(String s)                 { this.statut = s; }
    public void setAdresseLivraison(String a)       { this.adresseLivraison = a; }
    public void setEnvoyeeEmail(boolean e)          { this.envoyeeEmail = e; }
    public void setCheminPDF(String p)              { this.cheminPDF = p; }

    @Override
    public String toString() {
        return "Facture{" +
                "id=" + idFacture +
                ", numero='" + numeroFacture + '\'' +
                ", commande=" + idCommande +
                ", montant=" + montantTotal +
                ", statut='" + statut + '\'' +
                '}';
    }
}