package models;

import java.util.Date;

public class Team {
    private int id_team;
    private String nom_equipe;
    private String logo;
    private Date date_creation;
    private int id_capitaine;

    public Team() {}

    public Team(int id_team, String nom_equipe, String logo, Date date_creation, int id_capitaine) {
        this.id_team = id_team;
        this.nom_equipe = nom_equipe;
        this.logo = logo;
        this.date_creation = date_creation;
        this.id_capitaine = id_capitaine;
    }

    public int getId_team() { return id_team; }
    public void setId_team(int id_team) { this.id_team = id_team; }

    public String getNom_equipe() { return nom_equipe; }
    public void setNom_equipe(String nom_equipe) { this.nom_equipe = nom_equipe; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public Date getDate_creation() { return date_creation; }
    public void setDate_creation(Date date_creation) { this.date_creation = date_creation; }

    public int getId_capitaine() { return id_capitaine; }
    public void setId_capitaine(int id_capitaine) { this.id_capitaine = id_capitaine; }

    @Override
    public String toString() {
        return "Team{" +
                "id_team=" + id_team +
                ", nom_equipe='" + nom_equipe + '\'' +
                ", logo='" + logo + '\'' +
                ", date_creation=" + date_creation +
                ", id_capitaine=" + id_capitaine +
                "}\n";
    }
}
