package org.example.modele;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class commande {

    private int           idCommande;
    private int           idUser;
    private BigDecimal    montantTotal;
    private int           pointsUtilises;
    private String        statut;
    private LocalDateTime dateCommande;
    private String        adresseLivraison;
    private String        methodePaiement;

    public commande() {}

    public commande(int idUser, BigDecimal montantTotal, int pointsUtilises,
                    String adresseLivraison, String methodePaiement) {
        this.idUser           = idUser;
        this.montantTotal     = montantTotal;
        this.pointsUtilises   = pointsUtilises;
        this.adresseLivraison = adresseLivraison;
        this.methodePaiement  = methodePaiement;
        this.statut           = "en_attente";
    }

    public int getIdCommande()                  { return idCommande; }
    public void setIdCommande(int id)           { this.idCommande = id; }

    public int getIdUser()                      { return idUser; }
    public void setIdUser(int id)               { this.idUser = id; }

    public BigDecimal getMontantTotal()         { return montantTotal; }
    public void setMontantTotal(BigDecimal m)   { this.montantTotal = m; }

    public int getPointsUtilises()              { return pointsUtilises; }
    public void setPointsUtilises(int pts)      { this.pointsUtilises = pts; }

    public String getStatut()                   { return statut; }
    public void setStatut(String statut)        { this.statut = statut; }

    public LocalDateTime getDateCommande()      { return dateCommande; }
    public void setDateCommande(LocalDateTime d){ this.dateCommande = d; }

    public String getAdresseLivraison()         { return adresseLivraison; }
    public void setAdresseLivraison(String a)   { this.adresseLivraison = a; }

    public String getMethodePaiement()          { return methodePaiement; }
    public void setMethodePaiement(String m)    { this.methodePaiement = m; }

    @Override
    public String toString() {
        return "Commande{id=" + idCommande +
                ", user=" + idUser +
                ", total=" + montantTotal +
                ", statut='" + statut + "'}";
    }
}
