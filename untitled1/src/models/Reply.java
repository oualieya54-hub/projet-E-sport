package models;

import java.sql.Timestamp;

public class Reply {
    private int id;
    private String content;
    private int userId;
    private int postId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isApproved;

    public Reply() {}

    public Reply(String content, int userId, int postId) {
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.isApproved = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    @Override
    public String toString() {
        return "Reply{id=" + id + ", userId=" + userId + ", postId=" + postId + ", content='" + content.substring(0, Math.min(20, content.length())) + "...'}";
    }
}