package org.example.model;

import java.time.LocalDateTime;

public class Session {
    private int idSession;
    private LocalDateTime dateHeure;
    private String jeu;
    private float prix;
    private int idCoach;

    public Session(int idSession, LocalDateTime dateHeure, String jeu, float prix, int idCoach) {
        this.idSession = idSession;
        this.dateHeure = dateHeure;
        this.jeu       = jeu;
        this.prix      = prix;
        this.idCoach   = idCoach;
    }

    public int getIdSession()           { return idSession; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public String getJeu()              { return jeu; }
    public float getPrix()              { return prix; }
    public int getIdCoach()             { return idCoach; }

    @Override
    public String toString() {
        return "Session { id=" + idSession + ", jeu='" + jeu + "', prix=" + prix + " }";
    }
}