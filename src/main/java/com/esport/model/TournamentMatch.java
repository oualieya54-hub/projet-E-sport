package com.esport.model;

import java.time.LocalDateTime;

public class TournamentMatch {

    public enum Status { SCHEDULED, LIVE, COMPLETED, CANCELLED }

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
    private Status        status;
    private String        streamUrl;

    public TournamentMatch() {}

    public TournamentMatch(int tournamentId, int round, int matchNumber,
                           int team1Id, int team2Id, LocalDateTime scheduledAt) {
        this.tournamentId = tournamentId; this.round = round;
        this.matchNumber = matchNumber; this.team1Id = team1Id;
        this.team2Id = team2Id; this.scheduledAt = scheduledAt;
        this.status = Status.SCHEDULED;
    }

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }
    public int getTournamentId()                     { return tournamentId; }
    public void setTournamentId(int tournamentId)    { this.tournamentId = tournamentId; }
    public int getRound()                            { return round; }
    public void setRound(int round)                  { this.round = round; }
    public int getMatchNumber()                      { return matchNumber; }
    public void setMatchNumber(int matchNumber)      { this.matchNumber = matchNumber; }
    public int getTeam1Id()                          { return team1Id; }
    public void setTeam1Id(int team1Id)              { this.team1Id = team1Id; }
    public int getTeam2Id()                          { return team2Id; }
    public void setTeam2Id(int team2Id)              { this.team2Id = team2Id; }
    public int getScoreTeam1()                       { return scoreTeam1; }
    public void setScoreTeam1(int scoreTeam1)        { this.scoreTeam1 = scoreTeam1; }
    public int getScoreTeam2()                       { return scoreTeam2; }
    public void setScoreTeam2(int scoreTeam2)        { this.scoreTeam2 = scoreTeam2; }
    public int getWinnerId()                         { return winnerId; }
    public void setWinnerId(int winnerId)            { this.winnerId = winnerId; }
    public LocalDateTime getScheduledAt()            { return scheduledAt; }
    public void setScheduledAt(LocalDateTime d)      { this.scheduledAt = d; }
    public LocalDateTime getPlayedAt()               { return playedAt; }
    public void setPlayedAt(LocalDateTime d)         { this.playedAt = d; }
    public Status getStatus()                        { return status; }
    public void setStatus(Status status)             { this.status = status; }
    public String getStreamUrl()                     { return streamUrl; }
    public void setStreamUrl(String streamUrl)       { this.streamUrl = streamUrl; }

    @Override
    public String toString() {
        return String.format("Match[id=%d, round=%d, Team%d vs Team%d, score=%d-%d, status=%s]",
                id, round, team1Id, team2Id, scoreTeam1, scoreTeam2, status);
    }

        // Add these two methods HERE:
        public int getScore1() {
            return scoreTeam1;
        }

        public int getScore2() {
            return scoreTeam2;
        }
    }  // <- closing brace of the class
