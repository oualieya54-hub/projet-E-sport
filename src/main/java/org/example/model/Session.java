package org.example.Model;

import java.time.LocalDateTime;

public class Session {
    private int idSession;
    private LocalDateTime dateHeure;
    private String jeu;
    private float prix;
    private int idCoach;
    private int dureeMinutes;
    private int capaciteMax;
    private String typeSession;
    private String statut;
    private Integer idFormation; // Can be null as per DB

    public Session(int idSession, LocalDateTime dateHeure, String jeu, float prix, int idCoach, int dureeMinutes, int capaciteMax, String typeSession, String statut, Integer idFormation) {
        this.idSession    = idSession;
        this.dateHeure    = dateHeure;
        this.jeu          = jeu;
        this.prix         = prix;
        this.idCoach      = idCoach;
        this.dureeMinutes = dureeMinutes;
        this.capaciteMax  = capaciteMax;
        this.typeSession  = typeSession;
        this.statut       = statut;
        this.idFormation  = idFormation;
    }

    // Additional constructor for easier object creation before DB insert
    public Session(int idSession, LocalDateTime dateHeure, String jeu, float prix, int idCoach) {
        this.idSession    = idSession;
        this.dateHeure    = dateHeure;
        this.jeu          = jeu;
        this.prix         = prix;
        this.idCoach      = idCoach;
        this.dureeMinutes = 60; // Default as per DB
        this.capaciteMax  = 10; // Default as per DB
        this.typeSession  = "groupe"; // Default as per DB (individuel/groupe/atelier)
        this.statut       = "ouverte"; // Default as per DB (ouverte/complète/annulée)
        this.idFormation  = null; // Default as per DB
    }

    public int getIdSession()           { return idSession; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public String getJeu()              { return jeu; }
    public float getPrix()              { return prix; }
    public int getIdCoach()             { return idCoach; }
    public int getDureeMinutes()        { return dureeMinutes; }
    public int getCapaciteMax()         { return capaciteMax; }
    public String getTypeSession()      { return typeSession; }
    public String getStatut()           { return statut; }
    public Integer getIdFormation()     { return idFormation; }

    // Setters
    public void setIdSession(int idSession)           { this.idSession = idSession; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    public void setJeu(String jeu)                    { this.jeu = jeu; }
    public void setPrix(float prix)                   { this.prix = prix; }
    public void setIdCoach(int idCoach)               { this.idCoach = idCoach; }
    public void setDureeMinutes(int dureeMinutes)     { this.dureeMinutes = dureeMinutes; }
    public void setCapaciteMax(int capaciteMax)       { this.capaciteMax = capaciteMax; }
    public void setTypeSession(String typeSession)    { this.typeSession = typeSession; }
    public void setStatut(String statut)              { this.statut = statut; }
    public void setIdFormation(Integer idFormation)   { this.idFormation = idFormation; }

    @Override
    public String toString() {
        return "Session { id=" + idSession + ", jeu='" + jeu + "', prix=" + prix + ", statut='" + statut + "', type='" + typeSession + "' }";
    }
}
