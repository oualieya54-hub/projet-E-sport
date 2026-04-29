package org.example.dao;

import org.example.connexion.connexionDB;
import org.example.modele.Paiement;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaiementDAO {
    private Connection conn;

    public PaiementDAO() {
        this.conn = connexionDB.getInstance();
    }

    // ✅ AJOUTER PAIEMENT
    public boolean add(Paiement paiement) {
        String sql = "INSERT INTO paiement (id_commande, montant, methode, statut, date_paiement, token_stripe, ref_transaction) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, paiement.getIdCommande());
            ps.setBigDecimal(2, paiement.getMontant());
            ps.setString(3, paiement.getMethode());
            ps.setString(4, paiement.getStatut());
            ps.setTimestamp(5, Timestamp.valueOf(paiement.getDatePaiement()));
            ps.setString(6, paiement.getTokenStripe());
            ps.setString(7, paiement.getReferenceTransaction());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                paiement.setIdPaiement(rs.getInt(1));
            }
            System.out.println("✅ Paiement ajouté: " + paiement.getMethode());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout paiement: " + e.getMessage());
            return false;
        }
    }

    // ✅ RÉCUPÉRER PAIEMENT PAR ID
    public Paiement getById(int id) {
        String sql = "SELECT * FROM paiement WHERE id_paiement = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return remplirPaiement(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture paiement: " + e.getMessage());
        }
        return null;
    }

    // ✅ RÉCUPÉRER PAIEMENT PAR COMMANDE
    public Paiement getByIdCommande(int idCommande) {
        String sql = "SELECT * FROM paiement WHERE id_commande = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return remplirPaiement(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture paiement par commande: " + e.getMessage());
        }
        return null;
    }

    // ✅ RÉCUPÉRER TOUS LES PAIEMENTS
    public List<Paiement> getAll() {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement ORDER BY date_paiement DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                paiements.add(remplirPaiement(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture paiements: " + e.getMessage());
        }
        return paiements;
    }

    // ✅ METTRE À JOUR PAIEMENT
    public boolean update(Paiement paiement) {
        String sql = "UPDATE paiement SET montant=?, methode=?, statut=?, token_stripe=?, ref_transaction=? WHERE id_paiement=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, paiement.getMontant());
            ps.setString(2, paiement.getMethode());
            ps.setString(3, paiement.getStatut());
            ps.setString(4, paiement.getTokenStripe());
            ps.setString(5, paiement.getReferenceTransaction());
            ps.setInt(6, paiement.getIdPaiement());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Paiement mis à jour");
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour paiement: " + e.getMessage());
            return false;
        }
    }

    // ✅ SUPPRIMER PAIEMENT
    public boolean delete(int id) {
        String sql = "DELETE FROM paiement WHERE id_paiement = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Paiement supprimé");
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression paiement: " + e.getMessage());
            return false;
        }
    }

    // ✅ OBTENIR PAIEMENTS PAR STATUT
    public List<Paiement> getByStatut(String statut) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement WHERE statut = ? ORDER BY date_paiement DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                paiements.add(remplirPaiement(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture paiements par statut: " + e.getMessage());
        }
        return paiements;
    }

    // ✅ VÉRIFIER SI PAIEMENT APPROUVÉ
    public boolean isPaiementApprouve(int idCommande) {
        Paiement paiement = getByIdCommande(idCommande);
        return paiement != null && paiement.getStatut().equals("approuve");
    }

    // Helper
    private Paiement remplirPaiement(ResultSet rs) throws SQLException {
        Paiement p = new Paiement();
        p.setIdPaiement(rs.getInt("id_paiement"));
        p.setIdCommande(rs.getInt("id_commande"));
        p.setMontant(rs.getBigDecimal("montant"));
        p.setMethode(rs.getString("methode"));
        p.setStatut(rs.getString("statut"));
        p.setTokenStripe(rs.getString("token_stripe"));
        p.setReferenceTransaction(rs.getString("ref_transaction"));
        Timestamp ts = rs.getTimestamp("date_paiement");
        if (ts != null) p.setDatePaiement(ts.toLocalDateTime());
        return p;
    }
}