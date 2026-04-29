package org.example.modele;

public class catégorie {

    private int    idCategorie;
    private String nom;
    private String description;
    private String iconeUrl;
    private String typeCategorie;

    public catégorie() {}

    public catégorie(String nom, String description, String iconeUrl, String typeCategorie) {
        this.nom           = nom;
        this.description   = description;
        this.iconeUrl      = iconeUrl;
        this.typeCategorie = typeCategorie;
    }

    public int    getIdCategorie()              { return idCategorie; }
    public void   setIdCategorie(int id)        { this.idCategorie = id; }
    public String getNom()                      { return nom; }
    public void   setNom(String nom)            { this.nom = nom; }
    public String getDescription()              { return description; }
    public void   setDescription(String d)      { this.description = d; }
    public String getIconeUrl()                 { return iconeUrl; }
    public void   setIconeUrl(String url)       { this.iconeUrl = url; }
    public String getTypeCategorie()            { return typeCategorie; }
    public void   setTypeCategorie(String t)    { this.typeCategorie = t; }

    @Override
    public String toString() { return nom; }
}