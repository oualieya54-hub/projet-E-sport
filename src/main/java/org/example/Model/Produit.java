package org.example.modele;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class produit {

    private int         idProduit;
    private int         idCategorie;
    private String      nom;
    private String      description;
    private BigDecimal  prix;
    private int         stock;
    private String      imageUrl;
    private String      statut;
    private String      typeProduit;
    private int         pointsGagnes;
    private LocalDateTime dateAjout;


    public produit() {}

    public produit(int idCategorie, String nom, String description,
                   BigDecimal prix, int stock, String imageUrl,
                   String statut, String typeProduit, int pointsGagnes) {
        this.idCategorie  = idCategorie;
        this.nom          = nom;
        this.description  = description;
        this.prix         = prix;
        this.stock        = stock;
        this.imageUrl     = imageUrl;
        this.statut       = statut;
        this.typeProduit  = typeProduit;
        this.pointsGagnes = pointsGagnes;
    }


    public int getIdProduit()               { return idProduit; }
    public void setIdProduit(int id)        { this.idProduit = id; }

    public int getIdCategorie()             { return idCategorie; }
    public void setIdCategorie(int id)      { this.idCategorie = id; }

    public String getNom()                  { return nom; }
    public void setNom(String nom)          { this.nom = nom; }

    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }

    public BigDecimal getPrix()             { return prix; }
    public void setPrix(BigDecimal prix)    { this.prix = prix; }

    public int getStock()                   { return stock; }
    public void setStock(int stock)         { this.stock = stock; }

    public String getImageUrl()             { return imageUrl; }
    public void setImageUrl(String url)     { this.imageUrl = url; }

    public String getStatut()               { return statut; }
    public void setStatut(String statut)    { this.statut = statut; }

    public String getTypeProduit()          { return typeProduit; }
    public void setTypeProduit(String t)    { this.typeProduit = t; }

    public int getPointsGagnes()            { return pointsGagnes; }
    public void setPointsGagnes(int pts)    { this.pointsGagnes = pts; }

    public LocalDateTime getDateAjout()     { return dateAjout; }
    public void setDateAjout(LocalDateTime d){ this.dateAjout = d; }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + idProduit +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", stock=" + stock +
                ", statut='" + statut + '\'' +
                '}';
    }
}
