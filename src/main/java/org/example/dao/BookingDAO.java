package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public List<Booking> getByEleve(int idEleve) throws SQLException {
        List<Booking> liste = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE id_eleve = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEleve);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Booking(
                        rs.getInt("id_booking"),
                        rs.getInt("id_session"),
                        rs.getInt("id_eleve"),
                        rs.getString("statut_paiement")
                ));
            }
        }
        return liste;
    }

    public List<Booking> getAll() throws SQLException {
        List<Booking> liste = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Booking(
                        rs.getInt("id_booking"),
                        rs.getInt("id_session"),
                        rs.getInt("id_eleve"),
                        rs.getString("statut_paiement")
                ));
            }
        }
        return liste;
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
    //Metier:Voir tous les élèves inscrits à une session donnée.
    public List<Booking> getBookingsBySession(int idSession) throws SQLException {
        List<Booking> liste = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE id_session = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSession);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Booking(
                        rs.getInt("id_booking"),
                        rs.getInt("id_session"),
                        rs.getInt("id_eleve"),
                        rs.getString("statut_paiement")
                ));
            }
        }
        return liste;
    }
}