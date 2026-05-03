package com.esport.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Tournament {

    private int              id;
    private String           name;
    private String           game;
    private TournamentFormat format;
    private TournamentStatus status;
    private int              maxTeams;
    private BigDecimal       prizePool;
    private BigDecimal       entryFee;
    private LocalDateTime    startDate;
    private LocalDateTime    endDate;
    private LocalDateTime    registrationDeadline;
    private String           bannerUrl;
    private String           description;
    private int              createdBy;
    private LocalDateTime    createdAt;
    private LocalDateTime    updatedAt;

    public Tournament() {}

    public Tournament(String name, String game, TournamentFormat format, int maxTeams,
                      BigDecimal prizePool, BigDecimal entryFee,
                      LocalDateTime startDate, LocalDateTime registrationDeadline) {
        this.name                 = name;
        this.game                 = game;
        this.format               = format;
        this.maxTeams             = maxTeams;
        this.prizePool            = prizePool;
        this.entryFee             = entryFee;
        this.startDate            = startDate;
        this.registrationDeadline = registrationDeadline;
        this.status               = TournamentStatus.UPCOMING;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }
    public String getGame()                     { return game; }
    public void setGame(String game)            { this.game = game; }
    public TournamentFormat getFormat()         { return format; }
    public void setFormat(TournamentFormat f)   { this.format = f; }
    public TournamentStatus getStatus()         { return status; }
    public void setStatus(TournamentStatus s)   { this.status = s; }
    public int getMaxTeams()                    { return maxTeams; }
    public void setMaxTeams(int maxTeams)       { this.maxTeams = maxTeams; }
    public BigDecimal getPrizePool()            { return prizePool; }
    public void setPrizePool(BigDecimal p)      { this.prizePool = p; }
    public BigDecimal getEntryFee()             { return entryFee; }
    public void setEntryFee(BigDecimal f)       { this.entryFee = f; }
    public LocalDateTime getStartDate()         { return startDate; }
    public void setStartDate(LocalDateTime d)   { this.startDate = d; }
    public LocalDateTime getEndDate()           { return endDate; }
    public void setEndDate(LocalDateTime d)     { this.endDate = d; }
    public LocalDateTime getRegistrationDeadline()      { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDateTime d){ this.registrationDeadline = d; }
    public String getBannerUrl()                { return bannerUrl; }
    public void setBannerUrl(String url)        { this.bannerUrl = url; }
    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }
    public int getCreatedBy()                   { return createdBy; }
    public void setCreatedBy(int id)            { this.createdBy = id; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime d)   { this.createdAt = d; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime d)   { this.updatedAt = d; }

    @Override
    public String toString() {
        return "Tournament{id=" + id + ", name='" + name + "', game='" + game +
                "', status=" + status + ", prize=$" + prizePool + "}";
    }
}
