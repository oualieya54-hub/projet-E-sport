package org.example;

import org.example.dao.BookingDAO;
import org.example.dao.SessionDAO;
import org.example.model.Booking;
import org.example.model.Session;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {

        SessionDAO sessionDAO = new SessionDAO();
        BookingDAO bookingDAO = new BookingDAO();

        // 1. Afficher toutes les sessions
        System.out.println("=== Sessions disponibles ===");
        List<Session> sessions = sessionDAO.getDisponibilites();
        for (Session s : sessions) {
            System.out.println(s);
        }

        // 2. Créer une nouvelle session
        System.out.println("\n=== Création session ===");
        sessionDAO.create(new Session(0, LocalDateTime.now(), "Fortnite", 19.99f, 3));

        // 3. Faire une réservation
        System.out.println("\n=== Nouvelle réservation ===");
        bookingDAO.book(new Booking(0, 1, 200, "en attente"));

        // 4. Confirmer le paiement
        System.out.println("\n=== Confirmation paiement ===");
        bookingDAO.confirmPayment(1);
    }
}