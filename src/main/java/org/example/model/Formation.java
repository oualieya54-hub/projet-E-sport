package org.example.Model;

import java.time.LocalDate;

public class Formation {
    private int    idFormation;
    private String titre;
    private String description;
    private String jeu;
    private String niveau;        // enum: 'débutant','intermédiaire','avancé'
    private int    dureeSemaines;
    private float  prix;
    private int    idCoach;
    private LocalDate dateDebut;
    private String statut;        // enum: 'active','archivée','brouillon'
    private int    nombreSessions;

    // Full constructor
    public Formation(int idFormation, String titre, String description, String jeu,
                     String niveau, int dureeSemaines, float prix, int idCoach,
                     LocalDate dateDebut, String statut, int nombreSessions) {
        this.idFormation   = idFormation;
        this.titre         = titre;
        this.description   = description;
        this.jeu           = jeu;
        this.niveau        = niveau;
        this.dureeSemaines = dureeSemaines;
        this.prix          = prix;
        this.idCoach       = idCoach;
        this.dateDebut     = dateDebut;
        this.statut        = statut;
        this.nombreSessions = nombreSessions;
    }

    // Convenience constructor (defaults from DB)
    public Formation(String titre, String jeu, String niveau, float prix, int idCoach) {
        this.idFormation   = 0;
        this.titre         = titre;
        this.description   = null;
        this.jeu           = jeu;
        this.niveau        = niveau;
        this.dureeSemaines = 4;           // default
        this.prix          = prix;
        this.idCoach       = idCoach;
        this.dateDebut     = null;
        this.statut        = "brouillon"; // default
        this.nombreSessions = 1;          // default
    }

    // Getters
    public int       getIdFormation()   { return idFormation; }
    public String    getTitre()         { return titre; }
    public String    getDescription()   { return description; }
    public String    getJeu()           { return jeu; }
    public String    getNiveau()        { return niveau; }
    public int       getDureeSemaines() { return dureeSemaines; }
    public float     getPrix()          { return prix; }
    public int       getIdCoach()       { return idCoach; }
    public LocalDate getDateDebut()     { return dateDebut; }
    public String    getStatut()        { return statut; }
    public int       getNombreSessions(){ return nombreSessions; }

    // Setters
    public void setIdFormation(int idFormation)        { this.idFormation = idFormation; }
    public void setTitre(String titre)                 { this.titre = titre; }
    public void setDescription(String description)     { this.description = description; }
    public void setJeu(String jeu)                     { this.jeu = jeu; }
    public void setNiveau(String niveau)               { this.niveau = niveau; }
    public void setDureeSemaines(int dureeSemaines)    { this.dureeSemaines = dureeSemaines; }
    public void setPrix(float prix)                    { this.prix = prix; }
    public void setIdCoach(int idCoach)                { this.idCoach = idCoach; }
    public void setDateDebut(LocalDate dateDebut)      { this.dateDebut = dateDebut; }
    public void setStatut(String statut)               { this.statut = statut; }
    public void setNombreSessions(int nombreSessions)  { this.nombreSessions = nombreSessions; }

    @Override
    public String toString() {
        return "Formation { id=" + idFormation + ", titre='" + titre + "', jeu='" + jeu +
               "', niveau='" + niveau + "', statut='" + statut + "', prix=" + prix + " }";
    }
}
