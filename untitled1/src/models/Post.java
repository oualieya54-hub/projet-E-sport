package models;

import java.sql.Timestamp;

public class Post {
    private int id;
    private String title;
    private String content;
    private int userId;
    private int forumId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int views;
    private boolean isPinned;
    private boolean isLocked;

    public Post() {}

    public Post(String title, String content, int userId, int forumId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.forumId = forumId;
        this.views = 0;
        this.isPinned = false;
        this.isLocked = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getForumId() { return forumId; }
    public void setForumId(int forumId) { this.forumId = forumId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }

    @Override
    public String toString() {
        return "Post{id=" + id + ", title='" + title + "', userId=" + userId + ", forumId=" + forumId + "}";
    }
}
