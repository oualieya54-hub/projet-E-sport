package com.esport.models;

import java.time.LocalDateTime;

public class TournamentMatch {

    private int           id;
    private int           tournamentId;
    private int           round;
    private int           matchNumber;
    private int           team1Id;
    private int           team2Id;
    private int           scoreTeam1;
    private int           scoreTeam2;
    private int           winnerId;
    private LocalDateTime scheduledAt;
    private LocalDateTime playedAt;
    private MatchStatus   status;
    private String        streamUrl;

    public TournamentMatch() {}

    public TournamentMatch(int tournamentId, int round, int matchNumber,
                           int team1Id, int team2Id, LocalDateTime scheduledAt) {
        this.tournamentId = tournamentId;
        this.round        = round;
        this.matchNumber  = matchNumber;
        this.team1Id      = team1Id;
        this.team2Id      = team2Id;
        this.scheduledAt  = scheduledAt;
        this.status       = MatchStatus.SCHEDULED;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public int getTournamentId()                { return tournamentId; }
    public void setTournamentId(int tid)        { this.tournamentId = tid; }
    public int getRound()                       { return round; }
    public void setRound(int r)                 { this.round = r; }
    public int getMatchNumber()                 { return matchNumber; }
    public void setMatchNumber(int n)           { this.matchNumber = n; }
    public int getTeam1Id()                     { return team1Id; }
    public void setTeam1Id(int id)              { this.team1Id = id; }
    public int getTeam2Id()                     { return team2Id; }
    public void setTeam2Id(int id)              { this.team2Id = id; }
    public int getScoreTeam1()                  { return scoreTeam1; }
    public void setScoreTeam1(int s)            { this.scoreTeam1 = s; }
    public int getScoreTeam2()                  { return scoreTeam2; }
    public void setScoreTeam2(int s)            { this.scoreTeam2 = s; }
    public int getWinnerId()                    { return winnerId; }
    public void setWinnerId(int id)             { this.winnerId = id; }
    public LocalDateTime getScheduledAt()       { return scheduledAt; }
    public void setScheduledAt(LocalDateTime d) { this.scheduledAt = d; }
    public LocalDateTime getPlayedAt()          { return playedAt; }
    public void setPlayedAt(LocalDateTime d)    { this.playedAt = d; }
    public MatchStatus getStatus()              { return status; }
    public void setStatus(MatchStatus s)        { this.status = s; }
    public String getStreamUrl()                { return streamUrl; }
    public void setStreamUrl(String url)        { this.streamUrl = url; }

    @Override
    public String toString() {
        return "Match{id=" + id + ", round=" + round + ", T1=" + team1Id +
                " vs T2=" + team2Id + ", score=" + scoreTeam1 + "-" + scoreTeam2 +
                ", status=" + status + "}";
    }
}
