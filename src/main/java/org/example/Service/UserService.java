package org.example.Service;

import org.example.Model.User;
import org.example.Utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final Connection conn = MyDatabase.getInstance().getConnection();

    public boolean ajouter(User u) {
        String sql = "INSERT INTO users (nom, pseudo, email, password, avatar_url, role, points, is_banned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPseudo());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getAvatarUrl());
            ps.setString(6, u.getRole() != null ? u.getRole() : "player");
            ps.setInt(7, u.getPoints());
            ps.setBoolean(8, u.isBanned());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    u.setId(rs.getInt(1));
                }
                System.out.println("✅ User ajouté : id=" + u.getId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout user : " + e.getMessage());
        }
        return false;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur findById user : " + e.getMessage());
        }
        return null;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur findAll user : " + e.getMessage());
        }
        return list;
    }

    public boolean mettreAJourPoints(int idUser, int deltaPoints) {
        String sql = "UPDATE users SET points = points + ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deltaPoints);
            ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur mettreAJourPoints : " + e.getMessage());
        }
        return false;
    }

    public boolean attribuerBonus(int idUser, int montantBonus, String raison) {
        System.out.println("🎁 Attribution de " + montantBonus + " points bonus à l'utilisateur " + idUser + " pour : " + raison);
        return mettreAJourPoints(idUser, montantBonus);
    }

    public boolean verifierEtPromouvoirVIP(int idUser) {
        TransactionService ts = new TransactionService();
        double totalDepense = ts.calculerTotalDepense(idUser);

        if (totalDepense >= 500.0) { // Seuil pour devenir VIP
            String sql = "UPDATE users SET role = 'VIP' WHERE id = ? AND role != 'VIP' AND role != 'admin'";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idUser);
                if (ps.executeUpdate() > 0) {
                    System.out.println("⭐ L'utilisateur " + idUser + " a été promu VIP ! (Total dépensé : " + totalDepense + "€)");
                    // On peut aussi lui donner un bonus de bienvenue VIP
                    attribuerBonus(idUser, 500, "Passage au statut VIP");
                    return true;
                }
            } catch (SQLException e) {
                System.err.println("❌ Erreur promotion VIP : " + e.getMessage());
            }
        }
        return false;
    }

    public boolean bannirUser(int idUser, boolean bannir) {
        String sql = "UPDATE users SET is_banned = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, bannir);
            ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur bannirUser : " + e.getMessage());
        }
        return false;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setPseudo(rs.getString("pseudo"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setRole(rs.getString("role"));
        u.setPoints(rs.getInt("points"));

        Timestamp activeTs = rs.getTimestamp("last_active");
        if (activeTs != null) u.setLastActive(activeTs.toLocalDateTime());

        u.setBanned(rs.getBoolean("is_banned"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) u.setCreatedAt(createdTs.toLocalDateTime());

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) u.setUpdatedAt(updatedTs.toLocalDateTime());

        return u;
    }
}
