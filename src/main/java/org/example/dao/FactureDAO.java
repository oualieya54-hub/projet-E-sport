package org.example.dao;

import org.example.connexion.connexionDB;
import org.example.modele.Facture;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {
    private Connection conn;

    public FactureDAO() {
        this.conn = connexionDB.getInstance();
    }

    // ✅ AJOUTER FACTURE
    public boolean add(Facture facture) {
        String sql = "INSERT INTO facture (numero_facture, id_commande, montant_total, montant_tva, montant_ht, " +
                "statut, adresse_livraison, envoyee_email, chemin_pdf) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, facture.getNumeroFacture());
            ps.setInt(2, facture.getIdCommande());
            ps.setBigDecimal(3, facture.getMontantTotal());
            ps.setBigDecimal(4, facture.getMontantTVA());
            ps.setBigDecimal(5, facture.getMontantHT());
            ps.setString(6, facture.getStatut());
            ps.setString(7, facture.getAdresseLivraison());
            ps.setBoolean(8, facture.isEnvoyeeEmail());
            ps.setString(9, facture.getCheminPDF());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                facture.setIdFacture(rs.getInt(1));
            }
            System.out.println("✅ Facture ajoutée: " + facture.getNumeroFacture());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout facture: " + e.getMessage());
            return false;
        }
    }

    // ✅ RÉCUPÉRER FACTURE PAR ID
    public Facture getById(int id) {
        String sql = "SELECT * FROM facture WHERE id_facture = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return remplirFacture(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture facture: " + e.getMessage());
        }
        return null;
    }

    // ✅ RÉCUPÉRER FACTURE PAR COMMANDE
    public Facture getByIdCommande(int idCommande) {
        String sql = "SELECT * FROM facture WHERE id_commande = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return remplirFacture(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture facture par commande: " + e.getMessage());
        }
        return null;
    }

    // ✅ RÉCUPÉRER TOUTES LES FACTURES
    public List<Facture> getAll() {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT * FROM facture ORDER BY date_facture DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                factures.add(remplirFacture(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture factures: " + e.getMessage());
        }
        return factures;
    }

    // ✅ METTRE À JOUR FACTURE
    public boolean update(Facture facture) {
        String sql = "UPDATE facture SET statut=?, envoyee_email=?, date_envoi=?, chemin_pdf=? WHERE id_facture=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, facture.getStatut());
            ps.setBoolean(2, facture.isEnvoyeeEmail());
            if (facture.getDateEnvoi() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(facture.getDateEnvoi()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.setString(4, facture.getCheminPDF());
            ps.setInt(5, facture.getIdFacture());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Facture mise à jour");
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour facture: " + e.getMessage());
            return false;
        }
    }

    // ✅ SUPPRIMER FACTURE
    public boolean delete(int id) {
        String sql = "DELETE FROM facture WHERE id_facture = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Facture supprimée");
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression facture: " + e.getMessage());
            return false;
        }
    }

    // ✅ OBTENIR FACTURES PAR STATUT
    public List<Facture> getByStatut(String statut) {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT * FROM facture WHERE statut = ? ORDER BY date_facture DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                factures.add(remplirFacture(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture factures par statut: " + e.getMessage());
        }
        return factures;
    }

    // ✅ MONTANT TOTAL DES FACTURES
    public java.math.BigDecimal getMontantTotal() {
        String sql = "SELECT SUM(montant_total) as total FROM facture";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur calcul montant total: " + e.getMessage());
        }
        return java.math.BigDecimal.ZERO;
    }

    // Helper
    private Facture remplirFacture(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        f.setIdFacture(rs.getInt("id_facture"));
        f.setNumeroFacture(rs.getString("numero_facture"));
        f.setIdCommande(rs.getInt("id_commande"));
        f.setMontantTotal(rs.getBigDecimal("montant_total"));
        f.setMontantTVA(rs.getBigDecimal("montant_tva"));
        f.setMontantHT(rs.getBigDecimal("montant_ht"));
        f.setStatut(rs.getString("statut"));
        f.setAdresseLivraison(rs.getString("adresse_livraison"));
        f.setEnvoyeeEmail(rs.getBoolean("envoyee_email"));
        f.setCheminPDF(rs.getString("chemin_pdf"));

        Timestamp ts = rs.getTimestamp("date_facture");
        if (ts != null) f.setDateFacture(ts.toLocalDateTime());

        Timestamp tsEnvoi = rs.getTimestamp("date_envoi");
        if (tsEnvoi != null) f.setDateEnvoi(tsEnvoi.toLocalDateTime());

        return f;
    }
}