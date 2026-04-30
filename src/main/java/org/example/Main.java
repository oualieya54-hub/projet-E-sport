/*package org.example;

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
}*/
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

        System.out.println("================================================");
        System.out.println("         TEST CRUD - SESSION");
        System.out.println("================================================");

        // CREATE
        System.out.println("\n--- CREATE : Ajouter 2 sessions ---");
        sessionDAO.create(new Session(0, LocalDateTime.now().plusDays(1), "Fortnite", 19.99f, 1));
        sessionDAO.create(new Session(0, LocalDateTime.now().plusDays(2), "League of Legends", 24.99f, 2));

        // READ
        System.out.println("\n--- READ : Toutes les sessions ---");
        List<Session> sessions = sessionDAO.getDisponibilites();
        for (Session s : sessions) {
            System.out.println(s);
        }

        // UPDATE
        System.out.println("\n--- UPDATE : Modifier la premiere session ---");
        if (!sessions.isEmpty()) {
            Session s = sessions.get(0);
            s.setJeu("Valorant");
            s.setPrix(29.99f);
            sessionDAO.update(s);
        }

        // READ apres update
        System.out.println("\n--- READ apres UPDATE ---");
        for (Session s : sessionDAO.getDisponibilites()) {
            System.out.println(s);
        }

        // DELETE
        System.out.println("\n--- DELETE : Supprimer la derniere session ---");
        List<Session> sessionsAvantDelete = sessionDAO.getDisponibilites();
        if (!sessionsAvantDelete.isEmpty()) {
            Session derniere = sessionsAvantDelete.get(sessionsAvantDelete.size() - 1);
            sessionDAO.cancel(derniere.getIdSession());
        }

        System.out.println("\n================================================");
        System.out.println("         TEST METIER - SESSION");
        System.out.println("================================================");

        // getSessionsByCoach
        System.out.println("\n--- Sessions du coach id=1 ---");
        List<Session> parCoach = sessionDAO.getSessionsByCoach(1);
        if (parCoach.isEmpty()) System.out.println("Aucune session pour ce coach.");
        for (Session s : parCoach) System.out.println(s);

        // getSessionsByJeu
        System.out.println("\n--- Sessions du jeu Valorant ---");
        List<Session> parJeu = sessionDAO.getSessionsByJeu("Valorant");
        if (parJeu.isEmpty()) System.out.println("Aucune session pour ce jeu.");
        for (Session s : parJeu) System.out.println(s);

        // getSessionsFutures
        System.out.println("\n--- Sessions futures ---");
        List<Session> futures = sessionDAO.getSessionsFutures();
        if (futures.isEmpty()) System.out.println("Aucune session future.");
        for (Session s : futures) System.out.println(s);

        // isCoachDisponible
        System.out.println("\n--- Coach id=1 disponible demain ? ---");
        boolean dispo = sessionDAO.isCoachDisponible(1, LocalDateTime.now().plusDays(3));
        System.out.println(dispo ? "Oui, coach disponible !" : "Non, coach occupe a ce creneau.");

        System.out.println("\n================================================");
        System.out.println("         TEST CRUD - BOOKING");
        System.out.println("================================================");

        // Recuperer une session existante pour les bookings
        List<Session> sessionsPourBooking = sessionDAO.getDisponibilites();
        if (sessionsPourBooking.isEmpty()) {
            System.out.println("Aucune session disponible pour tester les bookings.");
            return;
        }
        int idSession = sessionsPourBooking.get(0).getIdSession();

        // CREATE
        System.out.println("\n--- CREATE : Reserver une session ---");
        bookingDAO.book(new Booking(0, idSession, 101, "en attente"));

        // READ
        System.out.println("\n--- READ : Toutes les reservations ---");
        List<Booking> tousBookings = bookingDAO.getAll();
        if (tousBookings.isEmpty()) System.out.println("Aucune reservation.");
        for (Booking b : tousBookings) System.out.println(b);

        // UPDATE (confirm payment)
        System.out.println("\n--- UPDATE : Confirmer paiement du premier booking ---");
        if (!tousBookings.isEmpty()) {
            bookingDAO.confirmPayment(tousBookings.get(0).getIdBooking());
        }

        // READ apres update
        System.out.println("\n--- READ apres UPDATE ---");
        for (Booking b : bookingDAO.getAll()) System.out.println(b);

        // DELETE
        System.out.println("\n--- DELETE : Annuler le premier booking ---");
        List<Booking> bookingsAvantDelete = bookingDAO.getAll();
        if (!bookingsAvantDelete.isEmpty()) {
            bookingDAO.cancel(bookingsAvantDelete.get(0).getIdBooking());
        }

        System.out.println("\n================================================");
        System.out.println("         TEST METIER - BOOKING");
        System.out.println("================================================");

        // Preparer des bookings pour les tests
        bookingDAO.book(new Booking(0, idSession, 102, "en attente"));
        bookingDAO.book(new Booking(0, idSession, 103, "en attente"));

        // getBookingsBySession
        System.out.println("\n--- Reservations de la session id=" + idSession + " ---");
        List<Booking> parSession = bookingDAO.getBookingsBySession(idSession);
        if (parSession.isEmpty()) System.out.println("Aucune reservation pour cette session.");
        for (Booking b : parSession) System.out.println(b);

        // getByEleve
        System.out.println("\n--- Reservations de l'eleve id=102 ---");
        List<Booking> parEleve = bookingDAO.getByEleve(102);
        if (parEleve.isEmpty()) System.out.println("Aucune reservation pour cet eleve.");
        for (Booking b : parEleve) System.out.println(b);

        // hasAlreadyBooked
        System.out.println("\n--- Eleve 102 deja inscrit a la session " + idSession + " ? ---");
        boolean dejaInscrit = bookingDAO.hasAlreadyBooked(102, idSession);
        System.out.println(dejaInscrit ? "Oui, deja inscrit !" : "Non, pas encore inscrit.");

        // bookSafe - doublon (doit bloquer)
        System.out.println("\n--- bookSafe : Tenter double inscription eleve 102 ---");
        bookingDAO.bookSafe(new Booking(0, idSession, 102, "en attente"));

        // bookSafe - nouvel eleve (doit passer)
        System.out.println("\n--- bookSafe : Nouvel eleve 104 ---");
        bookingDAO.bookSafe(new Booking(0, idSession, 104, "en attente"));

        // getNombreReservations
        System.out.println("\n--- Nombre de reservations pour session " + idSession + " ---");
        int nombre = bookingDAO.getNombreReservations(idSession);
        System.out.println("Total : " + nombre + " reservation(s)");

        System.out.println("\n================================================");
        System.out.println("         TOUS LES TESTS TERMINES");
        System.out.println("================================================");
    }
}