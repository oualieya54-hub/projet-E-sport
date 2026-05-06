package org.example.Service;

import org.example.Model.Transaction;
import org.example.Utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    private final Connection conn = MyDatabase.getInstance().getConnection();

    public boolean ajouter(Transaction t) {
        String sql = """
            INSERT INTO transaction
              (id_user, id_produit, id_commande, type, quantite, prix_unitaire, note, commentaire, data_json)
            VALUES (?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getIdUser());
            ps.setInt(2, t.getIdProduit());
            ps.setObject(3, t.getIdCommande());
            ps.setString(4, t.getType());
            ps.setInt(5, t.getQuantite());
            ps.setObject(6, t.getPrixUnitaire());
            ps.setObject(7, t.getNote());
            ps.setString(8, t.getCommentaire());
            ps.setString(9, t.getDataJson());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) t.setIdTransaction(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.err.println("ajouter transaction : " + e.getMessage()); }
        return false;
    }

    public boolean modifier(Transaction t) {
        String sql = """
            UPDATE transaction SET
              quantite=?, prix_unitaire=?, note=?, commentaire=?, data_json=?
            WHERE id_transaction=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getQuantite());
            ps.setObject(2, t.getPrixUnitaire());
            ps.setObject(3, t.getNote());
            ps.setString(4, t.getCommentaire());
            ps.setString(5, t.getDataJson());
            ps.setInt(6, t.getIdTransaction());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("modifier transaction : " + e.getMessage()); }
        return false;
    }

    public boolean supprimer(int idTransaction) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM transaction WHERE id_transaction=?")) {
            ps.setInt(1, idTransaction);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("supprimer transaction : " + e.getMessage()); }
        return false;
    }

    public Transaction findById(int idTransaction) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM transaction WHERE id_transaction=?")) {
            ps.setInt(1, idTransaction);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("findById transaction : " + e.getMessage()); }
        return null;
    }

    public List<Transaction> findAll() {
        return query("SELECT * FROM transaction ORDER BY date_action DESC");
    }

    public List<Transaction> findByUser(int idUser) {
        return queryInt("SELECT * FROM transaction WHERE id_user=? ORDER BY date_action DESC", idUser);
    }

    public List<Transaction> findByType(String type) {
        return queryStr("SELECT * FROM transaction WHERE type=? ORDER BY date_action DESC", type);
    }

    public List<Transaction> findPanierUser(int idUser) {
        String sql = "SELECT * FROM transaction WHERE id_user=? AND type='panier'";
        return queryInt(sql, idUser);
    }

    public List<Transaction> findWishlistUser(int idUser) {
        String sql = "SELECT * FROM transaction WHERE id_user=? AND type='wishlist'";
        return queryInt(sql, idUser);
    }

    public List<Transaction> findAvisProduit(int idProduit) {
        String sql = "SELECT * FROM transaction WHERE id_produit=? AND type='avis' ORDER BY date_action DESC";
        return queryInt(sql, idProduit);
    }

    public boolean ajouterAuPanier(int idUser, int idProduit, int quantite) {
        String checkSql = "SELECT id_transaction FROM transaction WHERE id_user=? AND id_produit=? AND type='panier'";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, idUser);
            check.setInt(2, idProduit);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id_transaction");
                try (PreparedStatement upd = conn.prepareStatement(
                        "UPDATE transaction SET quantite=quantite+? WHERE id_transaction=?")) {
                    upd.setInt(1, quantite);
                    upd.setInt(2, id);
                    return upd.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { System.err.println("ajouterAuPanier check : " + e.getMessage()); }

        Transaction t = new Transaction(idUser, idProduit, null, "panier", quantite, null);
        return ajouter(t);
    }

    public boolean retirerDuPanier(int idUser, int idProduit) {
        String sql = "DELETE FROM transaction WHERE id_user=? AND id_produit=? AND type='panier'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setInt(2, idProduit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("retirerDuPanier : " + e.getMessage()); }
        return false;
    }

    public boolean ajouterWishlist(int idUser, int idProduit) {
        String checkSql = "SELECT 1 FROM transaction WHERE id_user=? AND id_produit=? AND type='wishlist'";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, idUser);
            check.setInt(2, idProduit);
            if (check.executeQuery().next()) return false;
        } catch (SQLException e) { System.err.println("ajouterWishlist check : " + e.getMessage()); }

        Transaction t = new Transaction(idUser, idProduit, null, "wishlist", 1, null);
        return ajouter(t);
    }

    public boolean posterAvis(int idUser, int idProduit, int note, String commentaire) {
        String checkSql = "SELECT 1 FROM transaction WHERE id_user=? AND id_produit=? AND type='avis'";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, idUser);
            check.setInt(2, idProduit);
            if (check.executeQuery().next()) {
                String upd = "UPDATE transaction SET note=?, commentaire=? WHERE id_user=? AND id_produit=? AND type='avis'";
                try (PreparedStatement ps = conn.prepareStatement(upd)) {
                    ps.setInt(1, note);
                    ps.setString(2, commentaire);
                    ps.setInt(3, idUser);
                    ps.setInt(4, idProduit);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { System.err.println("posterAvis check : " + e.getMessage()); }

        Transaction t = new Transaction(idUser, idProduit, note, commentaire);
        return ajouter(t);
    }

    public double getNoteMoyenneProduit(int idProduit) {
        String sql = "SELECT AVG(note) FROM transaction WHERE id_produit=? AND type='avis' AND note IS NOT NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProduit);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { System.err.println("getNoteMoyenne : " + e.getMessage()); }
        return 0;
    }

    public boolean viderPanier(int idUser) {
        String sql = "DELETE FROM transaction WHERE id_user=? AND type='panier'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("viderPanier : " + e.getMessage());
        }
        return false;
    }

    public double calculerTotalDepense(int idUser) {
        String sql = """
            SELECT SUM(quantite * prix_unitaire) as total 
            FROM transaction 
            WHERE id_user=? AND type='achat' AND prix_unitaire IS NOT NULL
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.err.println("calculerTotalDepense : " + e.getMessage());
        }
        return 0.0;
    }

    public boolean validerPanier(int idUser, int idCommande) {
        String sql = """
            UPDATE transaction
            SET type='achat', id_commande=?
            WHERE id_user=? AND type='panier'
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("validerPanier : " + e.getMessage()); }
        return false;
    }

    private List<Transaction> query(String sql) {
        List<Transaction> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("query transaction : " + e.getMessage()); }
        return list;
    }

    private List<Transaction> queryInt(String sql, int param) {
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("queryInt transaction : " + e.getMessage()); }
        return list;
    }

    private List<Transaction> queryStr(String sql, String param) {
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("queryStr transaction : " + e.getMessage()); }
        return list;
    }

    private Transaction map(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setIdTransaction(rs.getInt("id_transaction"));
        t.setIdUser(rs.getInt("id_user"));
        t.setIdProduit(rs.getInt("id_produit"));
        int idCmd = rs.getInt("id_commande"); if (!rs.wasNull()) t.setIdCommande(idCmd);
        t.setType(rs.getString("type"));
        t.setQuantite(rs.getInt("quantite"));
        double pu = rs.getDouble("prix_unitaire"); if (!rs.wasNull()) t.setPrixUnitaire(pu);
        int note = rs.getInt("note"); if (!rs.wasNull()) t.setNote(note);
        t.setCommentaire(rs.getString("commentaire"));
        t.setDataJson(rs.getString("data_json"));
        Timestamp ts = rs.getTimestamp("date_action");
        if (ts != null) t.setDateAction(ts.toLocalDateTime());
        return t;
    }
}