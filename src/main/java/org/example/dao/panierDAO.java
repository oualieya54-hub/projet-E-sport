package org.example.dao;

import org.example.connexion.connexionDB;
import org.example.modele.produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class panierDAO {

    private Connection conn;

    public panierDAO() {
        this.conn = connexionDB.getInstance();
    }

    public boolean ajouterAuPanier(int idUser, int idProduit, int quantite) {
        // Si déjà dans le panier → incrémenter la quantité
        String sql = "INSERT INTO panier (id_user, id_produit, quantite) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantite = quantite + ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setInt(2, idProduit);
            ps.setInt(3, quantite);
            ps.setInt(4, quantite);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout panier : " + e.getMessage());
        }
        return false;
    }

    public List<Object[]> getPanierUser(int idUser) {
        // Retourne : {Produit, quantite}
        List<Object[]> panier = new ArrayList<>();
        String sql = "SELECT p.*, pa.quantite FROM panier pa " +
                "JOIN produit p ON pa.id_produit = p.id_produit " +
                "WHERE pa.id_user = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                produit p = new produit();
                p.setIdProduit(rs.getInt("id_produit"));
                p.setNom(rs.getString("nom"));
                p.setPrix(rs.getBigDecimal("prix"));
                p.setImageUrl(rs.getString("image_url"));
                p.setPointsGagnes(rs.getInt("points_gagnes"));
                int qte = rs.getInt("quantite");
                panier.add(new Object[]{p, qte});
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture panier : " + e.getMessage());
        }
        return panier;
    }


    public boolean modifierQuantite(int idUser, int idProduit, int nouvelleQte) {
        if (nouvelleQte <= 0) return supprimerDuPanier(idUser, idProduit);
        String sql = "UPDATE panier SET quantite = ? WHERE id_user = ? AND id_produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nouvelleQte);
            ps.setInt(2, idUser);
            ps.setInt(3, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur modification quantité : " + e.getMessage());
        }
        return false;
    }


    public boolean supprimerDuPanier(int idUser, int idProduit) {
        String sql = "DELETE FROM panier WHERE id_user = ? AND id_produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setInt(2, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression panier : " + e.getMessage());
        }
        return false;
    }


    public boolean viderPanier(int idUser) {
        String sql = "DELETE FROM panier WHERE id_user = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur vidage panier : " + e.getMessage());
        }
        return false;
    }


    public boolean ajouterWishlist(int idUser, int idProduit) {
        String sql = "INSERT IGNORE INTO wishlist (id_user, id_produit) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setInt(2, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout wishlist : " + e.getMessage());
        }
        return false;
    }


    public List<produit> getWishlistUser(int idUser) {
        List<produit> liste = new ArrayList<>();
        String sql = "SELECT p.* FROM wishlist w " +
                "JOIN produit p ON w.id_produit = p.id_produit " +
                "WHERE w.id_user = ? ORDER BY w.date_ajout DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                produit p = new produit();
                p.setIdProduit(rs.getInt("id_produit"));
                p.setNom(rs.getString("nom"));
                p.setPrix(rs.getBigDecimal("prix"));
                p.setStock(rs.getInt("stock"));
                p.setStatut(rs.getString("statut"));
                p.setImageUrl(rs.getString("image_url"));
                liste.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture wishlist : " + e.getMessage());
        }
        return liste;
    }


    public boolean supprimerWishlist(int idUser, int idProduit) {
        String sql = "DELETE FROM wishlist WHERE id_user = ? AND id_produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setInt(2, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression wishlist : " + e.getMessage());
        }
        return false;
    }
}
