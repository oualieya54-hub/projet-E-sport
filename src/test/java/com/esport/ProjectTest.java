package com.esport;

import com.esport.config.DatabaseConnection;
import com.esport.metier.*;
import com.esport.model.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the entire E-Sport project.
 * Tests all CRUD operations and business logic for:
 * - Tournaments
 * - Events
 * - Matches
 * - Bets
 */
@DisplayName("E-Sport Project - Comprehensive Test Suite")
public class ProjectTest {

    private static TournamentMetier tournamentMetier;
    private static EventMetier eventMetier;
    private static MatchMetier matchMetier;
    private static BetMetier betMetier;

    private static int testTournamentId = -1;
    private static int testEventId = -1;
    private static int testMatchId = -1;

    @BeforeAll
    static void setupAll() {
        System.out.println("\n========== INITIALIZING TEST SUITE ==========\n");
        // Initialize all metier classes
        tournamentMetier = new TournamentMetier();
        eventMetier = new EventMetier();
        matchMetier = new MatchMetier();
        betMetier = new BetMetier();
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("\n========== CLOSING DATABASE CONNECTION ==========\n");
        DatabaseConnection.closeConnection();
    }

    // =============================
    // TOURNAMENT TESTS
    // =============================
    @Nested
    @DisplayName("Tournament Operations Tests")
    class TournamentTests {

        @ProjectTest
        @DisplayName("T1: Create a new tournament")
        void testCreateTournament() {
            assertDoesNotThrow(() -> {
                Tournament t = tournamentMetier.ajouter(
                        "MAVEN CUP 2026",
                        "Valorant",
                        Tournament.Format.SINGLE_ELIMINATION,
                        8,
                        new BigDecimal("3000"),
                        new BigDecimal("15"),
                        LocalDateTime.now().plusDays(15),
                        LocalDateTime.now().plusDays(12),
                        "Test Maven tournament",
                        1
                );
                testTournamentId = t.getId();
                assertNotNull(t);
                assertEquals("MAVEN CUP 2026", t.getName());
                assertTrue(testTournamentId > 0);
                System.out.println("✅ Tournament created with ID: " + testTournamentId);
            });
        }

        @ProjectTest
        @DisplayName("T2: Search tournament by ID")
        void testSearchTournamentById() {
            assertTrue(testTournamentId > 0, "Tournament must be created first");
            assertDoesNotThrow(() -> {
                Tournament t = tournamentMetier.rechercherParId(testTournamentId);
                assertNotNull(t);
                assertEquals("MAVEN CUP 2026", t.getName());
                System.out.println("✅ Found tournament: " + t.getName());
            });
        }

        @ProjectTest
        @DisplayName("T3: Search tournament by name")
        void testSearchTournamentByName() {
            assertDoesNotThrow(() -> {
                List<Tournament> tournaments = tournamentMetier.rechercherParNom("MAVEN");
                assertNotNull(tournaments);
                assertTrue(tournaments.size() > 0);
                System.out.println("✅ Found " + tournaments.size() + " tournament(s) with 'MAVEN'");
            });
        }

        @ProjectTest
        @DisplayName("T4: Search tournament by game")
        void testSearchTournamentByGame() {
            assertDoesNotThrow(() -> {
                List<Tournament> tournaments = tournamentMetier.rechercherParJeu("Valorant");
                assertNotNull(tournaments);
                assertTrue(tournaments.size() > 0);
                System.out.println("✅ Found " + tournaments.size() + " Valorant tournament(s)");
            });
        }

        @ProjectTest
        @DisplayName("T5: Search tournament by status")
        void testSearchTournamentByStatus() {
            assertDoesNotThrow(() -> {
                List<Tournament> tournaments = tournamentMetier.rechercherParStatus(Tournament.Status.UPCOMING);
                assertNotNull(tournaments);
                assertTrue(tournaments.size() > 0);
                System.out.println("✅ Found " + tournaments.size() + " UPCOMING tournament(s)");
            });
        }

        @ProjectTest
        @DisplayName("T6: List all tournaments")
        void testListAllTournaments() {
            assertDoesNotThrow(() -> {
                List<Tournament> tournaments = tournamentMetier.listerTous();
                assertNotNull(tournaments);
                assertTrue(tournaments.size() > 0);
                System.out.println("✅ Listed " + tournaments.size() + " total tournament(s)");
            });
        }

        @ProjectTest
        @DisplayName("T7: Register team to tournament")
        void testRegisterTeamToTournament() {
            assertTrue(testTournamentId > 0, "Tournament must be created first");
            assertDoesNotThrow(() -> {
                tournamentMetier.inscrireEquipe(testTournamentId, 1);
                tournamentMetier.inscrireEquipe(testTournamentId, 2);
                System.out.println("✅ Registered 2 teams to tournament");
            });
        }

        @ProjectTest
        @DisplayName("T8: Update tournament")
        void testUpdateTournament() {
            assertTrue(testTournamentId > 0, "Tournament must be created first");
            assertDoesNotThrow(() -> {
                tournamentMetier.modifier(
                        testTournamentId,
                        "MAVEN CUP 2026 - UPDATED",
                        null,
                        null,
                        0,
                        new BigDecimal("5000"),
                        null,
                        null,
                        null,
                        "Description updated"
                );
                Tournament t = tournamentMetier.rechercherParId(testTournamentId);
                assertEquals("MAVEN CUP 2026 - UPDATED", t.getName());
                System.out.println("✅ Tournament updated successfully");
            });
        }

        @ProjectTest
        @DisplayName("T9: Start tournament")
        void testStartTournament() {
            assertTrue(testTournamentId > 0, "Tournament must be created first");
            assertDoesNotThrow(() -> {
                tournamentMetier.demarrer(testTournamentId);
                Tournament t = tournamentMetier.rechercherParId(testTournamentId);
                assertEquals(Tournament.Status.ONGOING, t.getStatus());
                System.out.println("✅ Tournament started - Status: ONGOING");
            });
        }

        @ProjectTest
        @DisplayName("T10: Complete tournament")
        void testCompleteTournament() {
            assertTrue(testTournamentId > 0, "Tournament must be created first");
            assertDoesNotThrow(() -> {
                tournamentMetier.terminer(testTournamentId);
                Tournament t = tournamentMetier.rechercherParId(testTournamentId);
                assertEquals(Tournament.Status.COMPLETED, t.getStatus());
                System.out.println("✅ Tournament completed - Status: COMPLETED");
            });
        }
    }

    // =============================
    // EVENT TESTS
    // =============================
    @Nested
    @DisplayName("Event Operations Tests")
    class EventTests {

        @ProjectTest
        @DisplayName("E1: Create a new event")
        void testCreateEvent() {
            assertDoesNotThrow(() -> {
                Event ev = eventMetier.ajouter(
                        "MAVEN LAN EVENT",
                        Event.Type.LAN,
                        "CS2",
                        "Tunis Arena",
                        false,
                        LocalDateTime.now().plusDays(20),
                        LocalDateTime.now().plusDays(22),
                        100,
                        new BigDecimal("25"),
                        "Test event",
                        1
                );
                testEventId = ev.getId();
                assertNotNull(ev);
                assertEquals("MAVEN LAN EVENT", ev.getTitle());
                assertTrue(testEventId > 0);
                System.out.println("✅ Event created with ID: " + testEventId);
            });
        }

        @ProjectTest
        @DisplayName("E2: Search event by ID")
        void testSearchEventById() {
            assertTrue(testEventId > 0, "Event must be created first");
            assertDoesNotThrow(() -> {
                Event ev = eventMetier.rechercherParId(testEventId);
                assertNotNull(ev);
                assertEquals("MAVEN LAN EVENT", ev.getTitle());
                System.out.println("✅ Found event: " + ev.getTitle());
            });
        }

        @ProjectTest
        @DisplayName("E3: Search event by title")
        void testSearchEventByTitle() {
            assertDoesNotThrow(() -> {
                List<Event> events = eventMetier.rechercherParTitre("MAVEN");
                assertNotNull(events);
                assertTrue(events.size() > 0);
                System.out.println("✅ Found " + events.size() + " event(s) with 'MAVEN'");
            });
        }

        @ProjectTest
        @DisplayName("E4: Search event by type")
        void testSearchEventByType() {
            assertDoesNotThrow(() -> {
                List<Event> events = eventMetier.rechercherParType(Event.Type.LAN);
                assertNotNull(events);
                assertTrue(events.size() > 0);
                System.out.println("✅ Found " + events.size() + " LAN event(s)");
            });
        }

        @ProjectTest
        @DisplayName("E5: List all events")
        void testListAllEvents() {
            assertDoesNotThrow(() -> {
                List<Event> events = eventMetier.listerTous();
                assertNotNull(events);
                assertTrue(events.size() > 0);
                System.out.println("✅ Listed " + events.size() + " total event(s)");
            });
        }

        @ProjectTest
        @DisplayName("E6: Update event")
        void testUpdateEvent() {
            assertTrue(testEventId > 0, "Event must be created first");
            assertDoesNotThrow(() -> {
                eventMetier.modifier(
                        testEventId,
                        "MAVEN LAN EVENT - UPDATED",
                        null,
                        null,
                        "Salle 2",
                        null,
                        null,
                        200,
                        new BigDecimal("30"),
                        null
                );
                Event ev = eventMetier.rechercherParId(testEventId);
                assertEquals("MAVEN LAN EVENT - UPDATED", ev.getTitle());
                System.out.println("✅ Event updated successfully");
            });
        }

        @ProjectTest
        @DisplayName("E7: Publish event")
        void testPublishEvent() {
            assertTrue(testEventId > 0, "Event must be created first");
            assertDoesNotThrow(() -> {
                eventMetier.publier(testEventId);
                Event ev = eventMetier.rechercherParId(testEventId);
                assertEquals(Event.Status.PUBLISHED, ev.getStatus());
                System.out.println("✅ Event published - Status: PUBLISHED");
            });
        }

        @ProjectTest
        @DisplayName("E8: List published events")
        void testListPublishedEvents() {
            assertDoesNotThrow(() -> {
                List<Event> events = eventMetier.listerPublies();
                assertNotNull(events);
                assertTrue(events.size() > 0);
                System.out.println("✅ Listed " + events.size() + " published event(s)");
            });
        }

        @ProjectTest
        @DisplayName("E9: Register user to event")
        void testRegisterUserToEvent() {
            assertTrue(testEventId > 0, "Event must be created first");
            assertDoesNotThrow(() -> {
                String ticket = eventMetier.inscrireUser(testEventId, 1);
                assertNotNull(ticket);
                assertTrue(!ticket.isEmpty());
                System.out.println("✅ User registered to event - Ticket: " + ticket);
            });
        }

        @ProjectTest
        @DisplayName("E10: Count registered users")
        void testCountRegisteredUsers() {
            assertTrue(testEventId > 0, "Event must be created first");
            assertDoesNotThrow(() -> {
                int count = eventMetier.compterInscrits(testEventId);
                assertTrue(count > 0);
                System.out.println("✅ Found " + count + " registered user(s)");
            });
        }

        @ProjectTest
        @DisplayName("E11: Cancel event")
        void testCancelEvent() {
            assertTrue(testEventId > 0, "Event must be created first");
            assertDoesNotThrow(() -> {
                eventMetier.annuler(testEventId);
                Event ev = eventMetier.rechercherParId(testEventId);
                assertEquals(Event.Status.CANCELLED, ev.getStatus());
                System.out.println("✅ Event cancelled - Status: CANCELLED");
            });
        }
    }

    // =============================
    // MATCH TESTS
    // =============================
    @Nested
    @DisplayName("Match Operations Tests")
    class MatchTests {

        @ProjectTest
        @DisplayName("M1: Create a new match")
        void testCreateMatch() {
            assertDoesNotThrow(() -> {
                TournamentMatch m = matchMetier.ajouter(
                        1,
                        99,
                        1,
                        1,
                        2,
                        LocalDateTime.now().plusHours(2)
                );
                testMatchId = m.getId();
                assertNotNull(m);
                assertTrue(testMatchId > 0);
                System.out.println("✅ Match created with ID: " + testMatchId);
            });
        }

        @ProjectTest
        @DisplayName("M2: Search match by ID")
        void testSearchMatchById() {
            assertTrue(testMatchId > 0, "Match must be created first");
            assertDoesNotThrow(() -> {
                TournamentMatch m = matchMetier.rechercherParId(testMatchId);
                assertNotNull(m);
                assertTrue(m.getId() > 0);
                System.out.println("✅ Found match with ID: " + m.getId());
            });
        }

        @ProjectTest
        @DisplayName("M3: Update match")
        void testUpdateMatch() {
            assertTrue(testMatchId > 0, "Match must be created first");
            assertDoesNotThrow(() -> {
                matchMetier.modifier(testMatchId, null, "https://twitch.tv/test");
                TournamentMatch m = matchMetier.rechercherParId(testMatchId);
                assertEquals("https://twitch.tv/test", m.getStreamUrl());
                System.out.println("✅ Match updated with stream URL");
            });
        }

        @ProjectTest
        @DisplayName("M4: Start match (go live)")
        void testStartMatch() {
            assertTrue(testMatchId > 0, "Match must be created first");
            assertDoesNotThrow(() -> {
                matchMetier.demarrer(testMatchId);
                TournamentMatch m = matchMetier.rechercherParId(testMatchId);
                assertEquals(TournamentMatch.Status.LIVE, m.getStatus());
                System.out.println("✅ Match started - Status: LIVE");
            });
        }

        @ProjectTest
        @DisplayName("M5: List live matches")
        void testListLiveMatches() {
            assertDoesNotThrow(() -> {
                List<TournamentMatch> liveMatches = matchMetier.listerLive();
                assertNotNull(liveMatches);
                assertTrue(liveMatches.size() > 0);
                System.out.println("✅ Found " + liveMatches.size() + " live match(es)");
            });
        }

        @ProjectTest
        @DisplayName("M6: Update match score")
        void testUpdateMatchScore() {
            assertTrue(testMatchId > 0, "Match must be created first");
            assertDoesNotThrow(() -> {
                matchMetier.mettreAJourScore(testMatchId, 3, 1);
                TournamentMatch m = matchMetier.rechercherParId(testMatchId);
                assertEquals(3, m.getScore1());
                assertEquals(1, m.getScore2());
                System.out.println("✅ Match score updated to 3-1");
            });
        }

        @ProjectTest
        @DisplayName("M7: End match and settle bets")
        void testEndMatchAndSettleBets() {
            assertTrue(testMatchId > 0, "Match must be created first");
            assertDoesNotThrow(() -> {
                int settled = matchMetier.terminer(testMatchId, 1, 3, 1);
                TournamentMatch m = matchMetier.rechercherParId(testMatchId);
                assertEquals(TournamentMatch.Status.COMPLETED, m.getStatus());
                System.out.println("✅ Match completed - " + settled + " bet(s) settled");
            });
        }

        @ProjectTest
        @DisplayName("M8: List matches by tournament")
        void testListMatchesByTournament() {
            assertDoesNotThrow(() -> {
                List<TournamentMatch> matches = matchMetier.listerParTournoi(1);
                assertNotNull(matches);
                System.out.println("✅ Listed " + matches.size() + " match(es) for tournament 1");
            });
        }
    }

    // =============================
    // BET TESTS
    // =============================
    @Nested
    @DisplayName("Bet Operations Tests")
    class BetTests {

        @ProjectTest
        @DisplayName("B1: Create user wallet")
        void testCreateWallet() {
            assertDoesNotThrow(() -> {
                betMetier.creerWallet(1);
                System.out.println("✅ Wallet created for user 1");
            });
        }

        @ProjectTest
        @DisplayName("B2: Check wallet balance")
        void testCheckBalance() {
            assertDoesNotThrow(() -> {
                BigDecimal balance = betMetier.consulterSolde(1);
                assertNotNull(balance);
                assertTrue(balance.compareTo(BigDecimal.ZERO) >= 0);
                System.out.println("✅ Wallet balance: $" + balance);
            });
        }

        @ProjectTest
        @DisplayName("B3: Deposit to wallet")
        void testDepositToWallet() {
            assertDoesNotThrow(() -> {
                betMetier.deposer(1, new BigDecimal("100.00"));
                BigDecimal balance = betMetier.consulterSolde(1);
                assertTrue(balance.compareTo(new BigDecimal("100")) >= 0);
                System.out.println("✅ Deposited $100 - New balance: $" + balance);
            });
        }

        @ProjectTest
        @DisplayName("B4: Reject invalid deposit (below minimum)")
        void testRejectInvalidDeposit() {
            assertThrows(Exception.class, () -> {
                betMetier.deposer(1, new BigDecimal("2.00"));
            });
            System.out.println("✅ Correctly rejected deposit below minimum");
        }

        @ProjectTest
        @DisplayName("B5: Place bet on live match")
        void testPlaceBet() {
            assertDoesNotThrow(() -> {
                List<TournamentMatch> liveMatches = matchMetier.listerLive();
                if (!liveMatches.isEmpty()) {
                    TournamentMatch m = liveMatches.get(0);
                    Bet bet = betMetier.placerPari(
                            1,
                            m.getId(),
                            m.getTeam1Id(),
                            new BigDecimal("25.00"),
                            new BigDecimal("2.00")
                    );
                    assertNotNull(bet);
                    assertTrue(bet.getId() > 0);
                    System.out.println("✅ Bet placed - Bet ID: " + bet.getId() +
                            " | Potential Win: $" + bet.getPotentialWin());
                } else {
                    System.out.println("⚠️  No live matches available - Skipping bet placement");
                }
            });
        }

        @ProjectTest
        @DisplayName("B6: List user bets")
        void testListUserBets() {
            assertDoesNotThrow(() -> {
                List<Bet> bets = betMetier.listerParisUser(1);
                assertNotNull(bets);
                System.out.println("✅ Listed " + bets.size() + " bet(s) for user 1");
            });
        }

        @ProjectTest
        @DisplayName("B7: List all bets")
        void testListAllBets() {
            assertDoesNotThrow(() -> {
                List<Bet> bets = betMetier.listerTous();
                assertNotNull(bets);
                System.out.println("✅ Listed " + bets.size() + " total bet(s)");
            });
        }
    }

    // =============================
    // INTEGRATION TESTS
    // =============================
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @ProjectTest
        @DisplayName("I1: Complete workflow - Tournament to Bets")
        void testCompleteWorkflow() {
            assertDoesNotThrow(() -> {
                // 1. Create Tournament
                Tournament t = tournamentMetier.ajouter(
                        "INTEGRATION TEST",
                        "League of Legends",
                        Tournament.Format.ROUND_ROBIN,
                        4,
                        new BigDecimal("2000"),
                        new BigDecimal("10"),
                        LocalDateTime.now().plusDays(10),
                        LocalDateTime.now().plusDays(8),
                        "Integration test",
                        1
                );
                assertTrue(t.getId() > 0);
                System.out.println("✅ Step 1: Tournament created");

                // 2. Register teams
                tournamentMetier.inscrireEquipe(t.getId(), 1);
                tournamentMetier.inscrireEquipe(t.getId(), 2);
                System.out.println("✅ Step 2: Teams registered");

                // 3. Start tournament
                tournamentMetier.demarrer(t.getId());
                System.out.println("✅ Step 3: Tournament started");

                // 4. Create match
                TournamentMatch m = matchMetier.ajouter(
                        t.getId(),
                        1,
                        1,
                        1,
                        2,
                        LocalDateTime.now().plusHours(1)
                );
                assertTrue(m.getId() > 0);
                System.out.println("✅ Step 4: Match created");

                // 5. Start match
                matchMetier.demarrer(m.getId());
                System.out.println("✅ Step 5: Match started (LIVE)");

                // 6. Create wallet and deposit
                betMetier.creerWallet(1);
                betMetier.deposer(1, new BigDecimal("50.00"));
                System.out.println("✅ Step 6: Wallet created and funded");

                // 7. Place bet
                Bet bet = betMetier.placerPari(
                        1,
                        m.getId(),
                        1,
                        new BigDecimal("20.00"),
                        new BigDecimal("1.80")
                );
                assertTrue(bet.getId() > 0);
                System.out.println("✅ Step 7: Bet placed successfully");

                System.out.println("✅ COMPLETE WORKFLOW TEST PASSED!");
            });
        }

        @ProjectTest
        @DisplayName("I2: Database connectivity check")
        void testDatabaseConnectivity() {
            assertDoesNotThrow(() -> {
                // Try to list any data to verify DB connection works
                List<Tournament> tournaments = tournamentMetier.listerTous();
                assertNotNull(tournaments);
                System.out.println("✅ Database connection verified - Found " +
                        tournaments.size() + " tournament(s)");
            });
        }
    }
}