package services;
import interfaces.IService;
import models.User;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ServiceUser implements IService<User> {
    @Override
    public void add(User u) {
        String sql = "INSERT INTO `users` (`username`, `email`, `password_hash`, `avatar_url`, `role`, `is_banned`) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getEmail());
            pstmt.setString(3, u.getPasswordHash());
            pstmt.setString(4, u.getAvatarUrl());
            pstmt.setString(5, u.getRole());
            pstmt.setBoolean(6, u.isBanned());

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                u.setId(rs.getInt(1));
            }
            System.out.println("User ajouté ID = " + u.getId());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<User> getAll() {

        //TODO
        //1-cree req ✅
        //2-executer req✅
        //3-Matching SQL <=> JAVA✅
        //4- retourner la liste ✅
        List<User> Users = new ArrayList<>();
        String sql = "SELECT * FROM `users`";

        try {
            Statement stm = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setAvatarUrl(rs.getString("avatar_url"));
                u.setRole(rs.getString("role"));
                u.setRegistrationDate(rs.getTimestamp("registration_date"));
                u.setLastActive(rs.getTimestamp("last_active"));
                u.setBanned(rs.getBoolean("is_banned"));
                Users.add(u);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        return Users;
    }

    @Override
    public void delete(User u) {
        String sql = "DELETE FROM `users` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, u.getId());
            pstmt.executeUpdate();
            System.out.println("User deleted successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void update(User u) {
        String sql = "UPDATE `users` SET `username`=?, `email`=?, `password_hash`=?, `avatar_url`=?, `role`=?, `is_banned`=? WHERE `id`=?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getEmail());
            pstmt.setString(3, u.getPasswordHash());
            pstmt.setString(4, u.getAvatarUrl());
            pstmt.setString(5, u.getRole());
            pstmt.setBoolean(6, u.isBanned());
            pstmt.setInt(7, u.getId());

            pstmt.executeUpdate();
            System.out.println("User updated successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Find user by ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM `users` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setAvatarUrl(rs.getString("avatar_url"));
                u.setRole(rs.getString("role"));
                u.setRegistrationDate(rs.getTimestamp("registration_date"));
                u.setLastActive(rs.getTimestamp("last_active"));
                u.setBanned(rs.getBoolean("is_banned"));
                return u;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Find user by username (already exists, but keep as is)
    public User getUserByUsername(String username) {
        // (you already have this, but ensure it's there)
        String sql = "SELECT * FROM `users` WHERE `username` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setAvatarUrl(rs.getString("avatar_url"));
                u.setRole(rs.getString("role"));
                u.setRegistrationDate(rs.getTimestamp("registration_date"));
                u.setLastActive(rs.getTimestamp("last_active"));
                u.setBanned(rs.getBoolean("is_banned"));
                return u;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Check if username already exists
    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM `users` WHERE `username` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
