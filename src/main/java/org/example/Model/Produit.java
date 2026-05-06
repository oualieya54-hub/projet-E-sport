package org.example.Model;

import java.time.LocalDateTime;

public class Produit {

    private int           idProduit;
    private String        nom;
    private String        description;
    private double        prix;
    private int           stock;
    private String        statut;       // disponible | rupture | archivé
    private String        typeProduit;  // equipement | jersey | cle_jeu | coaching | avatar_item
    private String        categorie;
    private Double        prixPromo;
    private LocalDateTime promoDebut;
    private LocalDateTime promoFin;
    private String        codePromo;
    private Double        reductionPct;
    private int           pointsGagnes;
    private LocalDateTime dateAjout;

    public Produit() {}

    public Produit(String nom, double prix, int stock, String typeProduit, String categorie, int pointsGagnes) {
        this.nom          = nom;
        this.prix         = prix;
        this.stock        = stock;
        this.typeProduit  = typeProduit;
        this.categorie    = categorie;
        this.pointsGagnes = pointsGagnes;
        this.statut       = "disponible";
    }

    // ── Getters & Setters ──────────────────────────────────────

    public int           getIdProduit()               { return idProduit; }
    public void          setIdProduit(int v)           { this.idProduit = v; }

    public String        getNom()                      { return nom; }
    public void          setNom(String v)              { this.nom = v; }

    public String        getDescription()              { return description; }
    public void          setDescription(String v)      { this.description = v; }

    public double        getPrix()                     { return prix; }
    public void          setPrix(double v)             { this.prix = v; }

    public int           getStock()                    { return stock; }
    public void          setStock(int v)               { this.stock = v; }

    public String        getStatut()                   { return statut; }
    public void          setStatut(String v)           { this.statut = v; }

    public String        getTypeProduit()              { return typeProduit; }
    public void          setTypeProduit(String v)      { this.typeProduit = v; }

    public String        getCategorie()                { return categorie; }
    public void          setCategorie(String v)        { this.categorie = v; }

    public Double        getPrixPromo()                { return prixPromo; }
    public void          setPrixPromo(Double v)        { this.prixPromo = v; }

    public LocalDateTime getPromoDebut()               { return promoDebut; }
    public void          setPromoDebut(LocalDateTime v){ this.promoDebut = v; }

    public LocalDateTime getPromoFin()                 { return promoFin; }
    public void          setPromoFin(LocalDateTime v)  { this.promoFin = v; }

    public String        getCodePromo()                { return codePromo; }
    public void          setCodePromo(String v)        { this.codePromo = v; }

    public Double        getReductionPct()             { return reductionPct; }
    public void          setReductionPct(Double v)     { this.reductionPct = v; }

    public int           getPointsGagnes()             { return pointsGagnes; }
    public void          setPointsGagnes(int v)        { this.pointsGagnes = v; }

    public LocalDateTime getDateAjout()                { return dateAjout; }
    public void          setDateAjout(LocalDateTime v) { this.dateAjout = v; }

    // ── Helpers ──────────────────────────────────────────────────

    /** Retourne le prix effectif (promo si active, sinon prix normal) */
    public double getPrixEffectif() {
        if (prixPromo != null && promoDebut != null && promoFin != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(promoDebut) && now.isBefore(promoFin)) {
                return prixPromo;
            }
        }
        return prix;
    }

    /** Retourne true si une promo est active en ce moment */
    public boolean isEnPromo() {
        return getPrixEffectif() < prix;
    }

    @Override
    public String toString() {
        return String.format("Produit{id=%d, nom='%s', prix=%.2f, stock=%d, statut='%s'}",
                idProduit, nom, prix, stock, statut);
    }
}