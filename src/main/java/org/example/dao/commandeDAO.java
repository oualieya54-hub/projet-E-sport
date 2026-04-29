package org.example.dao;

import org.example.connexion.connexionDB;
import org.example.modele.commande;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class commandeDAO {

    private Connection conn;

    public commandeDAO() {
        this.conn = org.example.connexion.connexionDB.getInstance();
    }


    public boolean passerCommande(org.example.modele.commande commande,
                                  List<int[]> lignes) {
        // lignes = liste de {id_produit, quantite, prix_unitaire*100}
        try {
            conn.setAutoCommit(false); // Début transaction

            // 1. Insérer la commande
            String sqlCmd = "INSERT INTO commande " +
                    "(id_user, montant_total, points_utilises, statut, adresse_livraison, methode_paiement) " +
                    "VALUES (?, ?, ?, 'en_attente', ?, ?)";
            int idCommande;
            try (PreparedStatement ps = conn.prepareStatement(sqlCmd, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, commande.getIdUser());
                ps.setBigDecimal(2, commande.getMontantTotal());
                ps.setInt(3, commande.getPointsUtilises());
                ps.setString(4, commande.getAdresseLivraison());
                ps.setString(5, commande.getMethodePaiement());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                idCommande = rs.getInt(1);
                commande.setIdCommande(idCommande);
            }

            // 2. Insérer les lignes de commande + décrémenter le stock
            String sqlLigne = "INSERT INTO ligne_commande (id_commande, id_produit, quantite, prix_unitaire) " +
                    "VALUES (?, ?, ?, ?)";
            String sqlStock = "UPDATE produit SET stock = stock - ? WHERE id_produit = ? AND stock >= ?";
            int totalPoints = 0;

            for (int[] ligne : lignes) {
                int idProduit    = ligne[0];
                int quantite     = ligne[1];
                BigDecimal prix  = BigDecimal.valueOf(ligne[2]).divide(BigDecimal.valueOf(100));

                // Ligne de commande
                try (PreparedStatement ps = conn.prepareStatement(sqlLigne)) {
                    ps.setInt(1, idCommande);
                    ps.setInt(2, idProduit);
                    ps.setInt(3, quantite);
                    ps.setBigDecimal(4, prix);
                    ps.executeUpdate();
                }

                // Décrémenter stock
                try (PreparedStatement ps = conn.prepareStatement(sqlStock)) {
                    ps.setInt(1, quantite);
                    ps.setInt(2, idProduit);
                    ps.setInt(3, quantite);
                    int updated = ps.executeUpdate();
                    if (updated == 0) {
                        conn.rollback();
                        System.err.println("❌ Stock insuffisant pour produit id=" + idProduit);
                        return false;
                    }
                }

                // Calculer les points gagnés
                String sqlPts = "SELECT points_gagnes FROM produit WHERE id_produit = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlPts)) {
                    ps.setInt(1, idProduit);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) totalPoints += rs.getInt(1) * quantite;
                }
            }

            // 3. Créditer les points fidélité
            if (totalPoints > 0) {
                crediterPoints(commande.getIdUser(), idCommande, totalPoints, "Achat commande #" + idCommande);
            }

            // 4. Débiter les points utilisés si paiement partiel par points
            if (commande.getPointsUtilises() > 0) {
                debiterPoints(commande.getIdUser(), idCommande,
                        commande.getPointsUtilises(),
                        "Utilisation points commande #" + idCommande);
            }

            conn.commit(); // Valider la transaction
            System.out.println("✅ Commande #" + idCommande + " validée ! Points gagnés : " + totalPoints);
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("❌ Erreur commande, transaction annulée : " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    public List<org.example.modele.commande> getCommandesUser(int idUser) {
        List<org.example.modele.commande> liste = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE id_user = ? ORDER BY date_commande DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(remplirCommande(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture commandes : " + e.getMessage());
        }
        return liste;
    }


    public List<org.example.modele.commande> getToutesCommandes() {
        List<org.example.modele.commande> liste = new ArrayList<>();
        String sql = "SELECT * FROM commande ORDER BY date_commande DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(remplirCommande(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture toutes commandes : " + e.getMessage());
        }
        return liste;
    }


    public boolean changerStatut(int idCommande, String nouveauStatut) {
        String sql = "UPDATE commande SET statut = ? WHERE id_commande = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, idCommande);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Commande #" + idCommande + " → " + nouveauStatut);
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur changement statut : " + e.getMessage());
        }
        return false;
    }


    private void crediterPoints(int idUser, int idCommande, int points, String motif) throws SQLException {
        // Insérer ou mettre à jour le solde
        String sqlUpsert = "INSERT INTO points_fidelite (id_user, solde_actuel) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE solde_actuel = solde_actuel + ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlUpsert)) {
            ps.setInt(1, idUser);
            ps.setInt(2, points);
            ps.setInt(3, points);
            ps.executeUpdate();
        }
        // Historique
        insererHistoriquePoints(idUser, idCommande, points, "gain", motif);
    }


    private void debiterPoints(int idUser, int idCommande, int points, String motif) throws SQLException {
        String sql = "UPDATE points_fidelite SET solde_actuel = solde_actuel - ? " +
                "WHERE id_user = ? AND solde_actuel >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setInt(2, idUser);
            ps.setInt(3, points);
            ps.executeUpdate();
        }
        insererHistoriquePoints(idUser, idCommande, -points, "depense", motif);
    }

    private void insererHistoriquePoints(int idUser, int idCommande,
                                         int points, String type, String motif) throws SQLException {
        String sql = "INSERT INTO historique_points (id_user, id_commande, points, type_operation, motif) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            if (idCommande > 0) ps.setInt(2, idCommande); else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, points);
            ps.setString(4, type);
            ps.setString(5, motif);
            ps.executeUpdate();
        }
    }


    public int getSoldePoints(int idUser) {
        String sql = "SELECT solde_actuel FROM points_fidelite WHERE id_user = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture points : " + e.getMessage());
        }
        return 0;
    }


    //  HELPER
    private org.example.modele.commande remplirCommande(ResultSet rs) throws SQLException {
        org.example.modele.commande c = new org.example.modele.commande();
        c.setIdCommande(rs.getInt("id_commande"));
        c.setIdUser(rs.getInt("id_user"));
        c.setMontantTotal(rs.getBigDecimal("montant_total"));
        c.setPointsUtilises(rs.getInt("points_utilises"));
        c.setStatut(rs.getString("statut"));
        c.setAdresseLivraison(rs.getString("adresse_livraison"));
        c.setMethodePaiement(rs.getString("methode_paiement"));
        Timestamp ts = rs.getTimestamp("date_commande");
        if (ts != null) c.setDateCommande(ts.toLocalDateTime());
        return c;
    }
}
