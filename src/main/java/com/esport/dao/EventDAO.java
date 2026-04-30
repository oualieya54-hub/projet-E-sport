package com.esport.dao;

import com.esport.config.DatabaseConnection;
import com.esport.model.Event;

import java.sql.*;
import java.util.*;

public class EventDAO {

    private Event map(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(rs.getInt("id"));
        e.setTitle(rs.getString("title"));
        try { e.setType(Event.Type.valueOf(rs.getString("type").toUpperCase().replace(" ", "_"))); }
        catch (Exception ex) { e.setType(Event.Type.OTHER); }
        e.setGame(rs.getString("game"));
        e.setLocation(rs.getString("location"));
        e.setOnline(rs.getBoolean("is_online"));
        e.setPlatformLink(rs.getString("platform_link"));
        e.setTicketPrice(rs.getBigDecimal("ticket_price"));
        int cap = rs.getInt("capacity");
        e.setCapacity(rs.wasNull() ? null : cap);
        try { e.setStatus(Event.Status.valueOf(rs.getString("status").toUpperCase())); }
        catch (Exception ex) { e.setStatus(Event.Status.DRAFT); }
        e.setDescription(rs.getString("description"));
        e.setOrganizerId(rs.getInt("organizer_id"));
        Timestamp s = rs.getTimestamp("start_date");
        if (s != null) e.setStartDate(s.toLocalDateTime());
        Timestamp en = rs.getTimestamp("end_date");
        if (en != null) e.setEndDate(en.toLocalDateTime());
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) e.setCreatedAt(c.toLocalDateTime());
        return e;
    }

    public int create(Event ev) throws SQLException {
        String sql = """
            INSERT INTO events
            (title, type, game, location, is_online, platform_link,
             start_date, end_date, capacity, ticket_price, status, description, organizer_id)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ev.getTitle());
            ps.setString(2, ev.getType().name());
            ps.setString(3, ev.getGame());
            ps.setString(4, ev.getLocation());
            ps.setBoolean(5, ev.isOnline());
            ps.setString(6, ev.getPlatformLink());
            ps.setTimestamp(7, ev.getStartDate() != null ? Timestamp.valueOf(ev.getStartDate()) : null);
            ps.setTimestamp(8, ev.getEndDate()   != null ? Timestamp.valueOf(ev.getEndDate())   : null);
            if (ev.getCapacity() != null) ps.setInt(9, ev.getCapacity());
            else ps.setNull(9, Types.INTEGER);
            ps.setBigDecimal(10, ev.getTicketPrice());
            ps.setString(11, ev.getStatus().name().toLowerCase());
            ps.setString(12, ev.getDescription());
            ps.setInt(13, ev.getOrganizerId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public Optional<Event> findById(Integer id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM events WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        }
        return Optional.empty();
    }

    public List<Event> findAll() throws SQLException {
        List<Event> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM events ORDER BY start_date DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Event> findByTitle(String title) throws SQLException {
        List<Event> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM events WHERE title LIKE ?")) {
            ps.setString(1, "%" + title + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Event> findPublished() throws SQLException {
        List<Event> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM events WHERE status='published' ORDER BY start_date")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Event> findByType(Event.Type type) throws SQLException {
        List<Event> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT * FROM events WHERE type=?")) {
            ps.setString(1, type.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(Event ev) throws SQLException {
        String sql = """
            UPDATE events SET title=?, type=?, game=?, location=?, is_online=?,
            platform_link=?, start_date=?, end_date=?, capacity=?,
            ticket_price=?, status=?, description=? WHERE id=?
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ev.getTitle());
            ps.setString(2, ev.getType().name());
            ps.setString(3, ev.getGame());
            ps.setString(4, ev.getLocation());
            ps.setBoolean(5, ev.isOnline());
            ps.setString(6, ev.getPlatformLink());
            ps.setTimestamp(7, ev.getStartDate() != null ? Timestamp.valueOf(ev.getStartDate()) : null);
            ps.setTimestamp(8, ev.getEndDate()   != null ? Timestamp.valueOf(ev.getEndDate())   : null);
            if (ev.getCapacity() != null) ps.setInt(9, ev.getCapacity());
            else ps.setNull(9, Types.INTEGER);
            ps.setBigDecimal(10, ev.getTicketPrice());
            ps.setString(11, ev.getStatus().name().toLowerCase());
            ps.setString(12, ev.getDescription());
            ps.setInt(13, ev.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Integer id) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM events WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int registerUser(int eventId, int userId, String ticket) throws SQLException {
        String sql = "INSERT INTO event_registrations (event_id, user_id, ticket_code) VALUES (?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, eventId); ps.setInt(2, userId); ps.setString(3, ticket);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public int countRegistrations(int eventId) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM event_registrations WHERE event_id=?")) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public boolean markAttended(int eventId, int userId) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE event_registrations SET attended=TRUE WHERE event_id=? AND user_id=?")) {
            ps.setInt(1, eventId); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int eventId, Event.Status status) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE events SET status=? WHERE id=?")) {
            ps.setString(1, status.name().toLowerCase());
            ps.setInt(2, eventId);
            return ps.executeUpdate() > 0;
        }
    }
}