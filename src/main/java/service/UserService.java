package service;

import models.*;
import utils.DatabaseConnection;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

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

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (nom, pseudo, email, password, avatar_url, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false); // Transaction

            ps.setString(1, user.getNom());
            ps.setString(2, user.getPseudo());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashPassword(user.getPassword()));
            ps.setString(5, user.getAvatarUrl());
            ps.setString(6, user.getRole() != null ? user.getRole() : "Player");

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    user.setId(userId);

                    if (user instanceof Player) {
                        Player p = (Player) user;
                        String subSql = "INSERT INTO players (id, jeu, rang, badges, avatar) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement subPs = conn.prepareStatement(subSql)) {
                            subPs.setInt(1, userId);
                            subPs.setString(2, p.getJeu());
                            subPs.setString(3, p.getRang());
                            subPs.setString(4, p.getBadges());
                            subPs.setString(5, p.getAvatar());
                            subPs.executeUpdate();
                        }
                    } else if (user instanceof Captain) {
                        Captain c = (Captain) user;
                        String subSql = "INSERT INTO captains (id, jeu, rang, badges, avatar) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement subPs = conn.prepareStatement(subSql)) {
                            subPs.setInt(1, userId);
                            subPs.setString(2, c.getJeu());
                            subPs.setString(3, c.getRang());
                            subPs.setString(4, c.getBadges());
                            subPs.setString(5, c.getAvatar());
                            subPs.executeUpdate();
                        }
                    } else if (user instanceof Coach) {
                        Coach c = (Coach) user;
                        String subSql = "INSERT INTO coaches (id, specialite) VALUES (?, ?)";
                        try (PreparedStatement subPs = conn.prepareStatement(subSql)) {
                            subPs.setInt(1, userId);
                            subPs.setString(2, c.getSpecialite());
                            subPs.executeUpdate();
                        }
                    } else if (user instanceof Guest) {
                        Guest g = (Guest) user;
                        String subSql = "INSERT INTO guests (id, coins) VALUES (?, ?)";
                        try (PreparedStatement subPs = conn.prepareStatement(subSql)) {
                            subPs.setInt(1, userId);
                            subPs.setInt(2, g.getCoins());
                            subPs.executeUpdate();
                        }
                    }

                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("addUser error: " + e.getMessage());
            return false;
        }
    }

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

    public User getFullUserById(int id, String role) {
        String sql = "";
        if ("Player".equalsIgnoreCase(role)) {
            sql = "SELECT u.*, p.jeu, p.rang, p.badges, p.avatar FROM users u LEFT JOIN players p ON u.id = p.id WHERE u.id = ?";
        } else if ("Captain".equalsIgnoreCase(role)) {
            sql = "SELECT u.*, c.jeu, c.rang, c.badges, c.avatar FROM users u LEFT JOIN captains c ON u.id = c.id WHERE u.id = ?";
        } else if ("Coach".equalsIgnoreCase(role)) {
            sql = "SELECT u.*, c.specialite FROM users u LEFT JOIN coaches c ON u.id = c.id WHERE u.id = ?";
        } else if ("Guest".equalsIgnoreCase(role)) {
            sql = "SELECT u.*, g.coins FROM users u LEFT JOIN guests g ON u.id = g.id WHERE u.id = ?";
        } else {
            sql = "SELECT * FROM users WHERE id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if ("Player".equalsIgnoreCase(role)) {
                        Player p = new Player();
                        copyBaseUser(mapRow(rs), p);
                        p.setJeu(rs.getString("jeu"));
                        p.setRang(rs.getString("rang"));
                        p.setBadges(rs.getString("badges"));
                        p.setAvatar(rs.getString("avatar"));
                        return p;
                    } else if ("Captain".equalsIgnoreCase(role)) {
                        Captain c = new Captain();
                        copyBaseUser(mapRow(rs), c);
                        c.setJeu(rs.getString("jeu"));
                        c.setRang(rs.getString("rang"));
                        c.setBadges(rs.getString("badges"));
                        c.setAvatar(rs.getString("avatar"));
                        return c;
                    } else if ("Coach".equalsIgnoreCase(role)) {
                        Coach c = new Coach();
                        copyBaseUser(mapRow(rs), c);
                        c.setSpecialite(rs.getString("specialite"));
                        return c;
                    } else if ("Guest".equalsIgnoreCase(role)) {
                        Guest g = new Guest();
                        copyBaseUser(mapRow(rs), g);
                        g.setCoins(rs.getInt("coins"));
                        return g;
                    } else {
                        return mapRow(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("getFullUserById error: " + e.getMessage());
        }
        return null;
    }

    private void copyBaseUser(User source, User target) {
        target.setId(source.getId());
        target.setNom(source.getNom());
        target.setPseudo(source.getPseudo());
        target.setEmail(source.getEmail());
        target.setPassword(source.getPassword());
        target.setAvatarUrl(source.getAvatarUrl());
        target.setRole(source.getRole());
        target.setPoints(source.getPoints());
        target.setIsBanned(source.getIsBanned());
        target.setLastActive(source.getLastActive());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
    }

    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, hashPassword(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = mapRow(rs);
                    updateLastActive(u.getId());
                    return getFullUserById(u.getId(), u.getRole());
                }
            }
        } catch (SQLException e) {
            System.err.println("login error: " + e.getMessage());
        }
        return null;
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET nom=?, pseudo=?, avatar_url=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            ps.setString(1, user.getNom());
            ps.setString(2, user.getPseudo());
            ps.setString(3, user.getAvatarUrl());
            ps.setInt(4, user.getId());
            int affected = ps.executeUpdate();
            
            if (affected > 0) {
                if (user instanceof Player) {
                    Player p = (Player) user;
                    String subSql = "UPDATE players SET jeu=?, rang=?, badges=?, avatar=? WHERE id=?";
                    try (PreparedStatement subPs = conn.prepareStatement(subSql)) {
                        subPs.setString(1, p.getJeu());
                        subPs.setString(2, p.getRang());
                        subPs.setString(3, p.getBadges());
                        subPs.setString(4, p.getAvatar());
                        subPs.setInt(5, p.getId());
                        subPs.executeUpdate();
                    }
                } else if (user instanceof Captain) {
                    Captain c = (Captain) user;
                    String subSql = "UPDATE captains SET jeu=?, rang=?, badges=?, avatar=? WHERE id=?";
                    try (PreparedStatement subPs = conn.prepareStatement(subSql)) {
                        subPs.setString(1, c.getJeu());
                        subPs.setString(2, c.getRang());
                        subPs.setString(3, c.getBadges());
                        subPs.setString(4, c.getAvatar());
                        subPs.setInt(5, c.getId());
                        subPs.executeUpdate();
                    }
                } else if (user instanceof Coach) {
                    Coach c = (Coach) user;
                    String subSql = "UPDATE coaches SET specialite=? WHERE id=?";
                    try (PreparedStatement subPs = conn.prepareStatement(subSql)) {
                        subPs.setString(1, c.getSpecialite());
                        subPs.setInt(2, c.getId());
                        subPs.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            System.err.println("updateUser error: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        String checkSql = "SELECT id FROM users WHERE id=? AND password=?";
        String updateSql = "UPDATE users SET password=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql);
             PreparedStatement updatePs = conn.prepareStatement(updateSql)) {

            checkPs.setInt(1, userId);
            checkPs.setString(2, hashPassword(oldPassword));
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    updatePs.setString(1, hashPassword(newPassword));
                    updatePs.setInt(2, userId);
                    return updatePs.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("updatePassword error: " + e.getMessage());
        }
        return false;
    }

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

    public boolean updateLastActive(int userId) {
        String sql = "UPDATE users SET last_active = NOW() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

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
}