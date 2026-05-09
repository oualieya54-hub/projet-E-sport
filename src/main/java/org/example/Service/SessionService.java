package org.example.Service;

import org.example.Utils.MyDatabase;
import org.example.Model.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class SessionService {

    public void create(Session s) throws SQLException {
        String sql = "INSERT INTO Session (date_heure, jeu, prix, id_coach, duree_minutes, capacite_max, type_session, statut, id_formation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(s.getDateHeure()));
            ps.setString(2, s.getJeu());
            ps.setFloat(3, s.getPrix());
            ps.setInt(4, s.getIdCoach());
            ps.setInt(5, s.getDureeMinutes());
            ps.setInt(6, s.getCapaciteMax());
            ps.setString(7, s.getTypeSession());
            ps.setString(8, s.getStatut());
            if (s.getIdFormation() != null) {
                ps.setInt(9, s.getIdFormation());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.executeUpdate();
            System.out.println("✅ Session créée !");
        }
    }

    private Session extractSessionFromResultSet(ResultSet rs) throws SQLException {
        int idForm = rs.getInt("id_formation");
        Integer idFormation = rs.wasNull() ? null : idForm;
        
        return new Session(
                rs.getInt("id_session"),
                rs.getTimestamp("date_heure").toLocalDateTime(),
                rs.getString("jeu"),
                rs.getFloat("prix"),
                rs.getInt("id_coach"),
                rs.getInt("duree_minutes"),
                rs.getInt("capacite_max"),
                rs.getString("type_session"),
                rs.getString("statut"),
                idFormation
        );
    }

    public List<Session> getDisponibilites() throws SQLException {
        List<Session> liste = new ArrayList<>();
        String sql = "SELECT * FROM Session";
        try (Connection con = MyDatabase.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(extractSessionFromResultSet(rs));
            }
        }
        return liste;
    }

    public Session getById(int idSession) throws SQLException {
        String sql = "SELECT * FROM session WHERE id_session = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSession);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractSessionFromResultSet(rs);
        }
        return null;
    }

    public void update(Session s) throws SQLException {
        String sql = "UPDATE Session SET date_heure=?, jeu=?, prix=?, id_coach=?, duree_minutes=?, capacite_max=?, type_session=?, statut=?, id_formation=? WHERE id_session=?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(s.getDateHeure()));
            ps.setString(2, s.getJeu());
            ps.setFloat(3, s.getPrix());
            ps.setInt(4, s.getIdCoach());
            ps.setInt(5, s.getDureeMinutes());
            ps.setInt(6, s.getCapaciteMax());
            ps.setString(7, s.getTypeSession());
            ps.setString(8, s.getStatut());
            if (s.getIdFormation() != null) {
                ps.setInt(9, s.getIdFormation());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.setInt(10, s.getIdSession());
            ps.executeUpdate();
            System.out.println("✅ Session mise à jour !");
        }
    }

    public void cancel(int idSession) throws SQLException {
        String sql = "DELETE FROM Session WHERE id_session = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSession);
            ps.executeUpdate();
            System.out.println("✅ Session annulée !");
        }
    }

    //Métier :Récupérer toutes les sessions d'un coach spécifique — utile pour afficher son planning.
    public List<Session> getSessionsByCoach(int idCoach) throws SQLException {
        List<Session> liste = new ArrayList<>();
        String sql = "SELECT * FROM Session WHERE id_coach = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCoach);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(extractSessionFromResultSet(rs));
            }
        }
        return liste;
    }

    //Metier:Filtrer les sessions par jeu (Fortnite, League of Legends...).
    public List<Session> getSessionsByJeu(String jeu) throws SQLException {
        List<Session> liste = new ArrayList<>();
        String sql = "SELECT * FROM Session WHERE jeu = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, jeu);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(extractSessionFromResultSet(rs));
            }
        }
        return liste;
    }

    //Metier:Retourner uniquement les sessions dont la date est dans le futur — éviter d'afficher des sessions passées.
    public List<Session> getSessionsFutures() throws SQLException {
        List<Session> liste = new ArrayList<>();
        String sql = "SELECT * FROM Session WHERE date_heure > NOW() ORDER BY date_heure ASC";
        try (Connection con = MyDatabase.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) { 
                liste.add(extractSessionFromResultSet(rs));
            }
        }
        return liste;
    }

    //Métier :Vérifier si un coach est libre à un créneau donné avant de créer une session — éviter les conflits d'horaire.
    public boolean isCoachDisponible(int idCoach, LocalDateTime dateHeure) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Session WHERE id_coach = ? AND date_heure = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCoach);
            ps.setTimestamp(2, Timestamp.valueOf(dateHeure));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        }
        return false;
    }
    //Métier : Récupérer toutes les sessions liées à une formation spécifique.
    public List<Session> getSessionsByFormation(int idFormation) throws SQLException {
        List<Session> liste = new ArrayList<>();
        String sql = "SELECT * FROM Session WHERE id_formation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFormation);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(extractSessionFromResultSet(rs));
            }
        }
        return liste;
    }
}
//.
