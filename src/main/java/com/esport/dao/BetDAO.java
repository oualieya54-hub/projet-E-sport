package com.esport.dao;

import com.esport.config.DatabaseConnection;
import com.esport.model.Bet;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class BetDAO {

    private Bet map(ResultSet rs) throws SQLException {
        Bet b = new Bet();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setMatchId(rs.getInt("match_id"));
        b.setBetOnTeamId(rs.getInt("bet_on_team_id"));
        b.setAmount(rs.getBigDecimal("amount"));
        b.setOdds(rs.getBigDecimal("odds"));
        b.setPotentialWin(rs.getBigDecimal("potential_win"));
        try { b.setStatus(Bet.Status.valueOf(rs.getString("status").toUpperCase())); }
        catch (Exception e) { b.setStatus(Bet.Status.PENDING); }
        Timestamp p = rs.getTimestamp("placed_at");
        if (p != null) b.setPlacedAt(p.toLocalDateTime());
        Timestamp s = rs.getTimestamp("settled_at");
        if (s != null) b.setSettledAt(s.toLocalDateTime());
        return b;
    }

    @Override
    public int create(Bet bet) throws SQLException {
        String sql = "INSERT INTO bets (user_id, match_id, bet_on_team_id, amount, odds) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bet.getUserId()); ps.setInt(2, bet.getMatchId());
            ps.setInt(3, bet.getBetOnTeamId());
            ps.setBigDecimal(4, bet.getAmount()); ps.setBigDecimal(5, bet.getOdds());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    @Override
    public Optional<Bet> findById(Integer id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM bets WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Bet> findAll() throws SQLException {
        List<Bet> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM bets ORDER BY placed_at DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<Bet> findByUser(int userId) throws SQLException {
        List<Bet> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM bets WHERE user_id=? ORDER BY placed_at DESC")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<Bet> findByMatch(int matchId) throws SQLException {
        List<Bet> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM bets WHERE match_id=?")) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<Bet> findPendingByMatch(int matchId) throws SQLException {
        List<Bet> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM bets WHERE match_id=? AND status='pending'")) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public boolean update(Bet bet) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE bets SET status=?, settled_at=? WHERE id=?")) {
            ps.setString(1, bet.getStatus().name().toLowerCase());
            ps.setTimestamp(2, bet.getSettledAt() != null ? Timestamp.valueOf(bet.getSettledAt()) : null);
            ps.setInt(3, bet.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM bets WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int settleBets(int matchId, int winnerTeamId) throws SQLException {
        List<Bet> pending = findPendingByMatch(matchId);
        int count = 0;
        for (Bet bet : pending) {
            if (bet.getBetOnTeamId() == winnerTeamId) {
                updateBetStatus(bet.getId(), "won");
                creditWinnings(bet.getUserId(), bet.getPotentialWin());
            } else {
                updateBetStatus(bet.getId(), "lost");
            }
            count++;
        }
        return count;
    }

    private void updateBetStatus(int betId, String status) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE bets SET status=?, settled_at=NOW() WHERE id=?")) {
            ps.setString(1, status); ps.setInt(2, betId);
            ps.executeUpdate();
        }
    }

    @Override
    public BigDecimal getWalletBalance(int userId) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT balance FROM bet_wallet WHERE user_id=?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBigDecimal("balance");
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean createWallet(int userId) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("INSERT IGNORE INTO bet_wallet (user_id) VALUES (?)")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deposit(int userId, BigDecimal amount) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE bet_wallet SET balance=balance+?, total_deposited=total_deposited+? WHERE user_id=?")) {
            ps.setBigDecimal(1, amount); ps.setBigDecimal(2, amount); ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deduct(int userId, BigDecimal amount) throws SQLException {
        if (getWalletBalance(userId).compareTo(amount) < 0)
            throw new SQLException("Insufficient wallet balance.");
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE bet_wallet SET balance=balance-? WHERE user_id=?")) {
            ps.setBigDecimal(1, amount); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean creditWinnings(int userId, BigDecimal amount) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE bet_wallet SET balance=balance+?, total_won=total_won+? WHERE user_id=?")) {
            ps.setBigDecimal(1, amount); ps.setBigDecimal(2, amount); ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }
}