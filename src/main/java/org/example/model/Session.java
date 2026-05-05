package org.example.Model;

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

    // Setters
    public void setIdSession(int idSession)           { this.idSession = idSession; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    public void setJeu(String jeu)                    { this.jeu = jeu; }
    public void setPrix(float prix)                   { this.prix = prix; }
    public void setIdCoach(int idCoach)               { this.idCoach = idCoach; }

    @Override
    public String toString() {
        return "Session { id=" + idSession + ", jeu='" + jeu + "', prix=" + prix + " }";
    }

}
