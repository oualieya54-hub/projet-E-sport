package com.esport.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Event {

    public enum Type { LAN, ONLINE, MEETUP, BOOTCAMP, WATCH_PARTY, OTHER }
    public enum Status { DRAFT, PUBLISHED, CANCELLED, COMPLETED }

    private int           id;
    private String        title;
    private Type          type;
    private String        game;
    private String        location;
    private boolean       isOnline;
    private String        platformLink;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer       capacity;
    private BigDecimal    ticketPrice;
    private Status        status;
    private String        description;
    private int           organizerId;
    private LocalDateTime createdAt;

    public Event() {}

    public Event(String title, Type type, String game, LocalDateTime startDate,
                 LocalDateTime endDate, BigDecimal ticketPrice) {
        this.title = title; this.type = type; this.game = game;
        this.startDate = startDate; this.endDate = endDate;
        this.ticketPrice = ticketPrice; this.status = Status.DRAFT;
    }

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }
    public String getTitle()                         { return title; }
    public void setTitle(String title)               { this.title = title; }
    public Type getType()                            { return type; }
    public void setType(Type type)                   { this.type = type; }
    public String getGame()                          { return game; }
    public void setGame(String game)                 { this.game = game; }
    public String getLocation()                      { return location; }
    public void setLocation(String location)         { this.location = location; }
    public boolean isOnline()                        { return isOnline; }
    public void setOnline(boolean online)            { this.isOnline = online; }
    public String getPlatformLink()                  { return platformLink; }
    public void setPlatformLink(String platformLink) { this.platformLink = platformLink; }
    public LocalDateTime getStartDate()              { return startDate; }
    public void setStartDate(LocalDateTime startDate){ this.startDate = startDate; }
    public LocalDateTime getEndDate()                { return endDate; }
    public void setEndDate(LocalDateTime endDate)    { this.endDate = endDate; }
    public Integer getCapacity()                     { return capacity; }
    public void setCapacity(Integer capacity)        { this.capacity = capacity; }
    public BigDecimal getTicketPrice()               { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice){ this.ticketPrice = ticketPrice; }
    public Status getStatus()                        { return status; }
    public void setStatus(Status status)             { this.status = status; }
    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }
    public int getOrganizerId()                      { return organizerId; }
    public void setOrganizerId(int organizerId)      { this.organizerId = organizerId; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Event[id=%d, title='%s', type=%s, status=%s, start=%s]",
                id, title, type, status, startDate);
    }
}