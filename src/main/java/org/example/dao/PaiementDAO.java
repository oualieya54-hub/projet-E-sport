package org.example.dao;

import org.example.connexion.connexionDB;
import org.example.modele.Paiement;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaiementDAO {
    private connexionDB connexion;

    public PaiementDAO(connexionDB connexion) {
        this.connexion = connexion;
    }

    // ✅ AJOUTER PAIEMENT
    public void add(Paiement paiement) throws SQLException {
        String sql = "INSERT INTO paiement (commande_id, montant, methode, statut, date, token_stripe, ref_transaction) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, paiement.getCommandeId());
            stmt.setDouble(2, paiement.getMontant());
            stmt.setString(3, paiement.getMethode());
            stmt.setString(4, paiement.getStatut());
            stmt.setTimestamp(5, Timestamp.valueOf(paiement.getDate()));
            stmt.setString(6, paiement.getTokenStripe());
            stmt.setString(7, paiement.getReferenceTransaction());
            stmt.executeUpdate();
            System.out.println("✅ Paiement ajouté: " + paiement.getMethode());
        }
    }

    // ✅ RÉCUPÉRER PAIEMENT PAR ID
    public Paiement getById(int id) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE id = ?";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPaiement(rs);
            }
        }
        return null;
    }

    // ✅ RÉCUPÉRER PAIEMENT PAR COMMANDE
    public Paiement getByCommandeId(int commandeId) throws SQLException {
        String sql = "SELECT * FROM paiement WHERE commande_id = ? LIMIT 1";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPaiement(rs);
            }
        }
        return null;
    }

    // ✅ RÉCUPÉRER TOUS LES PAIEMENTS
    public List<Paiement> getAll() throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement";

        try (Statement stmt = connexion.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    // ✅ METTRE À JOUR PAIEMENT
    public void update(Paiement paiement) throws SQLException {
        String sql = "UPDATE paiement SET montant=?, methode=?, statut=?, token_stripe=?, ref_transaction=? WHERE id=?";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, paiement.getMontant());
            stmt.setString(2, paiement.getMethode());
            stmt.setString(3, paiement.getStatut());
            stmt.setString(4, paiement.getTokenStripe());
            stmt.setString(5, paiement.getReferenceTransaction());
            stmt.setInt(6, paiement.getId());
            stmt.executeUpdate();
            System.out.println("✅ Paiement mis à jour");
        }
    }

    // ✅ SUPPRIMER PAIEMENT
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM paiement WHERE id = ?";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Paiement supprimé");
        }
    }

    // ✅ OBTENIR PAIEMENTS PAR STATUT
    public List<Paiement> getByStatut(String statut) throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement WHERE statut = ?";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setString(1, statut);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        }
        return paiements;
    }

    // ✅ VÉRIFIER SI PAIEMENT APPROUVÉ
    public boolean isPaiementApprouve(int commandeId) throws SQLException {
        Paiement paiement = getByCommandeId(commandeId);
        return paiement != null && paiement.getStatut().equals("approuve");
    }

    private Paiement mapResultSetToPaiement(ResultSet rs) throws SQLException {
        Paiement paiement = new Paiement();
        paiement.setId(rs.getInt("id"));
        paiement.setCommandeId(rs.getInt("commande_id"));
        paiement.setMontant(rs.getDouble("montant"));
        paiement.setMethode(rs.getString("methode"));
        paiement.setStatut(rs.getString("statut"));
        paiement.setDate(rs.getTimestamp("date").toLocalDateTime());
        paiement.setTokenStripe(rs.getString("token_stripe"));
        paiement.setReferenceTransaction(rs.getString("ref_transaction"));
        return paiement;
    }
}
