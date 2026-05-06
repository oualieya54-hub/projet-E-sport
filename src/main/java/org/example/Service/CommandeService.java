package org.example.Service;

import org.example.Model.Commande;
import org.example.Utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeService {

    private final Connection conn;

    public CommandeService() {
        this.conn = MyDatabase.getInstance().getConnection();
    }

    public boolean ajouter(Commande c) {
        String sql = "INSERT INTO commande (id_user, montant_total, points_utilises, points_gagnes, statut, adresse_livraison, methode_paiement) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getIdUser());
            ps.setDouble(2, c.getMontantTotal());
            ps.setInt(3, c.getPointsUtilises());
            ps.setInt(4, c.getPointsGagnes());
            ps.setString(5, c.getStatut());
            ps.setString(6, c.getAdresseLivraison());
            ps.setString(7, c.getMethodePaiement());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    c.setIdCommande(rs.getInt(1));
                }
                System.out.println("✅ Commande ajoutée : id=" + c.getIdCommande());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout commande : " + e.getMessage());
        }
        return false;
    }

    public boolean modifier(Commande c) {
        String sql = "UPDATE commande SET id_user=?, montant_total=?, points_utilises=?, points_gagnes=?, statut=?, adresse_livraison=?, methode_paiement=? WHERE id_commande=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdUser());
            ps.setDouble(2, c.getMontantTotal());
            ps.setInt(3, c.getPointsUtilises());
            ps.setInt(4, c.getPointsGagnes());
            ps.setString(5, c.getStatut());
            ps.setString(6, c.getAdresseLivraison());
            ps.setString(7, c.getMethodePaiement());
            ps.setInt(8, c.getIdCommande());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Commande modifiée : id=" + c.getIdCommande());
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur modification commande : " + e.getMessage());
        }
        return false;
    }

    public boolean supprimer(int idCommande) {
        String sql = "DELETE FROM commande WHERE id_commande = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression commande : " + e.getMessage());
        }
        return false;
    }

    public List<Commande> findAll() {
        List<Commande> list = new ArrayList<>();
        String sql = "SELECT * FROM commande ORDER BY date_commande DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture commandes : " + e.getMessage());
        }
        return list;
    }

    public Commande findById(int idCommande) {
        String sql = "SELECT * FROM commande WHERE id_commande = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lecture commande id=" + idCommande + " : " + e.getMessage());
        }
        return null;
    }

    private Commande map(ResultSet rs) throws SQLException {
        Commande c = new Commande();
        c.setIdCommande(rs.getInt("id_commande"));
        c.setIdUser(rs.getInt("id_user"));
        c.setMontantTotal(rs.getDouble("montant_total"));
        c.setPointsUtilises(rs.getInt("points_utilises"));
        c.setPointsGagnes(rs.getInt("points_gagnes"));
        c.setStatut(rs.getString("statut"));

        Timestamp ts = rs.getTimestamp("date_commande");
        if (ts != null) {
            c.setDateCommande(ts.toLocalDateTime());
        }
        c.setAdresseLivraison(rs.getString("adresse_livraison"));
        c.setMethodePaiement(rs.getString("methode_paiement"));
        return c;
    }
}
