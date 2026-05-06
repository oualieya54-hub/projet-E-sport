package org.example.Service;

import org.example.Utils.MyDatabase;
import org.example.Model.Evaluation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationService {

    // ── Helper ────────────────────────────────────────────────────────────────
    private Evaluation extract(ResultSet rs) throws SQLException {
        return new Evaluation(
                rs.getInt("id_evaluation"),
                rs.getInt("id_booking"),
                rs.getInt("note"),
                rs.getString("commentaire"),
                rs.getTimestamp("date_eval").toLocalDateTime()
        );
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    /** Soumettre une évaluation pour un booking (1 booking = 1 évaluation max). */
    public void create(Evaluation e) throws SQLException {
        String sql = "INSERT INTO evaluation (id_booking, note, commentaire, date_eval) VALUES (?, ?, ?, ?)";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getIdBooking());
            ps.setInt(2, e.getNote());
            ps.setString(3, e.getCommentaire());
            ps.setTimestamp(4, Timestamp.valueOf(e.getDateEval()));
            ps.executeUpdate();
            System.out.println("✅ Évaluation soumise !");
        }
    }

    public List<Evaluation> getAll() throws SQLException {
        List<Evaluation> liste = new ArrayList<>();
        String sql = "SELECT * FROM evaluation";
        try (Connection con = MyDatabase.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    public Evaluation getByBooking(int idBooking) throws SQLException {
        String sql = "SELECT * FROM evaluation WHERE id_booking = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBooking);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extract(rs);
        }
        return null;
    }

    public void update(Evaluation e) throws SQLException {
        String sql = "UPDATE evaluation SET note=?, commentaire=? WHERE id_evaluation=?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getNote());
            ps.setString(2, e.getCommentaire());
            ps.setInt(3, e.getIdEvaluation());
            ps.executeUpdate();
            System.out.println("✅ Évaluation mise à jour !");
        }
    }

    public void delete(int idEvaluation) throws SQLException {
        String sql = "DELETE FROM evaluation WHERE id_evaluation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEvaluation);
            ps.executeUpdate();
            System.out.println("✅ Évaluation supprimée !");
        }
    }

    // ── Métier ────────────────────────────────────────────────────────────────

    /** Calculer la note moyenne d'un coach (via ses sessions et bookings). */
    public double getNoteMoyenneCoach(int idCoach) throws SQLException {
        String sql = "SELECT AVG(e.note) " +
                     "FROM evaluation e " +
                     "JOIN booking b ON e.id_booking = b.id_booking " +
                     "JOIN session s  ON b.id_session = s.id_session " +
                     "WHERE s.id_coach = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCoach);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    /** Récupérer toutes les évaluations d'une formation. */
    public List<Evaluation> getByFormation(int idFormation) throws SQLException {
        List<Evaluation> liste = new ArrayList<>();
        String sql = "SELECT e.* " +
                     "FROM evaluation e " +
                     "JOIN booking b ON e.id_booking = b.id_booking " +
                     "JOIN session s  ON b.id_session = s.id_session " +
                     "WHERE s.id_formation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFormation);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    /** Vérifier si un booking a déjà été évalué — éviter les doublons. */
    public boolean hasAlreadyEvaluated(int idBooking) throws SQLException {
        String sql = "SELECT COUNT(*) FROM evaluation WHERE id_booking = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBooking);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    /** Soumettre en toute sécurité — bloque si déjà évalué. */
    public void createSafe(Evaluation e) throws SQLException {
        if (hasAlreadyEvaluated(e.getIdBooking())) {
            System.out.println("⚠️ Ce booking a déjà été évalué !");
        } else {
            create(e);
        }
    }
}
