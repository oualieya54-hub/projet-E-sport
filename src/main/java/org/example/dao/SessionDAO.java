package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public void create(Session s) throws SQLException {
        String sql = "INSERT INTO Session (date_heure, jeu, prix, id_coach) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(s.getDateHeure()));
            ps.setString(2, s.getJeu());
            ps.setFloat(3, s.getPrix());
            ps.setInt(4, s.getIdCoach());
            ps.executeUpdate();
            System.out.println("✅ Session créée !");
        }
    }

    public List<Session> getDisponibilites() throws SQLException {
        List<Session> liste = new ArrayList<>();
        String sql = "SELECT * FROM Session";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Session(
                        rs.getInt("id_session"),
                        rs.getTimestamp("date_heure").toLocalDateTime(),
                        rs.getString("jeu"),
                        rs.getFloat("prix"),
                        rs.getInt("id_coach")
                ));
            }
        }
        return liste;
    }

    public void update(Session s) throws SQLException {
        String sql = "UPDATE Session SET date_heure=?, jeu=?, prix=?, id_coach=? WHERE id_session=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(s.getDateHeure()));
            ps.setString(2, s.getJeu());
            ps.setFloat(3, s.getPrix());
            ps.setInt(4, s.getIdCoach());
            ps.setInt(5, s.getIdSession());
            ps.executeUpdate();
            System.out.println("✅ Session mise à jour !");
        }
    }

    public void cancel(int idSession) throws SQLException {
        String sql = "DELETE FROM Session WHERE id_session = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSession);
            ps.executeUpdate();
            System.out.println("✅ Session annulée !");
        }
    }
}