package models;

import java.sql.Timestamp;

public class User {



    private int       id;
    private String    nom;
    private String    pseudo;
    private String    email;
    private String    password;
    private String    avatarUrl;
    private String    role;          // 'player' | 'captain' | 'coach' | 'guest' | 'moderator' | 'admin'
    private int       points;        // loyalty points balance
    private String    bio;
    private Timestamp lastActive;
    private boolean   isBanned;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String    googleId;
    private String    discordId;

    // ============================================================
    // Constructors
    // ============================================================

    public User() {}

    // Minimal constructor for registration
    public User(String nom, String pseudo, String email, String password) {
        this.nom      = nom;
        this.pseudo   = pseudo;
        this.email    = email;
        this.password = password;
        this.role     = "player";
        this.points   = 0;
        this.isBanned = false;
    }

    // Full constructor
    public User(String nom, String pseudo, String email, String password,
                String avatarUrl, String role) {
        this.nom       = nom;
        this.pseudo    = pseudo;
        this.email     = email;
        this.password  = password;
        this.avatarUrl = avatarUrl;
        this.role      = role != null ? role : "player";
        this.points    = 0;
        this.isBanned  = false;
    }

    // ============================================================
    // Getters & Setters
    // ============================================================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Timestamp getLastActive() { return lastActive; }
    public void setLastActive(Timestamp lastActive) { this.lastActive = lastActive; }

    public boolean getIsBanned() { return isBanned; }
    public void setIsBanned(boolean isBanned) { this.isBanned = isBanned; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public String getDiscordId() { return discordId; }
    public void setDiscordId(String discordId) { this.discordId = discordId; }

    // ============================================================
    // Convenience helpers
    // ============================================================

    public boolean isAdmin()     { return "admin".equals(role); }
    public boolean isModerator() { return "moderator".equals(role) || "admin".equals(role); }
    public boolean isCaptain()   { return "captain".equals(role); }
    public boolean isCoach()     { return "coach".equals(role); }

    @Override
    public String toString() {
        return "User{id=" + id + ", pseudo='" + pseudo + "', role='" + role +
                "', points=" + points + ", banned=" + isBanned + "}";
    }
}
