package org.example;

import org.example.Model.*;
import org.example.Utils.MyDatabase;
import org.example.controller.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCRUDWithSetup {

    private static final SessionController       sessionController       = new SessionController();
    private static final BookingController       bookingController       = new BookingController();
    private static final FormationController     formationController     = new FormationController();
    private static final CertificationController certificationController = new CertificationController();
    private static final EvaluationController    evaluationController    = new EvaluationController();

    // IDs dynamiques : récupérés après insertion, jamais hardcodés
    private static int idSession   = -1;
    private static int idBooking   = -1;
    private static int idFormation = -1;

    // ── Setup ──────────────────────────────────────────────────────────────────

    @BeforeAll
    static void setupTestData() throws SQLException {
        System.out.println("\n--- SETUP: Création des données de test ---");
        try (Connection con = MyDatabase.getConnection()) {
            insertUserIfAbsent(con, 1,   "Coach Un",       "coach_1",  "coach1@test.com",  "pass123", "coach");
            insertUserIfAbsent(con, 2,   "Coach Deux",     "coach_2",  "coach2@test.com",  "pass123", "coach");
            for (int i = 101; i <= 104; i++) {
                insertUserIfAbsent(con, i, "Eleve " + i, "eleve_" + i, "eleve" + i + "@test.com", "pass123", "player");
            }
        }
        System.out.println("Setup OK.");
    }

    private static void insertUserIfAbsent(Connection con, int id, String nom, String pseudo,
                                           String email, String password, String role) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO users (id, nom, pseudo, email, password, role) VALUES (?, ?, ?, ?, ?, ?)")) {
                    ins.setInt(1, id);
                    ins.setString(2, nom);
                    ins.setString(3, pseudo);
                    ins.setString(4, email);
                    ins.setString(5, password);
                    ins.setString(6, role);
                    ins.executeUpdate();
                    System.out.println("  Utilisateur id=" + id + " cree.");
                }
            }
        }
    }

    // ── SESSION ────────────────────────────────────────────────────────────────

    @Test @Order(1)
    void testSessionCreate() throws SQLException {
        sessionController.create(new Session(0, LocalDateTime.now().plusDays(1), "Fortnite",         19.99f, 1));
        sessionController.create(new Session(0, LocalDateTime.now().plusDays(2), "League of Legends", 24.99f, 2));
        sessionController.create(new Session(0, LocalDateTime.now().plusDays(3), "Valorant",          29.99f, 1));

        List<Session> sessions = sessionController.getDisponibilites();
        assertFalse(sessions.isEmpty(), "Au moins une session doit exister apres creation");
        idSession = sessions.get(0).getIdSession();
        System.out.println("[OK] " + sessions.size() + " session(s), idSession=" + idSession);
    }

    @Test @Order(2)
    void testSessionReadById() throws SQLException {
        List<Session> sessions = sessionController.getDisponibilites();
        assertFalse(sessions.isEmpty());
        idSession = sessions.get(0).getIdSession();

        Session s = sessionController.getById(idSession);
        assertNotNull(s, "getById doit retourner la session");
        System.out.println("[OK] Session lue : " + s);
    }

    @Test @Order(3)
    void testSessionUpdate() throws SQLException {
        List<Session> sessions = sessionController.getDisponibilites();
        assertFalse(sessions.isEmpty());
        idSession = sessions.get(0).getIdSession();

        Session s = sessionController.getById(idSession);
        assertNotNull(s);
        s.setJeu("CS:GO");
        s.setPrix(35.99f);
        sessionController.update(s);

        Session updated = sessionController.getById(idSession);
        assertNotNull(updated);
        assertEquals("CS:GO", updated.getJeu());
        assertEquals(35.99f, updated.getPrix(), 0.01f);
        System.out.println("[OK] Session mise a jour : " + updated);
    }

    @Test @Order(4)
    void testSessionFilters() throws SQLException {
        List<Session> byCoach = sessionController.getSessionsByCoach(1);
        assertNotNull(byCoach);
        System.out.println("[OK] Sessions coach 1 : " + byCoach.size());

        List<Session> byJeu = sessionController.getSessionsByJeu("Valorant");
        assertNotNull(byJeu);
        System.out.println("[OK] Sessions Valorant : " + byJeu.size());

        boolean dispo = sessionController.isCoachDisponible(1, LocalDateTime.now().plusDays(10));
        System.out.println("[OK] Coach dispo j+10 : " + dispo);
    }

    // ── FORMATION ──────────────────────────────────────────────────────────────

    @Test @Order(5)
    void testFormationCreate() throws SQLException {
        formationController.create(new Formation("Formation Fortnite Pro",     "Fortnite", "avancé",        199.99f, 1));
        formationController.create(new Formation("Formation Valorant Master",  "Valorant", "intermédiaire", 149.99f, 2));

        List<Formation> formations = formationController.getAll();
        assertFalse(formations.isEmpty(), "Au moins une formation doit exister");
        idFormation = formations.get(0).getIdFormation();
        System.out.println("[OK] " + formations.size() + " formation(s), idFormation=" + idFormation);
    }

    @Test @Order(6)
    void testFormationReadAndUpdate() throws SQLException {
        List<Formation> formations = formationController.getAll();
        assertFalse(formations.isEmpty());
        idFormation = formations.get(0).getIdFormation();

        Formation f = formationController.getById(idFormation);
        assertNotNull(f, "getById doit retourner la formation");

        f.setNiveau("avancé");
        f.setPrix(249.99f);
        formationController.update(f);

        Formation updated = formationController.getById(idFormation);
        assertNotNull(updated);
        assertEquals(249.99f, updated.getPrix(), 0.01f);
        System.out.println("[OK] Formation mise a jour : " + updated);
    }

    @Test @Order(7)
    void testFormationFilters() throws SQLException {
        List<Formation> active  = formationController.getActive();    assertNotNull(active);
        List<Formation> byJeu   = formationController.getByJeu("Fortnite"); assertNotNull(byJeu);
        List<Formation> byCoach = formationController.getByCoach(1);        assertNotNull(byCoach);
        System.out.println("[OK] Actives=" + active.size() + ", Fortnite=" + byJeu.size() + ", Coach1=" + byCoach.size());

        List<Formation> formations = formationController.getAll();
        if (!formations.isEmpty()) {
            formationController.activer(formations.get(0).getIdFormation());
            System.out.println("[OK] Formation activee.");
        }
    }

    // ── BOOKING ────────────────────────────────────────────────────────────────

    @Test @Order(8)
    void testBookingCreate() throws SQLException {
        List<Session> sessions = sessionController.getDisponibilites();
        assertFalse(sessions.isEmpty(), "Il faut des sessions pour creer des bookings");
        idSession = sessions.get(0).getIdSession();

        bookingController.book(new Booking(0, idSession, 101, "en_attente"));
        bookingController.book(new Booking(0, idSession, 102, "en_attente"));
        bookingController.book(new Booking(0, idSession, 103, "en_attente"));

        List<Booking> bookings = bookingController.getAll();
        assertFalse(bookings.isEmpty(), "Des bookings doivent exister");
        idBooking = bookings.get(0).getIdBooking();
        System.out.println("[OK] " + bookings.size() + " booking(s), idBooking=" + idBooking);
    }

    @Test @Order(9)
    void testBookingReadAndConfirm() throws SQLException {
        List<Booking> bookings = bookingController.getAll();
        assertFalse(bookings.isEmpty());
        idBooking = bookings.get(0).getIdBooking();

        Booking b = bookingController.getById(idBooking);
        assertNotNull(b, "getById doit retourner le booking");
        System.out.println("[OK] Booking lu : " + b);

        bookingController.confirmPayment(idBooking);
        System.out.println("[OK] Paiement confirme pour idBooking=" + idBooking);
    }

    @Test @Order(10)
    void testBookingFilters() throws SQLException {
        List<Session> sessions = sessionController.getDisponibilites();
        assertFalse(sessions.isEmpty());
        idSession = sessions.get(0).getIdSession();

        List<Booking> bySession = bookingController.getBookingsBySession(idSession);
        assertNotNull(bySession);

        List<Booking> byEleve = bookingController.getByEleve(102);
        assertNotNull(byEleve);

        boolean already = bookingController.hasAlreadyBooked(102, idSession);
        System.out.println("[OK] Eleve 102 deja inscrit: " + already);

        // bookSafe doit refuser silencieusement un doublon (UNIQUE KEY uq_booking)
        bookingController.bookSafe(new Booking(0, idSession, 102, "en_attente"));

        int nombre = bookingController.getNombreReservations(idSession);
        assertTrue(nombre >= 0);
        System.out.println("[OK] Total reservations session: " + nombre);
    }

    // ── CERTIFICATION ──────────────────────────────────────────────────────────

    @Test @Order(11)
    void testCertificationCreate() throws SQLException {
        List<Formation> formations = formationController.getAll();
        assertFalse(formations.isEmpty());
        idFormation = formations.get(0).getIdFormation();

        certificationController.create(new Certification(101, idFormation, "Gold",   85.5f));
        certificationController.create(new Certification(102, idFormation, "Silver",  72.0f));

        List<Certification> certs = certificationController.getAll();
        assertFalse(certs.isEmpty(), "Des certifications doivent exister");
        System.out.println("[OK] " + certs.size() + " certification(s).");
    }

    @Test @Order(12)
    void testCertificationFilters() throws SQLException {
        List<Formation> formations = formationController.getAll();
        assertFalse(formations.isEmpty());
        idFormation = formations.get(0).getIdFormation();

        List<Certification> byEleve = certificationController.getByEleve(101);
        assertNotNull(byEleve);

        List<Certification> byForm = certificationController.getByFormation(idFormation);
        assertNotNull(byForm);

        boolean already = certificationController.hasAlreadyCertified(101, idFormation);
        System.out.println("[OK] Eleve 101 certifie: " + already);

        String niveau = certificationController.calculerNiveau(95f);
        assertNotNull(niveau);
        System.out.println("[OK] Niveau score=95 -> " + niveau);

        List<Certification> byNiv = certificationController.getByNiveau("Gold");
        assertNotNull(byNiv);
        System.out.println("[OK] Certifications Gold: " + byNiv.size());
    }

    // ── EVALUATION ─────────────────────────────────────────────────────────────

    @Test @Order(13)
    void testEvaluationCreate() throws SQLException {
        List<Booking> bookings = bookingController.getAll();
        assertFalse(bookings.isEmpty(), "Il faut des bookings pour evaluer");
        idBooking = bookings.get(0).getIdBooking();

        evaluationController.create(new Evaluation(idBooking, 5, "Excellent coach, tres pedagogique!"));

        List<Evaluation> evals = evaluationController.getAll();
        assertFalse(evals.isEmpty(), "Des evaluations doivent exister");
        System.out.println("[OK] " + evals.size() + " evaluation(s).");
    }

    @Test @Order(14)
    void testEvaluationReadAndUpdate() throws SQLException {
        List<Booking> bookings = bookingController.getAll();
        assertFalse(bookings.isEmpty());
        idBooking = bookings.get(0).getIdBooking();

        Evaluation eval = evaluationController.getByBooking(idBooking);
        assertNotNull(eval, "L'evaluation du booking doit exister");

        boolean already = evaluationController.hasAlreadyEvaluated(idBooking);
        assertTrue(already, "Le booking doit deja avoir une evaluation");

        eval.setNote(5);
        eval.setCommentaire("Excellent! Vraiment formateur!");
        evaluationController.update(eval);
        System.out.println("[OK] Evaluation mise a jour : " + eval);
    }

    @Test @Order(15)
    void testEvaluationStats() throws SQLException {
        double moyenne = evaluationController.getNoteMoyenneCoach(1);
        assertTrue(moyenne >= 0.0, "Moyenne doit etre >= 0");
        System.out.println("[OK] Note moyenne coach 1 : " + moyenne);

        List<Formation> formations = formationController.getAll();
        if (!formations.isEmpty()) {
            List<Evaluation> byForm = evaluationController.getByFormation(formations.get(0).getIdFormation());
            assertNotNull(byForm);
            System.out.println("[OK] Evaluations par formation: " + byForm.size());
        }
    }
}
