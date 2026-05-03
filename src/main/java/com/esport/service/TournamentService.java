package com.esport.service;

import com.esport.iservice.ITournamentService;
import com.esport.models.Tournament;
import com.esport.models.TournamentFormat;
import com.esport.models.TournamentStatus;
import com.esport.models.TournamentMatch;
import com.esport.models.MatchStatus;
import com.esport.utile.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class TournamentService implements ITournamentService {

    @Override
    public void addTournament(Tournament tournament) {
        String sql = "INSERT INTO tournaments (name, game, format, max_teams, prize_pool, entry_fee, start_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, tournament.getName());
            pstmt.setString(2, tournament.getGame());
            pstmt.setString(3, tournament.getFormat().getLabel());
            pstmt.setInt(4, tournament.getMaxTeams());
            pstmt.setBigDecimal(5, tournament.getPrizePool());
            pstmt.setBigDecimal(6, tournament.getEntryFee());
            pstmt.setObject(7, tournament.getStartDate());
            pstmt.setString(8, tournament.getStatus().name().toLowerCase());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tournament.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB Error: " + e.getMessage());
        }
    }

    @Override
    public void updateTournament(Tournament tournament) {
        String sql = "UPDATE tournaments SET name=?, game=?, format=?, max_teams=?, prize_pool=?, entry_fee=?, start_date=?, status=? WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tournament.getName());
            pstmt.setString(2, tournament.getGame());
            pstmt.setString(3, tournament.getFormat().getLabel());
            pstmt.setInt(4, tournament.getMaxTeams());
            pstmt.setBigDecimal(5, tournament.getPrizePool());
            pstmt.setBigDecimal(6, tournament.getEntryFee());
            pstmt.setObject(7, tournament.getStartDate());
            pstmt.setString(8, tournament.getStatus().name().toLowerCase());
            pstmt.setInt(9, tournament.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteTournament(int id) {
        String sql = "DELETE FROM tournaments WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB Error: " + e.getMessage());
        }
    }

    @Override
    public Tournament getTournamentById(int id) {
        String sql = "SELECT * FROM tournaments WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTournament(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Tournament> getAllTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments";
        try (Connection conn = MyDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tournaments.add(mapResultSetToTournament(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB Error: " + e.getMessage());
        }
        return tournaments;
    }

    // ADVANCED LOGIC
    @Override
    public void startTournament(int tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        if (tournament != null && tournament.getStatus() == TournamentStatus.UPCOMING) {
            tournament.setStatus(TournamentStatus.ONGOING);
            updateTournament(tournament);
            
            // Logic to generate brackets (mock example for advanced logic)
            System.out.println("Generating brackets for Tournament: " + tournament.getName());
            MatchService matchService = new MatchService();
            
            // Generate a random match for demonstration of advanced business logic
            TournamentMatch firstMatch = new TournamentMatch();
            firstMatch.setTournamentId(tournamentId);
            firstMatch.setRound(1);
            firstMatch.setMatchNumber(1);
            firstMatch.setTeam1Id(101); // Mock team ID
            firstMatch.setTeam2Id(102); // Mock team ID
            firstMatch.setScheduledAt(LocalDateTime.now().plusDays(1));
            firstMatch.setStatus(MatchStatus.SCHEDULED);
            
            matchService.addMatch(firstMatch);
            System.out.println("Brackets generated and saved to database.");
        }
    }

    private Tournament mapResultSetToTournament(ResultSet rs) throws SQLException {
        Tournament tournament = new Tournament();
        tournament.setId(rs.getInt("id"));
        tournament.setName(rs.getString("name"));
        tournament.setGame(rs.getString("game"));
        // Map from DB label back to Enum
        String dbFormat = rs.getString("format");
        for (TournamentFormat f : TournamentFormat.values()) {
            if (f.getLabel().equalsIgnoreCase(dbFormat) || f.name().equalsIgnoreCase(dbFormat)) {
                tournament.setFormat(f);
                break;
            }
        }
        
        tournament.setMaxTeams(rs.getInt("max_teams"));
        tournament.setPrizePool(rs.getBigDecimal("prize_pool"));
        tournament.setEntryFee(rs.getBigDecimal("entry_fee"));
        
        Timestamp startDate = rs.getTimestamp("start_date");
        if (startDate != null) tournament.setStartDate(startDate.toLocalDateTime());
        
        String dbStatus = rs.getString("status");
        if (dbStatus != null) {
            tournament.setStatus(TournamentStatus.valueOf(dbStatus.toUpperCase()));
        }
        return tournament;
    }
}
