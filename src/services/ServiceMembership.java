package services;

import interfaces.IService;
import models.Membership;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceMembership implements IService<Membership> {

    @Override
    public void add(Membership m) {
        String req = "INSERT INTO `membership`(`id_team`, `id_user`, `role_dans_equipe`) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, m.getId_team());
            ps.setInt(2, m.getId_user());
            ps.setString(3, m.getRole_dans_equipe());
            ps.executeUpdate();
            System.out.println("Membre ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Membership> getAll() {
        List<Membership> memberships = new ArrayList<>();
        String req = "SELECT * FROM `membership`";
        try {
            Statement stm = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Membership m = new Membership();
                m.setId_membership(rs.getInt("id_membership"));
                m.setId_team(rs.getInt("id_team"));
                m.setId_user(rs.getInt("id_user"));
                m.setRole_dans_equipe(rs.getString("role_dans_equipe"));
                memberships.add(m);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return memberships;
    }

    @Override
    public void delete(Membership m) {
        String req = "DELETE FROM `membership` WHERE `id_membership` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, m.getId_membership());
            ps.executeUpdate();
            System.out.println("Membre supprimé avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Membership m) {
        String req = "UPDATE `membership` SET `id_team`=?, `id_user`=?, `role_dans_equipe`=? WHERE `id_membership`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, m.getId_team());
            ps.setInt(2, m.getId_user());
            ps.setString(3, m.getRole_dans_equipe());
            ps.setInt(4, m.getId_membership());
            ps.executeUpdate();
            System.out.println("Membership mis à jour !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void join(int id_team, int id_user, String role) {
        Membership m = new Membership(0, id_team, id_user, role);
        add(m);
    }


    public void changeRole(Membership m, String newRole) {
        m.setRole_dans_equipe(newRole);
        update(m);
    }
    // Recherche par ID
    public Membership getById(int id) {
        String req = "SELECT * FROM `membership` WHERE `id_membership` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Membership m = new Membership();
                m.setId_membership(rs.getInt("id_membership"));
                m.setId_team(rs.getInt("id_team"));
                m.setId_user(rs.getInt("id_user"));
                m.setRole_dans_equipe(rs.getString("role_dans_equipe"));
                return m;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    // Recherche par rôle
    public List<Membership> getByRole(String role) {
        List<Membership> list = new ArrayList<>();
        String req = "SELECT * FROM `membership` WHERE `role_dans_equipe` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, role);
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



}
