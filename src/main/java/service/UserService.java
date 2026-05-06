package service;

import models.User;
import utils.DatabaseConnection;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // ============================================================
    // HELPER: Hash password with SHA-256
    // ============================================================
    private String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plain.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // ============================================================
    // HELPER: Map ResultSet row → User object
    // Columns: id, nom, pseudo, email, password, avatar_url,
    //          role, points, last_active, is_banned,
    //          created_at, updated_at
    // NOTE: last_active is DATETIME in merged_esport — getTimestamp()
    //       handles both DATETIME and TIMESTAMP in MySQL JDBC.
    // ============================================================
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setPseudo(rs.getString("pseudo"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setRole(rs.getString("role"));
        u.setPoints(rs.getInt("points"));
        u.setLastActive(rs.getTimestamp("last_active"));
        u.setIsBanned(rs.getBoolean("is_banned"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }

    // ============================================================
    // CREATE — register a new user
    // role defaults to 'player', points default to 0 (DB default)
    // ============================================================
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (nom, pseudo, email, password, avatar_url, role) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getNom());
            ps.setString(2, user.getPseudo());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashPassword(user.getPassword()));
            ps.setString(5, user.getAvatarUrl());                             // nullable
            ps.setString(6, user.getRole() != null ? user.getRole() : "player");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("addUser error: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // READ — get all users
    // ============================================================
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("getAllUsers error: " + e.getMessage());
        }
        return list;
    }

    // ============================================================
    // READ — get user by ID
    // ============================================================
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            System.err.println("getUserById error: " + e.getMessage());
        }
        return null;
    }

    // ============================================================
    // READ — get user by pseudo (login / profile lookup)
    // ============================================================
    public User getUserByPseudo(String pseudo) {
        String sql = "SELECT * FROM users WHERE pseudo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pseudo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            System.err.println("getUserByPseudo error: " + e.getMessage());
        }
        return null;
    }

    // ============================================================
    // READ — search users by name or pseudo
    // ============================================================
    public List<User> searchUsers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE nom LIKE ? OR pseudo LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("searchUsers error: " + e.getMessage());
        }
        return list;
    }

    // ============================================================
    // UPDATE — edit profile (non-sensitive fields only)
    // ============================================================
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET nom=?, pseudo=?, email=?, avatar_url=?, role=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getNom());
            ps.setString(2, user.getPseudo());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAvatarUrl());
            ps.setString(5, user.getRole());
            ps.setInt(6, user.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("updateUser error: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // UPDATE — change password
    // ============================================================
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hashPassword(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("updatePassword error: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // UPDATE — manually adjust loyalty points (add or subtract)
    // NOTE: merged_esport has an `after_commande_insert` trigger
    //       that auto-updates points when a commande row is inserted.
    //       Use this method only for manual / out-of-order adjustments.
    // ============================================================
    public boolean updatePoints(int userId, int delta) {
        String sql = "UPDATE users SET points = GREATEST(0, points + ?) WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, delta);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("updatePoints error: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // UPDATE — ban / unban a user (admin only)
    // is_banned is tinyint(1) in merged_esport — setBoolean maps
    // correctly to 0 / 1.
    // ============================================================
    public boolean setBanned(int userId, boolean banned) {
        String sql = "UPDATE users SET is_banned=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, banned);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("setBanned error: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // UPDATE — record last active timestamp
    // ============================================================
    public boolean updateLastActive(int userId) {
        String sql = "UPDATE users SET last_active = NOW() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("updateLastActive error: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // DELETE — remove user
    // Cascades to: bet_wallet, booking, captains, certification,
    //   coaches, commande, event_registrations, formation (coach),
    //   guests, likes, membership, players, posts, replies,
    //   session (coach), transaction
    // ============================================================
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("deleteUser error: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // AUTH — login (returns null if credentials wrong or user banned)
    // is_banned = 0 filters out banned accounts at query level.
    // ============================================================
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email=? AND password=? AND is_banned=0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, hashPassword(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = mapRow(rs);           // map before closing ResultSet
                    updateLastActive(u.getId());   // record login time
                    return u;
                }
            }

        } catch (SQLException e) {
            System.err.println("login error: " + e.getMessage());
        }
        return null;
    }
}