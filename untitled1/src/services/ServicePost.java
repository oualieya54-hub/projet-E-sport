package services;

import interfaces.IService;
import models.Post;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePost implements IService<Post> {

    @Override
    public void add(Post p) {
        String sql = "INSERT INTO `posts` (`title`, `content`, `user_id`, `forum_id`, `views`, `is_pinned`, `is_locked`) VALUES (?,?,?,?,?,?,?)";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, p.getTitle());
            pstmt.setString(2, p.getContent());
            pstmt.setInt(3, p.getUserId());
            pstmt.setInt(4, p.getForumId());
            pstmt.setInt(5, p.getViews());
            pstmt.setBoolean(6, p.isPinned());
            pstmt.setBoolean(7, p.isLocked());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                p.setId(rs.getInt(1));
            }
            System.out.println("Post added! ID = " + p.getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM `posts`";
        try {
            Statement stmt = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("title"));
                p.setContent(rs.getString("content"));
                p.setUserId(rs.getInt("user_id"));
                p.setForumId(rs.getInt("forum_id"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                p.setUpdatedAt(rs.getTimestamp("updated_at"));
                p.setViews(rs.getInt("views"));
                p.setPinned(rs.getBoolean("is_pinned"));
                p.setLocked(rs.getBoolean("is_locked"));
                posts.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return posts;
    }

    @Override
    public void update(Post p) {
        String sql = "UPDATE `posts` SET `title`=?, `content`=?, `user_id`=?, `forum_id`=?, `views`=?, `is_pinned`=?, `is_locked`=?, `updated_at`=CURRENT_TIMESTAMP WHERE `id`=?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setString(1, p.getTitle());
            pstmt.setString(2, p.getContent());
            pstmt.setInt(3, p.getUserId());
            pstmt.setInt(4, p.getForumId());
            pstmt.setInt(5, p.getViews());
            pstmt.setBoolean(6, p.isPinned());
            pstmt.setBoolean(7, p.isLocked());
            pstmt.setInt(8, p.getId());
            pstmt.executeUpdate();
            System.out.println("Post updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Post p) {
        String sql = "DELETE FROM `posts` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, p.getId());
            pstmt.executeUpdate();
            System.out.println("Post deleted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Find post by ID
    public Post getPostById(int id) {
        String sql = "SELECT * FROM `posts` WHERE `id` = ?";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("title"));
                p.setContent(rs.getString("content"));
                p.setUserId(rs.getInt("user_id"));
                p.setForumId(rs.getInt("forum_id"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                p.setUpdatedAt(rs.getTimestamp("updated_at"));
                p.setViews(rs.getInt("views"));
                p.setPinned(rs.getBoolean("is_pinned"));
                p.setLocked(rs.getBoolean("is_locked"));
                return p;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Get all posts belonging to a specific forum
    public List<Post> getPostsByForum(int forumId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM `posts` WHERE `forum_id` = ? ORDER BY `is_pinned` DESC, `created_at` DESC";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, forumId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("title"));
                p.setContent(rs.getString("content"));
                p.setUserId(rs.getInt("user_id"));
                p.setForumId(rs.getInt("forum_id"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                p.setUpdatedAt(rs.getTimestamp("updated_at"));
                p.setViews(rs.getInt("views"));
                p.setPinned(rs.getBoolean("is_pinned"));
                p.setLocked(rs.getBoolean("is_locked"));
                posts.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return posts;
    }

    // Get all posts by a specific user
    public List<Post> getPostsByUser(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM `posts` WHERE `user_id` = ? ORDER BY `created_at` DESC";
        try {
            PreparedStatement pstmt = MyDataBase.getInstance().getCnx().prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("title"));
                p.setContent(rs.getString("content"));
                p.setUserId(rs.getInt("user_id"));
                p.setForumId(rs.getInt("forum_id"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                p.setUpdatedAt(rs.getTimestamp("updated_at"));
                p.setViews(rs.getInt("views"));
                p.setPinned(rs.getBoolean("is_pinned"));
                p.setLocked(rs.getBoolean("is_locked"));
                posts.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return posts;
    }
}
