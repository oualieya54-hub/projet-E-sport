package com.esport.service;

import com.esport.iservice.IEventService;
import com.esport.models.Event;
import com.esport.models.EventStatus;
import com.esport.models.EventType;
import com.esport.utile.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService implements IEventService {

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO event (title, type, game, start_date, end_date, ticket_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getType().name());
            pstmt.setString(3, event.getGame());
            pstmt.setObject(4, event.getStartDate());
            pstmt.setObject(5, event.getEndDate());
            pstmt.setBigDecimal(6, event.getTicketPrice());
            pstmt.setString(7, event.getStatus().name());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEvent(Event event) {
        String sql = "UPDATE event SET title=?, type=?, game=?, start_date=?, end_date=?, ticket_price=?, status=? WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getType().name());
            pstmt.setString(3, event.getGame());
            pstmt.setObject(4, event.getStartDate());
            pstmt.setObject(5, event.getEndDate());
            pstmt.setBigDecimal(6, event.getTicketPrice());
            pstmt.setString(7, event.getStatus().name());
            pstmt.setInt(8, event.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEvent(int id) {
        String sql = "DELETE FROM event WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Event getEventById(int id) {
        String sql = "SELECT * FROM event WHERE id=?";
        try (Connection conn = MyDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";
        try (Connection conn = MyDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setType(EventType.valueOf(rs.getString("type")));
        event.setGame(rs.getString("game"));
        
        Timestamp startDate = rs.getTimestamp("start_date");
        if (startDate != null) event.setStartDate(startDate.toLocalDateTime());
        
        Timestamp endDate = rs.getTimestamp("end_date");
        if (endDate != null) event.setEndDate(endDate.toLocalDateTime());
        
        event.setTicketPrice(rs.getBigDecimal("ticket_price"));
        event.setStatus(EventStatus.valueOf(rs.getString("status")));
        return event;
    }
}
