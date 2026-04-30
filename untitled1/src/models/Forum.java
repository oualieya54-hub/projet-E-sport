package models;

import java.sql.Timestamp;

public class Forum {
    private int id;
    private String name;
    private String description;
    private String icon;
    private int displayOrder;
    private Timestamp createdAt;

    public Forum() {}

    public Forum(String name, String description, String icon, int displayOrder) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Forum{id=" + id + ", name='" + name + "', description='" + description + "'}";
    }
}
