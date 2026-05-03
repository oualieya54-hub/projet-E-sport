package com.esport.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Event {

    private int           id;
    private String        title;
    private EventType     type;
    private String        game;
    private String        location;
    private boolean       isOnline;
    private String        platformLink;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer       capacity;
    private BigDecimal    ticketPrice;
    private EventStatus   status;
    private String        thumbnailUrl;
    private String        description;
    private int           organizerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Event() {}

    public Event(String title, EventType type, String game, LocalDateTime startDate,
                 LocalDateTime endDate, BigDecimal ticketPrice) {
        this.title       = title;
        this.type        = type;
        this.game        = game;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.ticketPrice = ticketPrice;
        this.status      = EventStatus.DRAFT;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }
    public EventType getType()                  { return type; }
    public void setType(EventType type)         { this.type = type; }
    public String getGame()                     { return game; }
    public void setGame(String game)            { this.game = game; }
    public String getLocation()                 { return location; }
    public void setLocation(String l)           { this.location = l; }
    public boolean isOnline()                   { return isOnline; }
    public void setOnline(boolean online)       { this.isOnline = online; }
    public String getPlatformLink()             { return platformLink; }
    public void setPlatformLink(String link)    { this.platformLink = link; }
    public LocalDateTime getStartDate()         { return startDate; }
    public void setStartDate(LocalDateTime d)   { this.startDate = d; }
    public LocalDateTime getEndDate()           { return endDate; }
    public void setEndDate(LocalDateTime d)     { this.endDate = d; }
    public Integer getCapacity()                { return capacity; }
    public void setCapacity(Integer c)          { this.capacity = c; }
    public BigDecimal getTicketPrice()          { return ticketPrice; }
    public void setTicketPrice(BigDecimal p)    { this.ticketPrice = p; }
    public EventStatus getStatus()              { return status; }
    public void setStatus(EventStatus status)   { this.status = status; }
    public String getThumbnailUrl()             { return thumbnailUrl; }
    public void setThumbnailUrl(String url)     { this.thumbnailUrl = url; }
    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }
    public int getOrganizerId()                 { return organizerId; }
    public void setOrganizerId(int id)          { this.organizerId = id; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime d)   { this.createdAt = d; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime d)   { this.updatedAt = d; }

    @Override
    public String toString() {
        return "Event{id=" + id + ", title='" + title + "', type=" + type +
                ", status=" + status + ", start=" + startDate + "}";
    }
}
