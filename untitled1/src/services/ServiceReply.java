package services;

import interfaces.IService;
import models.Reply;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReply implements IService<Reply> {

    @Override
    public void add(Reply r) {
        String sql = "INSERT INTO `replies` (`content`, `user_id`, `post_id`, `is_approved`) VALUES (?,?,?,?)";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, r.getContent());
            pstmt.setInt(2, r.getUserId());
            pstmt.setInt(3, r.getPostId());
            pstmt.setBoolean(4, r.isApproved());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                r.setId(rs.getInt(1));
            }
            System.out.println("Reply added! ID = " + r.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Reply> getAll() {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM `replies`";
        try {
            Statement stmt = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Reply r = new Reply();
                r.setId(rs.getInt("id"));
                r.setContent(rs.getString("content"));
                r.setUserId(rs.getInt("user_id"));
                r.setPostId(rs.getInt("post_id"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setUpdatedAt(rs.getTimestamp("updated_at"));
                r.setApproved(rs.getBoolean("is_approved"));
                replies.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return replies;
    }

    @Override
    public void update(Reply r) {
        String sql = "UPDATE `replies` SET `content`=?, `user_id`=?, `post_id`=?, `is_approved`=?, `updated_at`=CURRENT_TIMESTAMP WHERE `id`=?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, r.getContent());
            pstmt.setInt(2, r.getUserId());
            pstmt.setInt(3, r.getPostId());
            pstmt.setBoolean(4, r.isApproved());
            pstmt.setInt(5, r.getId());
            pstmt.executeUpdate();
            System.out.println("Reply updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Reply r) {
        String sql = "DELETE FROM `replies` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, r.getId());
            pstmt.executeUpdate();
            System.out.println("Reply deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Find reply by ID
    public Reply getReplyById(int id) {
        String sql = "SELECT * FROM `replies` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Reply r = new Reply();
                r.setId(rs.getInt("id"));
                r.setContent(rs.getString("content"));
                r.setUserId(rs.getInt("user_id"));
                r.setPostId(rs.getInt("post_id"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setUpdatedAt(rs.getTimestamp("updated_at"));
                r.setApproved(rs.getBoolean("is_approved"));
                return r;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Get all replies for a specific post
    public List<Reply> getRepliesByPost(int postId) {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM `replies` WHERE `post_id` = ? ORDER BY `created_at` ASC";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reply r = new Reply();
                r.setId(rs.getInt("id"));
                r.setContent(rs.getString("content"));
                r.setUserId(rs.getInt("user_id"));
                r.setPostId(rs.getInt("post_id"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setUpdatedAt(rs.getTimestamp("updated_at"));
                r.setApproved(rs.getBoolean("is_approved"));
                replies.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return replies;
    }

    // Get all replies by a specific user
    public List<Reply> getRepliesByUser(int userId) {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM `replies` WHERE `user_id` = ? ORDER BY `created_at` DESC";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reply r = new Reply();
                r.setId(rs.getInt("id"));
                r.setContent(rs.getString("content"));
                r.setUserId(rs.getInt("user_id"));
                r.setPostId(rs.getInt("post_id"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setUpdatedAt(rs.getTimestamp("updated_at"));
                r.setApproved(rs.getBoolean("is_approved"));
                replies.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return replies;
    }
}
