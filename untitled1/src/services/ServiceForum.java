package services;

import interfaces.IService;
import models.Forum;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceForum implements IService<Forum> {

    @Override
    public void add(Forum f) {
        String sql = "INSERT INTO `forums` (`name`, `description`, `icon`, `display_order`) VALUES (?,?,?,?)";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, f.getName());
            pstmt.setString(2, f.getDescription());
            pstmt.setString(3, f.getIcon());
            pstmt.setInt(4, f.getDisplayOrder());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                f.setId(rs.getInt(1));
            }
            System.out.println("Forum added! ID = " + f.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Forum> getAll() {
        List<Forum> forums = new ArrayList<>();
        String sql = "SELECT * FROM `forums`";
        try {
            Statement stmt = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Forum f = new Forum();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setDescription(rs.getString("description"));
                f.setIcon(rs.getString("icon"));
                f.setDisplayOrder(rs.getInt("display_order"));
                f.setCreatedAt(rs.getTimestamp("created_at"));
                forums.add(f);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return forums;
    }

    @Override
    public void update(Forum f) {
        String sql = "UPDATE `forums` SET `name`=?, `description`=?, `icon`=?, `display_order`=? WHERE `id`=?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, f.getName());
            pstmt.setString(2, f.getDescription());
            pstmt.setString(3, f.getIcon());
            pstmt.setInt(4, f.getDisplayOrder());
            pstmt.setInt(5, f.getId());
            pstmt.executeUpdate();
            System.out.println("Forum updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Forum f) {
        String sql = "DELETE FROM `forums` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, f.getId());
            pstmt.executeUpdate();
            System.out.println("Forum deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Find forum by ID
    public Forum getForumById(int id) {
        String sql = "SELECT * FROM `forums` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Forum f = new Forum();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setDescription(rs.getString("description"));
                f.setIcon(rs.getString("icon"));
                f.setDisplayOrder(rs.getInt("display_order"));
                f.setCreatedAt(rs.getTimestamp("created_at"));
                return f;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Find forum by name
    public Forum getForumByName(String name) {
        String sql = "SELECT * FROM `forums` WHERE `name` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Forum f = new Forum();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setDescription(rs.getString("description"));
                f.setIcon(rs.getString("icon"));
                f.setDisplayOrder(rs.getInt("display_order"));
                f.setCreatedAt(rs.getTimestamp("created_at"));
                return f;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Get forums sorted by display_order
    public List<Forum> getForumsSorted() {
        List<Forum> forums = new ArrayList<>();
        String sql = "SELECT * FROM `forums` ORDER BY `display_order` ASC";
        try {
            Statement stmt = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Forum f = new Forum();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                f.setDescription(rs.getString("description"));
                f.setIcon(rs.getString("icon"));
                f.setDisplayOrder(rs.getInt("display_order"));
                f.setCreatedAt(rs.getTimestamp("created_at"));
                forums.add(f);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return forums;
    }
}