package services;

import interfaces.IService;
import models.Membership;
import models.Team;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTeam implements IService<Team> {

    @Override
    public void add(Team t) {
        String req = "INSERT INTO `team`(`nom_equipe`, `logo`, `date_creation`, `id_capitaine`) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, t.getNom_equipe());
            ps.setString(2, t.getLogo());
            ps.setDate(3, new java.sql.Date(t.getDate_creation().getTime()));
            ps.setInt(4, t.getId_capitaine());
            ps.executeUpdate();
            System.out.println("Team ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Team> getAll() {
        List<Team> teams = new ArrayList<>();
        String req = "SELECT * FROM `team`";
        try {
            Statement stm = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Team t = new Team();
                t.setId_team(rs.getInt("id_team"));
                t.setNom_equipe(rs.getString("nom_equipe"));
                t.setLogo(rs.getString("logo"));
                t.setDate_creation(rs.getDate("date_creation"));
                t.setId_capitaine(rs.getInt("id_capitaine"));
                teams.add(t);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return teams;
    }

    @Override
    public void delete(Team t) {
        String req = "DELETE FROM `team` WHERE `id_team` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, t.getId_team());
            ps.executeUpdate();
            System.out.println("Team supprimée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Team t) {
        String req = "UPDATE `team` SET `nom_equipe`=?, `logo`=?, `date_creation`=?, `id_capitaine`=? WHERE `id_team`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, t.getNom_equipe());
            ps.setString(2, t.getLogo());
            ps.setDate(3, new java.sql.Date(t.getDate_creation().getTime()));
            ps.setInt(4, t.getId_capitaine());
            ps.setInt(5, t.getId_team());
            ps.executeUpdate();
            System.out.println("Team mise à jour avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void disband(Team t) {
        String reqMemberships = "DELETE FROM `membership` WHERE `id_team` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(reqMemberships);
            ps.setInt(1, t.getId_team());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        delete(t);
        System.out.println("Équipe dissoute !");
    }
    // Recherche par ID
    public Team getById(int id) {
        String req = "SELECT * FROM `team` WHERE `id_team` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Team t = new Team();
                t.setId_team(rs.getInt("id_team"));
                t.setNom_equipe(rs.getString("nom_equipe"));
                t.setLogo(rs.getString("logo"));
                t.setDate_creation(rs.getDate("date_creation"));
                t.setId_capitaine(rs.getInt("id_capitaine"));
                return t;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Team getByName(String nom) {
        String req = "SELECT * FROM `team` WHERE `nom_equipe` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Team t = new Team();
                t.setId_team(rs.getInt("id_team"));
                t.setNom_equipe(rs.getString("nom_equipe"));
                t.setLogo(rs.getString("logo"));
                t.setDate_creation(rs.getDate("date_creation"));
                t.setId_capitaine(rs.getInt("id_capitaine"));
                return t;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    // Tous les memberships d'une team
    public List<Membership> getMembershipsOfTeam(int id_team) {
        List<Membership> list = new ArrayList<>();
        String req = "SELECT * FROM `membership` WHERE `id_team` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, id_team);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Membership m = new Membership();
                m.setId_membership(rs.getInt("id_membership"));
                m.setId_team(rs.getInt("id_team"));
                m.setId_user(rs.getInt("id_user"));
                m.setRole_dans_equipe(rs.getString("role_dans_equipe"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
    // Changer le capitaine d'une team
    public void changerCapitaine(int id_team, int nouvelId_capitaine) {
        String req = "UPDATE `team` SET `id_capitaine` = ? WHERE `id_team` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, nouvelId_capitaine);
            ps.setInt(2, id_team);
            ps.executeUpdate();
            System.out.println("✅ Capitaine changé !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
    // Compter le nombre de membres d'une team
    public int countMembers(int id_team) {
        String req = "SELECT COUNT(*) FROM `membership` WHERE `id_team` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, id_team);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
    // Vérifier si une team est pleine (max 11 joueurs)
    public boolean isTeamFull(int id_team) {
        return countMembers(id_team) >= 11;
    }
    // Trier les teams par date de création
    public List<Team> getTeamsSortedByDate() {
        List<Team> list = new ArrayList<>();
        String req = "SELECT * FROM `team` ORDER BY `date_creation` ASC";
        try {
            Statement stm = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Team t = new Team();
                t.setId_team(rs.getInt("id_team"));
                t.setNom_equipe(rs.getString("nom_equipe"));
                t.setLogo(rs.getString("logo"));
                t.setDate_creation(rs.getDate("date_creation"));
                t.setId_capitaine(rs.getInt("id_capitaine"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }



}
