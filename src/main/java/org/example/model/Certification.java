package org.example.Model;

import java.time.LocalDate;

public class Certification {
    private int       idCertification;
    private int       idEleve;
    private int       idFormation;
    private String    niveauObtenu;  // enum: 'Bronze','Silver','Gold','Pro'
    private float     scoreFinal;
    private LocalDate dateObtention;

    // Full constructor (used when reading from DB)
    public Certification(int idCertification, int idEleve, int idFormation,
                         String niveauObtenu, float scoreFinal, LocalDate dateObtention) {
        this.idCertification = idCertification;
        this.idEleve         = idEleve;
        this.idFormation     = idFormation;
        this.niveauObtenu    = niveauObtenu;
        this.scoreFinal      = scoreFinal;
        this.dateObtention   = dateObtention;
    }

    // Convenience constructor (date defaults to today as per DB)
    public Certification(int idEleve, int idFormation, String niveauObtenu, float scoreFinal) {
        this.idCertification = 0;
        this.idEleve         = idEleve;
        this.idFormation     = idFormation;
        this.niveauObtenu    = niveauObtenu;
        this.scoreFinal      = scoreFinal;
        this.dateObtention   = LocalDate.now();
    }

    // Getters
    public int       getIdCertification() { return idCertification; }
    public int       getIdEleve()         { return idEleve; }
    public int       getIdFormation()     { return idFormation; }
    public String    getNiveauObtenu()    { return niveauObtenu; }
    public float     getScoreFinal()      { return scoreFinal; }
    public LocalDate getDateObtention()   { return dateObtention; }

    // Setters
    public void setIdCertification(int idCertification)   { this.idCertification = idCertification; }
    public void setIdEleve(int idEleve)                    { this.idEleve = idEleve; }
    public void setIdFormation(int idFormation)            { this.idFormation = idFormation; }
    public void setNiveauObtenu(String niveauObtenu)       { this.niveauObtenu = niveauObtenu; }
    public void setScoreFinal(float scoreFinal)            { this.scoreFinal = scoreFinal; }
    public void setDateObtention(LocalDate dateObtention)  { this.dateObtention = dateObtention; }

    @Override
    public String toString() {
        return "Certification { id=" + idCertification + ", idEleve=" + idEleve +
               ", idFormation=" + idFormation + ", niveau='" + niveauObtenu +
               "', score=" + scoreFinal + ", date=" + dateObtention + " }";
    }
}
