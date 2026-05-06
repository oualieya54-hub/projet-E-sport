package org.example.Service;

import org.example.Model.Produit;
import org.example.Utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProduitService {

    private final Connection conn = MyDatabase.getInstance().getConnection();

    public boolean ajouter(Produit p) {
        String sql = """
            INSERT INTO produit
              (nom, description, prix, stock, statut, type_produit, categorie,
               prix_promo, promo_debut, promo_fin, code_promo, reduction_pct, points_gagnes)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrix());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getStatut() != null ? p.getStatut() : "disponible");
            ps.setString(6, p.getTypeProduit());
            ps.setString(7, p.getCategorie());
            ps.setObject(8, p.getPrixPromo());
            ps.setObject(9, p.getPromoDebut() != null ? Timestamp.valueOf(p.getPromoDebut()) : null);
            ps.setObject(10, p.getPromoFin()  != null ? Timestamp.valueOf(p.getPromoFin())  : null);
            ps.setString(11, p.getCodePromo());
            ps.setObject(12, p.getReductionPct());
            ps.setInt(13, p.getPointsGagnes());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) p.setIdProduit(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.err.println("ajouter produit : " + e.getMessage()); }
        return false;
    }

    public boolean modifier(Produit p) {
        String sql = """
            UPDATE produit SET
              nom=?, description=?, prix=?, stock=?, statut=?, type_produit=?, categorie=?,
              prix_promo=?, promo_debut=?, promo_fin=?, code_promo=?, reduction_pct=?, points_gagnes=?
            WHERE id_produit=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrix());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getStatut());
            ps.setString(6, p.getTypeProduit());
            ps.setString(7, p.getCategorie());
            ps.setObject(8, p.getPrixPromo());
            ps.setObject(9, p.getPromoDebut() != null ? Timestamp.valueOf(p.getPromoDebut()) : null);
            ps.setObject(10, p.getPromoFin()  != null ? Timestamp.valueOf(p.getPromoFin())  : null);
            ps.setString(11, p.getCodePromo());
            ps.setObject(12, p.getReductionPct());
            ps.setInt(13, p.getPointsGagnes());
            ps.setInt(14, p.getIdProduit());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("modifier produit : " + e.getMessage()); }
        return false;
    }

    public boolean supprimer(int idProduit) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM produit WHERE id_produit=?")) {
            ps.setInt(1, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("supprimer produit : " + e.getMessage()); }
        return false;
    }

    public Produit findById(int idProduit) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM produit WHERE id_produit=?")) {
            ps.setInt(1, idProduit);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("findById produit : " + e.getMessage()); }
        return null;
    }

    public List<Produit> findAll() {
        return query("SELECT * FROM produit ORDER BY date_ajout DESC");
    }

    public List<Produit> findByType(String type) {
        return queryParam("SELECT * FROM produit WHERE type_produit=?", type);
    }

    public List<Produit> findByCategorie(String categorie) {
        return queryParam("SELECT * FROM produit WHERE categorie=?", categorie);
    }

    public List<Produit> findDisponibles() {
        return queryParam("SELECT * FROM produit WHERE statut=?", "disponible");
    }

    public List<Produit> findEnPromo() {
        String sql = "SELECT * FROM produit WHERE prix_promo IS NOT NULL AND promo_debut <= NOW() AND promo_fin >= NOW()";
        return query(sql);
    }

    public List<Produit> rechercher(String motCle) {
        String sql = "SELECT * FROM produit WHERE nom LIKE ? OR description LIKE ? OR categorie LIKE ?";
        List<Produit> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + motCle + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(map(rs));
        } catch (SQLException e) { System.err.println("rechercher : " + e.getMessage()); }
        return result;
    }

    public boolean mettreAJourStock(int idProduit, int delta) {
        String sql = """
            UPDATE produit SET
              stock = stock + ?,
              statut = CASE WHEN stock + ? <= 0 THEN 'rupture' ELSE 'disponible' END
            WHERE id_produit = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, delta);
            ps.setInt(3, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("mettreAJourStock : " + e.getMessage()); }
        return false;
    }

    public boolean appliquerPromo(int idProduit, double prixPromo, String debut, String fin) {
        String sql = "UPDATE produit SET prix_promo=?, promo_debut=?, promo_fin=? WHERE id_produit=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, prixPromo);
            ps.setString(2, debut);
            ps.setString(3, fin);
            ps.setInt(4, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("appliquerPromo : " + e.getMessage()); }
        return false;
    }

    private List<Produit> query(String sql) {
        List<Produit> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("query : " + e.getMessage()); }
        return list;
    }

    private List<Produit> queryParam(String sql, String param) {
        List<Produit> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("queryParam : " + e.getMessage()); }
        return list;
    }

    private Produit map(ResultSet rs) throws SQLException {
        Produit p = new Produit();
        p.setIdProduit(rs.getInt("id_produit"));
        p.setNom(rs.getString("nom"));
        p.setDescription(rs.getString("description"));
        p.setPrix(rs.getDouble("prix"));
        p.setStock(rs.getInt("stock"));
        p.setStatut(rs.getString("statut"));
        p.setTypeProduit(rs.getString("type_produit"));
        p.setCategorie(rs.getString("categorie"));
        p.setCodePromo(rs.getString("code_promo"));
        p.setPointsGagnes(rs.getInt("points_gagnes"));
        double pr = rs.getDouble("prix_promo"); if (!rs.wasNull()) p.setPrixPromo(pr);
        double rd = rs.getDouble("reduction_pct"); if (!rs.wasNull()) p.setReductionPct(rd);
        Timestamp td = rs.getTimestamp("promo_debut"); if (td != null) p.setPromoDebut(td.toLocalDateTime());
        Timestamp tf = rs.getTimestamp("promo_fin");   if (tf != null) p.setPromoFin(tf.toLocalDateTime());
        Timestamp da = rs.getTimestamp("date_ajout");  if (da != null) p.setDateAjout(da.toLocalDateTime());
        return p;
    }
}
