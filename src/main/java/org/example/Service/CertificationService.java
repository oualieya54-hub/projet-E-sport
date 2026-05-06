package org.example.Service;

import org.example.Utils.MyDatabase;
import org.example.Model.Certification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificationService {

    // ── Helper ────────────────────────────────────────────────────────────────
    private Certification extract(ResultSet rs) throws SQLException {
        return new Certification(
                rs.getInt("id_certification"),
                rs.getInt("id_eleve"),
                rs.getInt("id_formation"),
                rs.getString("niveau_obtenu"),
                rs.getFloat("score_final"),
                rs.getDate("date_obtention").toLocalDate()
        );
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    /** Délivrer une certification à un élève pour une formation. */
    public void create(Certification c) throws SQLException {
        String sql = "INSERT INTO certification (id_eleve, id_formation, niveau_obtenu, score_final, date_obtention) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getIdEleve());
            ps.setInt(2, c.getIdFormation());
            ps.setString(3, c.getNiveauObtenu());
            ps.setFloat(4, c.getScoreFinal());
            ps.setDate(5, Date.valueOf(c.getDateObtention()));
            ps.executeUpdate();
            System.out.println("✅ Certification délivrée !");
        }
    }

    public List<Certification> getAll() throws SQLException {
        List<Certification> liste = new ArrayList<>();
        String sql = "SELECT * FROM certification";
        try (Connection con = MyDatabase.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    /** Récupérer toutes les certifications d'un élève. */
    public List<Certification> getByEleve(int idEleve) throws SQLException {
        List<Certification> liste = new ArrayList<>();
        String sql = "SELECT * FROM certification WHERE id_eleve = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEleve);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    /** Récupérer toutes les certifications d'une formation. */
    public List<Certification> getByFormation(int idFormation) throws SQLException {
        List<Certification> liste = new ArrayList<>();
        String sql = "SELECT * FROM certification WHERE id_formation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFormation);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    public void update(Certification c) throws SQLException {
        String sql = "UPDATE certification SET niveau_obtenu=?, score_final=?, date_obtention=? " +
                     "WHERE id_certification=?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNiveauObtenu());
            ps.setFloat(2, c.getScoreFinal());
            ps.setDate(3, Date.valueOf(c.getDateObtention()));
            ps.setInt(4, c.getIdCertification());
            ps.executeUpdate();
            System.out.println("✅ Certification mise à jour !");
        }
    }

    public void delete(int idCertification) throws SQLException {
        String sql = "DELETE FROM certification WHERE id_certification = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCertification);
            ps.executeUpdate();
            System.out.println("✅ Certification supprimée !");
        }
    }

    // ── Métier ────────────────────────────────────────────────────────────────

    /** Vérifier si un élève a déjà une certification pour une formation (contrainte uq_certif). */
    public boolean hasAlreadyCertified(int idEleve, int idFormation) throws SQLException {
        String sql = "SELECT COUNT(*) FROM certification WHERE id_eleve = ? AND id_formation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEleve);
            ps.setInt(2, idFormation);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    /** Délivrer en toute sécurité — bloque si déjà certifié. */
    public void createSafe(Certification c) throws SQLException {
        if (hasAlreadyCertified(c.getIdEleve(), c.getIdFormation())) {
            System.out.println("⚠️ Cet élève possède déjà une certification pour cette formation !");
        } else {
            create(c);
        }
    }

    /** Déterminer automatiquement le niveau selon le score. */
    public String calculerNiveau(float score) {
        if (score >= 90) return "Pro";
        if (score >= 75) return "Gold";
        if (score >= 50) return "Silver";
        return "Bronze";
    }

    /** Filtrer les certifications par niveau (Bronze / Silver / Gold / Pro). */
    public List<Certification> getByNiveau(String niveau) throws SQLException {
        List<Certification> liste = new ArrayList<>();
        String sql = "SELECT * FROM certification WHERE niveau_obtenu = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, niveau);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }
}
