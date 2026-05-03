package com.esport.service;

import com.esport.iservice.IBetService;
import com.esport.models.Bet;
import com.esport.models.BetStatus;
import com.esport.utile.MyDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BetService implements IBetService {

    @Override
    public void placeBet(Bet bet) {
        // Advanced Logic: Calculate odds based on pool before placing
        BigDecimal dynamicOdds = calculateDynamicOdds(bet.getMatchId(), bet.getBetOnTeamId());
        if(dynamicOdds.compareTo(BigDecimal.ONE) > 0) {
            bet.setOdds(dynamicOdds);
            bet.setPotentialWin(bet.getAmount().multiply(dynamicOdds));
        }

        String sql = "INSERT INTO bets (user_id, match_id, bet_on_team_id, amount, odds, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, bet.getUserId());
            pstmt.setInt(2, bet.getMatchId());
            pstmt.setInt(3, bet.getBetOnTeamId());
            pstmt.setBigDecimal(4, bet.getAmount());
            pstmt.setBigDecimal(5, bet.getOdds());
            pstmt.setString(6, bet.getStatus().name().toLowerCase());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bet.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB Error: " + e.getMessage());
        }
    }

    @Override
    public void updateBet(Bet bet) {
        String sql = "UPDATE bets SET status=? WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bet.getStatus().name().toLowerCase());
            pstmt.setInt(2, bet.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBet(int id) {
        String sql = "DELETE FROM bets WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Bet getBetById(int id) {
        String sql = "SELECT * FROM bets WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Bet> getBetsByMatch(int matchId) {
        List<Bet> bets = new ArrayList<>();
        String sql = "SELECT * FROM bets WHERE match_id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, matchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bets.add(mapResultSetToBet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bets;
    }

    // ADVANCED LOGIC: Pari Mutuel Odds System
    @Override
    public BigDecimal calculateDynamicOdds(int matchId, int teamId) {
        List<Bet> allBets = getBetsByMatch(matchId);
        if (allBets.isEmpty()) return new BigDecimal("1.5"); // default starting odds

        BigDecimal totalPool = BigDecimal.ZERO;
        BigDecimal teamPool = BigDecimal.ZERO;

        for (Bet b : allBets) {
            totalPool = totalPool.add(b.getAmount());
            if (b.getBetOnTeamId() == teamId) {
                teamPool = teamPool.add(b.getAmount());
            }
        }

        if (teamPool.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal("2.0"); // high odds if no one bet on them yet
        }

        // Minus 5% house edge
        BigDecimal netPool = totalPool.multiply(new BigDecimal("0.95"));
        
        // Odds = NetPool / TeamPool
        return netPool.divide(teamPool, 2, RoundingMode.HALF_UP);
    }

    @Override
    public void settleBetsForMatch(int matchId, int winningTeamId) {
        List<Bet> bets = getBetsByMatch(matchId);
        for (Bet bet : bets) {
            if (bet.getBetOnTeamId() == winningTeamId) {
                bet.setStatus(BetStatus.WON);
            } else {
                bet.setStatus(BetStatus.LOST);
            }
            updateBet(bet);
        }
    }

    private Bet mapResultSetToBet(ResultSet rs) throws SQLException {
        Bet bet = new Bet();
        bet.setId(rs.getInt("id"));
        bet.setUserId(rs.getInt("user_id"));
        bet.setMatchId(rs.getInt("match_id"));
        bet.setBetOnTeamId(rs.getInt("bet_on_team_id"));
        bet.setAmount(rs.getBigDecimal("amount"));
        bet.setOdds(rs.getBigDecimal("odds"));
        bet.setPotentialWin(rs.getBigDecimal("potential_win"));
        bet.setStatus(BetStatus.valueOf(rs.getString("status")));
        return bet;
    }
}
