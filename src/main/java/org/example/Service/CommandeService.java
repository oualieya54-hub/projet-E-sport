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

    public Commande passerCommande(int idUser, String adresseLivraison, String methodePaiement, int pointsAUtiliser) {
        TransactionService transactionService = new TransactionService();
        ProduitService produitService = new ProduitService();
        UserService userService = new UserService();

        List<org.example.Model.Transaction> panier = transactionService.findPanierUser(idUser);
        if (panier == null || panier.isEmpty()) {
            System.err.println("❌ Le panier est vide.");
            return null;
        }

        double montantTotal = 0;
        int totalPointsGagnes = 0;

        for (org.example.Model.Transaction item : panier) {
            org.example.Model.Produit produit = produitService.findById(item.getIdProduit());
            if (produit == null || produit.getStock() < item.getQuantite()) {
                System.err.println("❌ Stock insuffisant ou produit introuvable pour ID " + item.getIdProduit());
                return null;
            }
            double prix = (produit.getPrixPromo() != null && produit.getPrixPromo() > 0) ? produit.getPrixPromo() : produit.getPrix();
            montantTotal += prix * item.getQuantite();
            totalPointsGagnes += produit.getPointsGagnes() * item.getQuantite();
        }

        // Apply points discount (e.g., 100 points = 1 Euro)
        double reduction = pointsAUtiliser / 100.0;
        if (reduction > montantTotal) {
            reduction = montantTotal;
            pointsAUtiliser = (int) (montantTotal * 100);
        }
        montantTotal -= reduction;

        Commande c = new Commande();
        c.setIdUser(idUser);
        c.setMontantTotal(montantTotal);
        c.setPointsUtilises(pointsAUtiliser);
        c.setPointsGagnes(totalPointsGagnes);
        c.setStatut("en_attente");
        c.setAdresseLivraison(adresseLivraison);
        c.setMethodePaiement(methodePaiement);

        try {
            conn.setAutoCommit(false); // Begin transaction

            if (ajouter(c)) {
                // Validate cart (changes 'panier' to 'achat')
                transactionService.validerPanier(idUser, c.getIdCommande());

                // Update stock
                for (org.example.Model.Transaction item : panier) {
                    produitService.mettreAJourStock(item.getIdProduit(), -item.getQuantite());
                }

                // Update user points
                int deltaPoints = totalPointsGagnes - pointsAUtiliser;
                if (deltaPoints != 0) {
                    userService.mettreAJourPoints(idUser, deltaPoints);
                }

                conn.commit();
                System.out.println("✅ Commande passée avec succès : ID " + c.getIdCommande());
                return c;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du passage de commande : " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("❌ Erreur lors du rollback : " + ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public boolean changerStatut(int idCommande, String nouveauStatut) {
        Commande c = findById(idCommande);
        if (c == null) return false;

        String oldStatut = c.getStatut();
        if (oldStatut.equals(nouveauStatut)) return true;

        String sql = "UPDATE commande SET statut = ? WHERE id_commande = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, idCommande);
            boolean ok = ps.executeUpdate() > 0;

            if (ok) {
                // If cancelled, rollback stock and points
                if ("annulée".equals(nouveauStatut) && !"annulée".equals(oldStatut)) {
                    UserService userService = new UserService();
                    ProduitService produitService = new ProduitService();

                    // Restore points: subtract gained points, add back used points
                    userService.mettreAJourPoints(c.getIdUser(), c.getPointsUtilises() - c.getPointsGagnes());

                    // We would ideally fetch all transactions for this commande and restore stock:
                    String tSql = "SELECT id_produit, quantite FROM transaction WHERE id_commande = ?";
                    try (PreparedStatement tPs = conn.prepareStatement(tSql)) {
                        tPs.setInt(1, idCommande);
                        ResultSet rs = tPs.executeQuery();
                        while (rs.next()) {
                            produitService.mettreAJourStock(rs.getInt("id_produit"), rs.getInt("quantite"));
                        }
                    }
                }
            }
            return ok;
        } catch (SQLException e) {
            System.err.println("❌ Erreur changerStatut : " + e.getMessage());
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
