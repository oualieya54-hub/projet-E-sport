package com.esport.dao;

import com.esport.config.DatabaseConnection;
import com.esport.model.TournamentMatch;

import java.sql.*;
import java.util.*;

public class MatchDAO {

    private TournamentMatch map(ResultSet rs) throws SQLException {
        TournamentMatch m = new TournamentMatch();
        m.setId(rs.getInt("id"));
        m.setTournamentId(rs.getInt("tournament_id"));
        m.setRound(rs.getInt("round"));
        m.setMatchNumber(rs.getInt("match_number"));
        m.setTeam1Id(rs.getInt("team1_id"));
        m.setTeam2Id(rs.getInt("team2_id"));
        m.setScoreTeam1(rs.getInt("score_team1"));
        m.setScoreTeam2(rs.getInt("score_team2"));
        m.setWinnerId(rs.getInt("winner_id"));
        m.setStreamUrl(rs.getString("stream_url"));
        try { m.setStatus(TournamentMatch.Status.valueOf(rs.getString("status").toUpperCase())); }
        catch (Exception e) { m.setStatus(TournamentMatch.Status.SCHEDULED); }
        Timestamp s = rs.getTimestamp("scheduled_at");
        if (s != null) m.setScheduledAt(s.toLocalDateTime());
        Timestamp p = rs.getTimestamp("played_at");
        if (p != null) m.setPlayedAt(p.toLocalDateTime());
        return m;
    }

    public int create(TournamentMatch m) throws SQLException {
        String sql = """
            INSERT INTO tournament_matches
            (tournament_id, round, match_number, team1_id, team2_id,
             score_team1, score_team2, scheduled_at, stream_url, status)
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getTournamentId());
            ps.setInt(2, m.getRound());
            ps.setInt(3, m.getMatchNumber());
            ps.setInt(4, m.getTeam1Id());
            ps.setInt(5, m.getTeam2Id());
            ps.setInt(6, m.getScoreTeam1());
            ps.setInt(7, m.getScoreTeam2());
            ps.setTimestamp(8, m.getScheduledAt() != null ? Timestamp.valueOf(m.getScheduledAt()) : null);
            ps.setString(9, m.getStreamUrl());
            ps.setString(10, m.getStatus().name().toLowerCase());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public Optional<TournamentMatch> findById(Integer id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM tournament_matches WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        }
        return Optional.empty();
    }

    public List<TournamentMatch> findAll() throws SQLException {
        List<TournamentMatch> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tournament_matches ORDER BY tournament_id, round, match_number")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<TournamentMatch> findByTournament(int tournamentId) throws SQLException {
        List<TournamentMatch> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM tournament_matches WHERE tournament_id=? ORDER BY round, match_number")) {
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<TournamentMatch> findByRound(int tournamentId, int round) throws SQLException {
        List<TournamentMatch> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM tournament_matches WHERE tournament_id=? AND round=?")) {
            ps.setInt(1, tournamentId); ps.setInt(2, round);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<TournamentMatch> findLive() throws SQLException {
        List<TournamentMatch> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tournament_matches WHERE status='live'")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(TournamentMatch m) throws SQLException {
        String sql = """
            UPDATE tournament_matches SET round=?, match_number=?, team1_id=?, team2_id=?,
            score_team1=?, score_team2=?, winner_id=?, scheduled_at=?,
            played_at=?, status=?, stream_url=? WHERE id=?
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, m.getRound()); ps.setInt(2, m.getMatchNumber());
            ps.setInt(3, m.getTeam1Id()); ps.setInt(4, m.getTeam2Id());
            ps.setInt(5, m.getScoreTeam1()); ps.setInt(6, m.getScoreTeam2());
            if (m.getWinnerId() > 0) ps.setInt(7, m.getWinnerId());
            else ps.setNull(7, Types.INTEGER);
            ps.setTimestamp(8, m.getScheduledAt() != null ? Timestamp.valueOf(m.getScheduledAt()) : null);
            ps.setTimestamp(9, m.getPlayedAt()   != null ? Timestamp.valueOf(m.getPlayedAt())   : null);
            ps.setString(10, m.getStatus().name().toLowerCase());
            ps.setString(11, m.getStreamUrl());
            ps.setInt(12, m.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Integer id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM tournament_matches WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateScore(int id, int s1, int s2) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE tournament_matches SET score_team1=?, score_team2=? WHERE id=?")) {
            ps.setInt(1, s1); ps.setInt(2, s2); ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean completeMatch(int id, int winner, int s1, int s2) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE tournament_matches SET winner_id=?, score_team1=?, score_team2=?, status='completed', played_at=NOW() WHERE id=?")) {
            ps.setInt(1, winner); ps.setInt(2, s1); ps.setInt(3, s2); ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int id, TournamentMatch.Status status) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE tournament_matches SET status=? WHERE id=?")) {
            ps.setString(1, status.name().toLowerCase());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }
}