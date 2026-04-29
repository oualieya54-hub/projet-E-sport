package models;

public class Membership {
    private int id_membership;
    private int id_team;
    private int id_user;
    private String role_dans_equipe;

    public Membership() {}

    public Membership(int id_membership, int id_team, int id_user, String role_dans_equipe) {
        this.id_membership = id_membership;
        this.id_team = id_team;
        this.id_user = id_user;
        this.role_dans_equipe = role_dans_equipe;
    }

    public int getId_membership() { return id_membership; }
    public void setId_membership(int id_membership) { this.id_membership = id_membership; }

    public int getId_team() { return id_team; }
    public void setId_team(int id_team) { this.id_team = id_team; }

    public int getId_user() { return id_user; }
    public void setId_user(int id_user) { this.id_user = id_user; }

    public String getRole_dans_equipe() { return role_dans_equipe; }
    public void setRole_dans_equipe(String role_dans_equipe) { this.role_dans_equipe = role_dans_equipe; }

    @Override
    public String toString() {
        return "Membership{" +
                "id_membership=" + id_membership +
                ", id_team=" + id_team +
                ", id_user=" + id_user +
                ", role_dans_equipe='" + role_dans_equipe + '\'' +
                "}\n";
    }
}
