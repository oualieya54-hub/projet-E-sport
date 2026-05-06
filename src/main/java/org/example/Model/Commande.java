package org.example.Model;

import java.time.LocalDateTime;

public class Commande {

    private int           idCommande;
    private int           idUser;
    private double        montantTotal;
    private int           pointsUtilises;
    private int           pointsGagnes;
    private String        statut;   // en_attente | confirmée | expédiée | livrée | annulée
    private LocalDateTime dateCommande;
    private String        adresseLivraison;
    private String        methodePaiement;

    public Commande() {}

    public Commande(int idUser, double montantTotal, String adresseLivraison, String methodePaiement) {
        this.idUser           = idUser;
        this.montantTotal     = montantTotal;
        this.adresseLivraison = adresseLivraison;
        this.methodePaiement  = methodePaiement;
        this.statut           = "en_attente";
        this.pointsUtilises   = 0;
        this.pointsGagnes     = 0;
    }

    // ── Getters & Setters ──────────────────────────────────────

    public int           getIdCommande()                  { return idCommande; }
    public void          setIdCommande(int v)              { this.idCommande = v; }

    public int           getIdUser()                      { return idUser; }
    public void          setIdUser(int v)                  { this.idUser = v; }

    public double        getMontantTotal()                 { return montantTotal; }
    public void          setMontantTotal(double v)         { this.montantTotal = v; }

    public int           getPointsUtilises()               { return pointsUtilises; }
    public void          setPointsUtilises(int v)          { this.pointsUtilises = v; }

    public int           getPointsGagnes()                 { return pointsGagnes; }
    public void          setPointsGagnes(int v)            { this.pointsGagnes = v; }

    public String        getStatut()                      { return statut; }
    public void          setStatut(String v)               { this.statut = v; }

    public LocalDateTime getDateCommande()                 { return dateCommande; }
    public void          setDateCommande(LocalDateTime v)  { this.dateCommande = v; }

    public String        getAdresseLivraison()             { return adresseLivraison; }
    public void          setAdresseLivraison(String v)     { this.adresseLivraison = v; }

    public String        getMethodePaiement()              { return methodePaiement; }
    public void          setMethodePaiement(String v)      { this.methodePaiement = v; }

    @Override
    public String toString() {
        return String.format("Commande{id=%d, user=%d, montant=%.2f, statut='%s'}",
                idCommande, idUser, montantTotal, statut);
    }
}