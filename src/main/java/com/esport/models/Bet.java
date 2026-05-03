package com.esport.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bet {

    private int           id;
    private int           userId;
    private int           matchId;
    private int           betOnTeamId;
    private BigDecimal    amount;
    private BigDecimal    odds;
    private BigDecimal    potentialWin;
    private BetStatus     status;
    private LocalDateTime placedAt;
    private LocalDateTime settledAt;

    public Bet() {}

    public Bet(int userId, int matchId, int betOnTeamId,
               BigDecimal amount, BigDecimal odds) {
        this.userId      = userId;
        this.matchId     = matchId;
        this.betOnTeamId = betOnTeamId;
        this.amount      = amount;
        this.odds        = odds;
        this.potentialWin = amount.multiply(odds);
        this.status      = BetStatus.PENDING;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public int getUserId()                      { return userId; }
    public void setUserId(int id)               { this.userId = id; }
    public int getMatchId()                     { return matchId; }
    public void setMatchId(int id)              { this.matchId = id; }
    public int getBetOnTeamId()                 { return betOnTeamId; }
    public void setBetOnTeamId(int id)          { this.betOnTeamId = id; }
    public BigDecimal getAmount()               { return amount; }
    public void setAmount(BigDecimal a)         { this.amount = a; }
    public BigDecimal getOdds()                 { return odds; }
    public void setOdds(BigDecimal o)           { this.odds = o; }
    public BigDecimal getPotentialWin()         { return potentialWin; }
    public void setPotentialWin(BigDecimal p)   { this.potentialWin = p; }
    public BetStatus getStatus()                { return status; }
    public void setStatus(BetStatus s)          { this.status = s; }
    public LocalDateTime getPlacedAt()          { return placedAt; }
    public void setPlacedAt(LocalDateTime d)    { this.placedAt = d; }
    public LocalDateTime getSettledAt()         { return settledAt; }
    public void setSettledAt(LocalDateTime d)   { this.settledAt = d; }

    @Override
    public String toString() {
        return "Bet{id=" + id + ", userId=" + userId + ", matchId=" + matchId +
                ", amount=" + amount + ", odds=" + odds +
                ", potentialWin=" + potentialWin + ", status=" + status + "}";
    }
}
