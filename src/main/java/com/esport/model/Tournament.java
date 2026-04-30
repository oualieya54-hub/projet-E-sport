package com.esport.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Tournament {

    public enum Format {
        SINGLE_ELIMINATION("Single Elimination"),
        DOUBLE_ELIMINATION("Double Elimination"),
        ROUND_ROBIN("Round Robin"),
        SWISS("Swiss");

        private final String label;
        Format(String label) { this.label = label; }
        public String getLabel() { return label; }

        public static Format fromLabel(String label) {
            for (Format f : values())
                if (f.label.equalsIgnoreCase(label)) return f;
            return SINGLE_ELIMINATION;
        }
    }

    public enum Status {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }

    private int           id;
    private String        name;
    private String        game;
    private Format        format;
    private Status        status;
    private int           maxTeams;
    private BigDecimal    prizePool;
    private BigDecimal    entryFee;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime registrationDeadline;
    private String        description;
    private int           createdBy;
    private LocalDateTime createdAt;

    public Tournament() {}

    public Tournament(String name, String game, Format format, int maxTeams,
                      BigDecimal prizePool, BigDecimal entryFee,
                      LocalDateTime startDate, LocalDateTime registrationDeadline) {
        this.name = name; this.game = game; this.format = format;
        this.maxTeams = maxTeams; this.prizePool = prizePool;
        this.entryFee = entryFee; this.startDate = startDate;
        this.registrationDeadline = registrationDeadline;
        this.status = Status.UPCOMING;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }
    public String getName()                          { return name; }
    public void setName(String name)                 { this.name = name; }
    public String getGame()                          { return game; }
    public void setGame(String game)                 { this.game = game; }
    public Format getFormat()                        { return format; }
    public void setFormat(Format format)             { this.format = format; }
    public Status getStatus()                        { return status; }
    public void setStatus(Status status)             { this.status = status; }
    public int getMaxTeams()                         { return maxTeams; }
    public void setMaxTeams(int maxTeams)            { this.maxTeams = maxTeams; }
    public BigDecimal getPrizePool()                 { return prizePool; }
    public void setPrizePool(BigDecimal prizePool)   { this.prizePool = prizePool; }
    public BigDecimal getEntryFee()                  { return entryFee; }
    public void setEntryFee(BigDecimal entryFee)     { this.entryFee = entryFee; }
    public LocalDateTime getStartDate()              { return startDate; }
    public void setStartDate(LocalDateTime startDate){ this.startDate = startDate; }
    public LocalDateTime getEndDate()                { return endDate; }
    public void setEndDate(LocalDateTime endDate)    { this.endDate = endDate; }
    public LocalDateTime getRegistrationDeadline()            { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDateTime d)      { this.registrationDeadline = d; }
    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }
    public int getCreatedBy()                        { return createdBy; }
    public void setCreatedBy(int createdBy)          { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Tournament[id=%d, name='%s', game='%s', format=%s, status=%s, teams=%d, prize=$%s]",
                id, name, game, format.getLabel(), status, maxTeams, prizePool);
    }
}