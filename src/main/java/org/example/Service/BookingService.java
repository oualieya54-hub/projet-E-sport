package org.example.Service;

import org.example.Utils.MyDatabase;
import org.example.Model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    public void book(Booking b) throws SQLException {
        String sql = "INSERT INTO Booking (id_session, id_eleve, statut_paiement, date_reservation, mode_paiement) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getIdSession());
            ps.setInt(2, b.getIdEleve());
            ps.setString(3, b.getStatutPaiement());
            ps.setTimestamp(4, Timestamp.valueOf(b.getDateReservation()));
            ps.setString(5, b.getModePaiement());
            ps.executeUpdate();
            System.out.println("✅ Réservation effectuée !");
        }
    }
    public Booking getById(int idBooking) throws SQLException {
        String sql = "SELECT * FROM booking WHERE id_booking = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBooking);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Booking(
                        rs.getInt("id_booking"),
                        rs.getInt("id_session"),
                        rs.getInt("id_eleve"),
                        rs.getString("statut_paiement"),
                        rs.getTimestamp("date_reservation").toLocalDateTime(),
                        rs.getString("mode_paiement")
                );
            }
        }
        return null;
    }

    public List<Booking> getByEleve(int idEleve) throws SQLException {
        List<Booking> liste = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE id_eleve = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEleve);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Booking(
                        rs.getInt("id_booking"),
                        rs.getInt("id_session"),
                        rs.getInt("id_eleve"),
                        rs.getString("statut_paiement"),
                        rs.getTimestamp("date_reservation").toLocalDateTime(),
                        rs.getString("mode_paiement")
                ));
            }
        }
        return liste;
    }

    public List<Booking> getAll() throws SQLException {
        List<Booking> liste = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (Connection con = MyDatabase.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Booking(
                        rs.getInt("id_booking"),
                        rs.getInt("id_session"),
                        rs.getInt("id_eleve"),
                        rs.getString("statut_paiement"),
                        rs.getTimestamp("date_reservation").toLocalDateTime(),
                        rs.getString("mode_paiement")
                ));
            }
        }
        return liste;
    }


    public void update(Booking b) throws SQLException {
        String sql = "UPDATE booking SET id_session=?, id_eleve=?, statut_paiement=?, date_reservation=?, mode_paiement=? WHERE id_booking=?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getIdSession());
            ps.setInt(2, b.getIdEleve());
            ps.setString(3, b.getStatutPaiement());
            ps.setTimestamp(4, Timestamp.valueOf(b.getDateReservation()));
            ps.setString(5, b.getModePaiement());
            ps.setInt(6, b.getIdBooking());
            ps.executeUpdate();
            System.out.println("✅ Réservation mise à jour !");
        }
    }

    public void confirmPayment(int idBooking) throws SQLException {
        // statut_paiement ENUM: 'en_attente' | 'confirmé' | 'annulé'
        String sql = "UPDATE Booking SET statut_paiement = 'confirmé' WHERE id_booking = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBooking);
            ps.executeUpdate();
            System.out.println("Paiement confirme !");
        }
    }

    public void cancel(int idBooking) throws SQLException {
        String sql = "DELETE FROM Booking WHERE id_booking = ?";
        try (Connection con = MyDatabase.getConnection();
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
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSession);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Booking(
                        rs.getInt("id_booking"),
                        rs.getInt("id_session"),
                        rs.getInt("id_eleve"),
                        rs.getString("statut_paiement"),
                        rs.getTimestamp("date_reservation").toLocalDateTime(),
                        rs.getString("mode_paiement")
                ));
            }
        }
        return liste;
    }
    //Metier: Vérifier si un élève est déjà inscrit à cette session — éviter les doublons.
    public boolean hasAlreadyBooked(int idEleve, int idSession) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Booking WHERE id_eleve = ? AND id_session = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEleve);
            ps.setInt(2, idSession);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }
    //Metier :Réservation sécurisée : appelle hasAlreadyBooked avant d'inserer..
    public void bookSafe(Booking b) throws SQLException {
        if (hasAlreadyBooked(b.getIdEleve(), b.getIdSession())) {
            System.out.println("Eleve deja inscrit a cette session !");
        } else {
            book(b);
            System.out.println("Reservation effectuee avec succes !");
        }
    }
    //Metier :  Compter le nombre d'élèves inscrits à une session — utile pour gérer une limite de places.
    public int getNombreReservations(int idSession) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Booking WHERE id_session = ?";
        try (Connection con = MyDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSession);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}



