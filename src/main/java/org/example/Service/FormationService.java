package org.example.Service;

import org.example.Utils.MyDatabase;
import org.example.Model.Formation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormationService {

    // ── Helper ────────────────────────────────────────────────────────────────
    private Formation extract(ResultSet rs) throws SQLException {
        Date d = rs.getDate("date_debut");
        return new Formation(
                rs.getInt("id_formation"),
                rs.getString("titre"),
                rs.getString("description"),
                rs.getString("jeu"),
                rs.getString("niveau"),
                rs.getInt("duree_semaines"),
                rs.getFloat("prix"),
                rs.getInt("id_coach"),
                d != null ? d.toLocalDate() : null,
                rs.getString("statut"),
                rs.getInt("nombre_sessions")
        );
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public void create(Formation f) throws SQLException {
        String sql = "INSERT INTO formation (titre, description, jeu, niveau, duree_semaines, prix, id_coach, date_debut, statut, nombre_sessions) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getTitre());
            ps.setString(2, f.getDescription());
            ps.setString(3, f.getJeu());
            ps.setString(4, f.getNiveau());
            ps.setInt(5, f.getDureeSemaines());
            ps.setFloat(6, f.getPrix());
            ps.setInt(7, f.getIdCoach());
            if (f.getDateDebut() != null)
                ps.setDate(8, Date.valueOf(f.getDateDebut()));
            else
                ps.setNull(8, Types.DATE);
            ps.setString(9, f.getStatut());
            ps.setInt(10, f.getNombreSessions());
            ps.executeUpdate();
            System.out.println("✅ Formation créée !");
        }
    }

    public List<Formation> getAll() throws SQLException {
        List<Formation> liste = new ArrayList<>();
        String sql = "SELECT * FROM formation";
        try (Connection con = MyDatabase.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    public Formation getById(int id) throws SQLException {
        String sql = "SELECT * FROM formation WHERE id_formation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extract(rs);
        }
        return null;
    }

    public void update(Formation f) throws SQLException {
        String sql = "UPDATE formation SET titre=?, description=?, jeu=?, niveau=?, duree_semaines=?, " +
                     "prix=?, id_coach=?, date_debut=?, statut=?, nombre_sessions=? WHERE id_formation=?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getTitre());
            ps.setString(2, f.getDescription());
            ps.setString(3, f.getJeu());
            ps.setString(4, f.getNiveau());
            ps.setInt(5, f.getDureeSemaines());
            ps.setFloat(6, f.getPrix());
            ps.setInt(7, f.getIdCoach());
            if (f.getDateDebut() != null)
                ps.setDate(8, Date.valueOf(f.getDateDebut()));
            else
                ps.setNull(8, Types.DATE);
            ps.setString(9, f.getStatut());
            ps.setInt(10, f.getNombreSessions());
            ps.setInt(11, f.getIdFormation());
            ps.executeUpdate();
            System.out.println("✅ Formation mise à jour !");
        }
    }

    public void delete(int idFormation) throws SQLException {
        String sql = "DELETE FROM formation WHERE id_formation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFormation);
            ps.executeUpdate();
            System.out.println("✅ Formation supprimée !");
        }
    }

    // ── Métier ────────────────────────────────────────────────────────────────

    /** Récupérer toutes les formations actives (statut = 'active'). */
    public List<Formation> getActive() throws SQLException {
        List<Formation> liste = new ArrayList<>();
        String sql = "SELECT * FROM formation WHERE statut = 'active'";
        try (Connection con = MyDatabase.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    /** Filtrer les formations par jeu. */
    public List<Formation> getByJeu(String jeu) throws SQLException {
        List<Formation> liste = new ArrayList<>();
        String sql = "SELECT * FROM formation WHERE jeu = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, jeu);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    /** Récupérer toutes les formations d'un coach spécifique. */
    public List<Formation> getByCoach(int idCoach) throws SQLException {
        List<Formation> liste = new ArrayList<>();
        String sql = "SELECT * FROM formation WHERE id_coach = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCoach);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(extract(rs));
        }
        return liste;
    }

    /** Activer une formation (passer statut → 'active'). */
    public void activer(int idFormation) throws SQLException {
        String sql = "UPDATE formation SET statut = 'active' WHERE id_formation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFormation);
            ps.executeUpdate();
            System.out.println("✅ Formation activée !");
        }
    }

    /** Archiver une formation (passer statut → 'archivée'). */
    public void archiver(int idFormation) throws SQLException {
        String sql = "UPDATE formation SET statut = 'archivée' WHERE id_formation = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idFormation);
            ps.executeUpdate();
            System.out.println("✅ Formation archivée !");
        }
    }
}
