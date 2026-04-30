package models;

import java.sql.Timestamp;

public class Like {
    private int id;
    private int userId;
    private String targetType;   // "post" or "reply"
    private int targetId;
    private int vote;            // 1 = upvote, -1 = downvote
    private Timestamp createdAt;

    public Like() {}

    public Like(int userId, String targetType, int targetId, int vote) {
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.vote = vote;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public int getTargetId() { return targetId; }
    public void setTargetId(int targetId) { this.targetId = targetId; }
    public int getVote() { return vote; }
    public void setVote(int vote) { this.vote = vote; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Like{id=" + id + ", userId=" + userId + ", targetType='" + targetType + "', targetId=" + targetId + ", vote=" + vote + "}";
    }
}
