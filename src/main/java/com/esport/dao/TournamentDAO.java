package com.esport.dao;

import com.esport.config.DatabaseConnection;
import com.esport.model.Tournament;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TournamentDAO {

    // ── Row mapper ────────────────────────────────────────────────────────────
    private Tournament map(ResultSet rs) throws SQLException {
        Tournament t = new Tournament();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        t.setGame(rs.getString("game"));
        t.setFormat(Tournament.Format.fromLabel(rs.getString("format")));
        t.setStatus(Tournament.Status.valueOf(rs.getString("status").toUpperCase()));
        t.setMaxTeams(rs.getInt("max_teams"));
        t.setPrizePool(rs.getBigDecimal("prize_pool"));
        t.setEntryFee(rs.getBigDecimal("entry_fee"));
        t.setDescription(rs.getString("description"));
        t.setCreatedBy(rs.getInt("created_by"));
        Timestamp s = rs.getTimestamp("start_date");
        if (s != null) t.setStartDate(s.toLocalDateTime());
        Timestamp e = rs.getTimestamp("end_date");
        if (e != null) t.setEndDate(e.toLocalDateTime());
        Timestamp r = rs.getTimestamp("registration_deadline");
        if (r != null) t.setRegistrationDeadline(r.toLocalDateTime());
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) t.setCreatedAt(c.toLocalDateTime());
        return t;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public int create(Tournament t) throws SQLException {
        String sql = """
            INSERT INTO tournaments
            (name, game, format, status, max_teams, prize_pool, entry_fee,
             start_date, end_date, registration_deadline, description, created_by)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getGame());
            ps.setString(3, t.getFormat().getLabel());
            ps.setString(4, t.getStatus().name().toLowerCase());
            ps.setInt(5, t.getMaxTeams());
            ps.setBigDecimal(6, t.getPrizePool());
            ps.setBigDecimal(7, t.getEntryFee());
            ps.setTimestamp(8,  t.getStartDate() != null ? Timestamp.valueOf(t.getStartDate()) : null);
            ps.setTimestamp(9,  t.getEndDate()   != null ? Timestamp.valueOf(t.getEndDate())   : null);
            ps.setTimestamp(10, t.getRegistrationDeadline() != null
                    ? Timestamp.valueOf(t.getRegistrationDeadline()) : null);
            ps.setString(11, t.getDescription());
            ps.setInt(12, t.getCreatedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    // ── READ BY ID ────────────────────────────────────────────────────────────
    public Optional<Tournament> findById(Integer id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM tournaments WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        }
        return Optional.empty();
    }

    // ── READ ALL ──────────────────────────────────────────────────────────────

    public List<Tournament> findAll() throws SQLException {
        List<Tournament> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM tournaments ORDER BY start_date DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── SEARCH BY NAME ────────────────────────────────────────────────────────
    public List<Tournament> findByName(String name) throws SQLException {
        List<Tournament> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM tournaments WHERE name LIKE ?")) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── SEARCH BY GAME ────────────────────────────────────────────────────────

    public List<Tournament> findByGame(String game) throws SQLException {
        List<Tournament> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM tournaments WHERE game LIKE ?")) {
            ps.setString(1, "%" + game + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── SEARCH BY STATUS ──────────────────────────────────────────────────────
    public List<Tournament> findByStatus(Tournament.Status status) throws SQLException {
        List<Tournament> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM tournaments WHERE status=? ORDER BY start_date")) {
            ps.setString(1, status.name().toLowerCase());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean update(Tournament t) throws SQLException {
        String sql = """
            UPDATE tournaments SET name=?, game=?, format=?, status=?, max_teams=?,
            prize_pool=?, entry_fee=?, start_date=?, end_date=?,
            registration_deadline=?, description=? WHERE id=?
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getGame());
            ps.setString(3, t.getFormat().getLabel());
            ps.setString(4, t.getStatus().name().toLowerCase());
            ps.setInt(5, t.getMaxTeams());
            ps.setBigDecimal(6, t.getPrizePool());
            ps.setBigDecimal(7, t.getEntryFee());
            ps.setTimestamp(8,  t.getStartDate() != null ? Timestamp.valueOf(t.getStartDate()) : null);
            ps.setTimestamp(9,  t.getEndDate()   != null ? Timestamp.valueOf(t.getEndDate())   : null);
            ps.setTimestamp(10, t.getRegistrationDeadline() != null
                    ? Timestamp.valueOf(t.getRegistrationDeadline()) : null);
            ps.setString(11, t.getDescription());
            ps.setInt(12, t.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean delete(Integer id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM tournaments WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── BUSINESS METHODS ──────────────────────────────────────────────────────
    public int countRegisteredTeams(int tournamentId) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM tournament_registrations WHERE tournament_id=? AND status='approved'")) {
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int registerTeam(int tournamentId, int teamId) throws SQLException {
        String sql = "INSERT INTO tournament_registrations (tournament_id, team_id, status) VALUES (?,?,'approved')";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, teamId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public boolean isRegistrationOpen(int tournamentId) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT registration_deadline FROM tournaments WHERE id=?")) {
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp dl = rs.getTimestamp("registration_deadline");
                return dl == null || dl.toLocalDateTime().isAfter(LocalDateTime.now());
            }
        }
        return false;
    }

    public boolean updateStatus(int id, Tournament.Status status) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE tournaments SET status=? WHERE id=?")) {
            ps.setString(1, status.name().toLowerCase());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }
}