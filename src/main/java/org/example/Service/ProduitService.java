package org.example.dao;

import org.example.connexion.connexionDB;
import org.example.modele.produit;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class produitDAO {

    private Connection conn;

    public produitDAO() {
        this.conn = connexionDB.getInstance();
    }


    public boolean ajouterProduit(produit p) {
        String sql = "INSERT INTO produit " +
                "(id_categorie, nom, description, prix, stock, image_url, " +
                "statut, type_produit, points_gagnes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    p.getIdCategorie());
            ps.setString(2, p.getNom());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrix());
            ps.setInt(5,    p.getStock());
            ps.setString(6, p.getImageUrl());
            ps.setString(7, p.getStatut());
            ps.setString(8, p.getTypeProduit());
            ps.setInt(9,    p.getPointsGagnes());

            int lignes = ps.executeUpdate();

            // Récupérer l'ID généré automatiquement
            if (lignes > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) p.setIdProduit(rs.getInt(1));
                System.out.println("✅ Produit ajouté : " + p.getNom() + " (id=" + p.getIdProduit() + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout produit : " + e.getMessage());
        }
        return false;
    }


    public List<produit> getTousProduits() {
        List<produit> liste = new ArrayList<>();
        String sql = "SELECT * FROM produit ORDER BY date_ajout DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(remplirProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture produits : " + e.getMessage());
        }
        return liste;
    }

    public produit getProduitParId(int id) {
        String sql = "SELECT * FROM produit WHERE id_produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return remplirProduit(rs);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture produit id=" + id + " : " + e.getMessage());
        }
        return null;
    }


    public List<produit> getProduitsParCategorie(int idCategorie) {
        List<produit> liste = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE id_categorie = ? AND statut = 'disponible'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCategorie);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(remplirProduit(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur produits par catégorie : " + e.getMessage());
        }
        return liste;
    }


    public List<produit> rechercherProduits(String motCle) {
        List<produit> liste = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE nom LIKE ? OR description LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + motCle + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(remplirProduit(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche : " + e.getMessage());
        }
        return liste;
    }


    public List<produit> getProduitsEnFlashSale() {
        List<produit> liste = new ArrayList<>();
        String sql = "SELECT p.* FROM produit p " +
                "JOIN flash_sale f ON p.id_produit = f.id_produit " +
                "WHERE f.active = 1 AND f.fin > NOW() AND f.stock_flash > 0";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(remplirProduit(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur flash sales : " + e.getMessage());
        }
        return liste;
    }


    public boolean modifierProduit(produit p) {
        String sql = "UPDATE produit SET id_categorie=?, nom=?, description=?, " +
                "prix=?, stock=?, image_url=?, statut=?, type_produit=?, points_gagnes=? " +
                "WHERE id_produit=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    p.getIdCategorie());
            ps.setString(2, p.getNom());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrix());
            ps.setInt(5,    p.getStock());
            ps.setString(6, p.getImageUrl());
            ps.setString(7, p.getStatut());
            ps.setString(8, p.getTypeProduit());
            ps.setInt(9,    p.getPointsGagnes());
            ps.setInt(10,   p.getIdProduit());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Produit modifié : id=" + p.getIdProduit());
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur modification produit : " + e.getMessage());
        }
        return false;
    }


    public boolean mettreAJourStock(int idProduit, int nouveauStock) {
        String sql = "UPDATE produit SET stock = ?, " +
                "statut = CASE WHEN ? <= 0 THEN 'rupture' ELSE 'disponible' END " +
                "WHERE id_produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nouveauStock);
            ps.setInt(2, nouveauStock);
            ps.setInt(3, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur mise à jour stock : " + e.getMessage());
        }
        return false;
    }


    public boolean supprimerProduit(int idProduit) {
        // Bonne pratique : on archive plutôt que supprimer
        String sql = "UPDATE produit SET statut = 'archivé' WHERE id_produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProduit);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("🗄️ Produit archivé : id=" + idProduit);
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression produit : " + e.getMessage());
        }
        return false;
    }


    //  Convertit un ResultSet en objet Produit

    private produit remplirProduit(ResultSet rs) throws SQLException {
        produit p = new produit();
        p.setIdProduit(rs.getInt("id_produit"));
        p.setIdCategorie(rs.getInt("id_categorie"));
        p.setNom(rs.getString("nom"));
        p.setDescription(rs.getString("description"));
        p.setPrix(rs.getBigDecimal("prix"));
        p.setStock(rs.getInt("stock"));
        p.setImageUrl(rs.getString("image_url"));
        p.setStatut(rs.getString("statut"));
        p.setTypeProduit(rs.getString("type_produit"));
        p.setPointsGagnes(rs.getInt("points_gagnes"));
        Timestamp ts = rs.getTimestamp("date_ajout");
        if (ts != null) p.setDateAjout(ts.toLocalDateTime());
        return p;
    }
}

