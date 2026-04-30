package com.esport.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bet {

    public enum Status { PENDING, WON, LOST, CANCELLED, REFUNDED }

    private int           id;
    private int           userId;
    private int           matchId;
    private int           betOnTeamId;
    private BigDecimal    amount;
    private BigDecimal    odds;
    private BigDecimal    potentialWin;
    private Status        status;
    private LocalDateTime placedAt;
    private LocalDateTime settledAt;

    public Bet() {}

    public Bet(int userId, int matchId, int betOnTeamId,
               BigDecimal amount, BigDecimal odds) {
        this.userId = userId; this.matchId = matchId;
        this.betOnTeamId = betOnTeamId; this.amount = amount;
        this.odds = odds;
        this.potentialWin = amount.multiply(odds);
        this.status = Status.PENDING;
    }

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }
    public int getUserId()                           { return userId; }
    public void setUserId(int userId)                { this.userId = userId; }
    public int getMatchId()                          { return matchId; }
    public void setMatchId(int matchId)              { this.matchId = matchId; }
    public int getBetOnTeamId()                      { return betOnTeamId; }
    public void setBetOnTeamId(int betOnTeamId)      { this.betOnTeamId = betOnTeamId; }
    public BigDecimal getAmount()                    { return amount; }
    public void setAmount(BigDecimal amount)         { this.amount = amount; }
    public BigDecimal getOdds()                      { return odds; }
    public void setOdds(BigDecimal odds)             { this.odds = odds; }
    public BigDecimal getPotentialWin()              { return potentialWin; }
    public void setPotentialWin(BigDecimal p)        { this.potentialWin = p; }
    public Status getStatus()                        { return status; }
    public void setStatus(Status status)             { this.status = status; }
    public LocalDateTime getPlacedAt()               { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt)  { this.placedAt = placedAt; }
    public LocalDateTime getSettledAt()              { return settledAt; }
    public void setSettledAt(LocalDateTime settledAt){ this.settledAt = settledAt; }

    @Override
    public String toString() {
        return String.format("Bet[id=%d, user=%d, match=%d, team=%d, amount=$%s, odds=%s, win=$%s, status=%s]",
                id, userId, matchId, betOnTeamId, amount, odds, potentialWin, status);
    }
}