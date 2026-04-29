package org.example.dao;

import org.example.connexion.connexionDB;
import org.example.modele.Facture;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {
    private connexionDB connexion;

    public FactureDAO(connexionDB connexion) {
        this.connexion = connexion;
    }

    // ✅ AJOUTER FACTURE
    public void add(Facture facture) throws SQLException {
        String sql = "INSERT INTO facture (numero_facture, commande_id, date, montant_total, montant_tva, montant_ht, " +
                "statut, adresse_livraison, envoyee_email, chemin_pdf) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setString(1, facture.getNumeroFacture());
            stmt.setInt(2, facture.getCommandeId());
            stmt.setTimestamp(3, Timestamp.valueOf(facture.getDate()));
            stmt.setDouble(4, facture.getMontantTotal());
            stmt.setDouble(5, facture.getMontantTVA());
            stmt.setDouble(6, facture.getMontantHT());
            stmt.setString(7, facture.getStatut());
            stmt.setString(8, facture.getAdresseLivraison());
            stmt.setBoolean(9, facture.isEnvoyeeEmail());
            stmt.setString(10, facture.getCheminPDF());
            stmt.executeUpdate();
            System.out.println("✅ Facture ajoutée: " + facture.getNumeroFacture());
        }
    }

    // ✅ RÉCUPÉRER FACTURE PAR ID
    public Facture getById(int id) throws SQLException {
        String sql = "SELECT * FROM facture WHERE id = ?";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFacture(rs);
            }
        }
        return null;
    }

    // ✅ RÉCUPÉRER FACTURE PAR COMMANDE
    public Facture getByCommandeId(int commandeId) throws SQLException {
        String sql = "SELECT * FROM facture WHERE commande_id = ? LIMIT 1";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFacture(rs);
            }
        }
        return null;
    }

    // ✅ RÉCUPÉRER TOUTES LES FACTURES
    public List<Facture> getAll() throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT * FROM facture";

        try (Statement stmt = connexion.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                factures.add(mapResultSetToFacture(rs));
            }
        }
        return factures;
    }

    // ✅ METTRE À JOUR FACTURE
    public void update(Facture facture) throws SQLException {
        String sql = "UPDATE facture SET statut=?, envoyee_email=?, date_envoi=?, chemin_pdf=? WHERE id=?";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setString(1, facture.getStatut());
            stmt.setBoolean(2, facture.isEnvoyeeEmail());
            stmt.setTimestamp(3, facture.getDateEnvoi() != null ? Timestamp.valueOf(facture.getDateEnvoi()) : null);
            stmt.setString(4, facture.getCheminPDF());
            stmt.setInt(5, facture.getId());
            stmt.executeUpdate();
            System.out.println("✅ Facture mise à jour");
        }
    }

    // ✅ SUPPRIMER FACTURE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM facture WHERE id = ?";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Facture supprimée");
        }
    }

    // ✅ OBTENIR FACTURES PAR STATUT
    public List<Facture> getByStatut(String statut) throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT * FROM facture WHERE statut = ?";

        try (PreparedStatement stmt = connexion.getConnection().prepareStatement(sql)) {
            stmt.setString(1, statut);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                factures.add(mapResultSetToFacture(rs));
            }
        }
        return factures;
    }

    // ✅ MONTANT TOTAL DES FACTURES
    public double getMontantTotal() throws SQLException {
        String sql = "SELECT SUM(montant_total) as total FROM facture";

        try (Statement stmt = connexion.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }

    private Facture mapResultSetToFacture(ResultSet rs) throws SQLException {
        Facture facture = new Facture();
        facture.setId(rs.getInt("id"));
        facture.setNumeroFacture(rs.getString("numero_facture"));
        facture.setCommandeId(rs.getInt("commande_id"));
        facture.setDate(rs.getTimestamp("date").toLocalDateTime());
        facture.setMontantTotal(rs.getDouble("montant_total"));
        facture.setStatut(rs.getString("statut"));
        facture.setAdresseLivraison(rs.getString("adresse_livraison"));
        facture.setEnvoyeeEmail(rs.getBoolean("envoyee_email"));
        facture.setCheminPDF(rs.getString("chemin_pdf"));
        if (rs.getTimestamp("date_envoi") != null) {
            facture.setDateEnvoi(rs.getTimestamp("date_envoi").toLocalDateTime());
        }
        return facture;
    }
}
