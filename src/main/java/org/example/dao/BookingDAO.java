package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Booking;

import java.sql.*;

public class BookingDAO {

    public void book(Booking b) throws SQLException {
        String sql = "INSERT INTO Booking (id_session, id_eleve, statut_paiement) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getIdSession());
            ps.setInt(2, b.getIdEleve());
            ps.setString(3, b.getStatutPaiement());
            ps.executeUpdate();
            System.out.println("✅ Réservation effectuée !");
        }
    }

    public void confirmPayment(int idBooking) throws SQLException {
        String sql = "UPDATE Booking SET statut_paiement = 'confirmé' WHERE id_booking = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBooking);
            ps.executeUpdate();
            System.out.println("✅ Paiement confirmé !");
        }
    }

    public void cancel(int idBooking) throws SQLException {
        String sql = "DELETE FROM Booking WHERE id_booking = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBooking);
            ps.executeUpdate();
            System.out.println("✅ Réservation annulée !");
        }
    }
}