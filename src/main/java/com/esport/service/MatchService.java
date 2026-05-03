package com.esport.service;

import com.esport.iservice.IMatchService;
import com.esport.models.TournamentMatch;
import com.esport.models.MatchStatus;
import com.esport.utile.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchService implements IMatchService {

    @Override
    public void addMatch(TournamentMatch match) {
        String sql = "INSERT INTO tournament_matches (tournament_id, round, match_number, team1_id, team2_id, scheduled_at, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, match.getTournamentId());
            pstmt.setInt(2, match.getRound());
            pstmt.setInt(3, match.getMatchNumber());
            pstmt.setInt(4, match.getTeam1Id());
            pstmt.setInt(5, match.getTeam2Id());
            pstmt.setObject(6, match.getScheduledAt());
            pstmt.setString(7, match.getStatus().name());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    match.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMatch(TournamentMatch match) {
        String sql = "UPDATE tournament_matches SET tournament_id=?, round=?, match_number=?, team1_id=?, team2_id=?, score_team1=?, score_team2=?, scheduled_at=?, status=? WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, match.getTournamentId());
            pstmt.setInt(2, match.getRound());
            pstmt.setInt(3, match.getMatchNumber());
            pstmt.setInt(4, match.getTeam1Id());
            pstmt.setInt(5, match.getTeam2Id());
            pstmt.setInt(6, match.getScoreTeam1());
            pstmt.setInt(7, match.getScoreTeam2());
            pstmt.setObject(8, match.getScheduledAt());
            pstmt.setString(9, match.getStatus().name());
            pstmt.setInt(10, match.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMatch(int id) {
        String sql = "DELETE FROM tournament_matches WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TournamentMatch getMatchById(int id) {
        String sql = "SELECT * FROM tournament_matches WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMatch(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TournamentMatch> getMatchesByTournament(int tournamentId) {
        List<TournamentMatch> matches = new ArrayList<>();
        String sql = "SELECT * FROM tournament_matches WHERE tournament_id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tournamentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    matches.add(mapResultSetToMatch(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    private TournamentMatch mapResultSetToMatch(ResultSet rs) throws SQLException {
        TournamentMatch match = new TournamentMatch();
        match.setId(rs.getInt("id"));
        match.setTournamentId(rs.getInt("tournament_id"));
        match.setRound(rs.getInt("round"));
        match.setMatchNumber(rs.getInt("match_number"));
        match.setTeam1Id(rs.getInt("team1_id"));
        match.setTeam2Id(rs.getInt("team2_id"));
        match.setScoreTeam1(rs.getInt("score_team1"));
        match.setScoreTeam2(rs.getInt("score_team2"));
        
        Timestamp scheduledAt = rs.getTimestamp("scheduled_at");
        if (scheduledAt != null) match.setScheduledAt(scheduledAt.toLocalDateTime());
        
        match.setStatus(MatchStatus.valueOf(rs.getString("status")));
        return match;
    }
}
