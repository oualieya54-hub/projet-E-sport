package services;

import interfaces.IService;
import models.Like;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceLike implements IService<Like> {

    @Override
    public void add(Like l) {
        String sql = "INSERT INTO `likes` (`user_id`, `target_type`, `target_id`, `vote`) VALUES (?,?,?,?)";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, l.getUserId());
            pstmt.setString(2, l.getTargetType());
            pstmt.setInt(3, l.getTargetId());
            pstmt.setInt(4, l.getVote());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                l.setId(rs.getInt(1));
            }
            System.out.println("Like added! ID = " + l.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Like> getAll() {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM `likes`";
        try {
            Statement stmt = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Like l = new Like();
                l.setId(rs.getInt("id"));
                l.setUserId(rs.getInt("user_id"));
                l.setTargetType(rs.getString("target_type"));
                l.setTargetId(rs.getInt("target_id"));
                l.setVote(rs.getInt("vote"));
                l.setCreatedAt(rs.getTimestamp("created_at"));
                likes.add(l);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return likes;
    }

    @Override
    public void update(Like l) {
        String sql = "UPDATE `likes` SET `vote`=? WHERE `id`=?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, l.getVote());
            pstmt.setInt(2, l.getId());
            pstmt.executeUpdate();
            System.out.println("Like updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Like l) {
        String sql = "DELETE FROM `likes` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, l.getId());
            pstmt.executeUpdate();
            System.out.println("Like deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Get all likes for a specific target
    public List<Like> getLikesByTarget(String targetType, int targetId) {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM `likes` WHERE `target_type` = ? AND `target_id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, targetType);
            pstmt.setInt(2, targetId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Like l = new Like();
                l.setId(rs.getInt("id"));
                l.setUserId(rs.getInt("user_id"));
                l.setTargetType(rs.getString("target_type"));
                l.setTargetId(rs.getInt("target_id"));
                l.setVote(rs.getInt("vote"));
                l.setCreatedAt(rs.getTimestamp("created_at"));
                likes.add(l);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return likes;
    }

    // Get a specific like by user and target (to check if user already voted)
    public Like getLikeByUserAndTarget(int userId, String targetType, int targetId) {
        String sql = "SELECT * FROM `likes` WHERE `user_id` = ? AND `target_type` = ? AND `target_id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, targetType);
            pstmt.setInt(3, targetId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Like l = new Like();
                l.setId(rs.getInt("id"));
                l.setUserId(rs.getInt("user_id"));
                l.setTargetType(rs.getString("target_type"));
                l.setTargetId(rs.getInt("target_id"));
                l.setVote(rs.getInt("vote"));
                l.setCreatedAt(rs.getTimestamp("created_at"));
                return l;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Get vote sum for a target
    public int getNetVotesForTarget(String targetType, int targetId) {
        String sql = "SELECT COALESCE(SUM(vote), 0) FROM `likes` WHERE `target_type` = ? AND `target_id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, targetType);
            pstmt.setInt(2, targetId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
