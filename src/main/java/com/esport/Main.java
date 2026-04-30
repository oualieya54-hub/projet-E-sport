package com.esport;

import com.esport.config.DatabaseConnection;
import com.esport.metier.*;
import com.esport.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Main — tests all Metier operations (CRUD + métiers).
 * Run this to verify everything works.
 */
public class Main {

    static TournamentMetier tournamentMetier = new TournamentMetier();
    static EventMetier      eventMetier      = new EventMetier();
    static MatchMetier      matchMetier      = new MatchMetier();
    static BetMetier        betMetier        = new BetMetier();

    static int passed = 0, failed = 0;

    public static void main(String[] args) {

        header("ESPORT — TEST METIER COMPLET");

        testTournamentMetier();
        testEventMetier();
        testMatchMetier();
        testBetMetier();

        summary();
        DatabaseConnection.closeConnection();
    }

    // =========================================================================
    static void testTournamentMetier() {
        section("1. TOURNAMENT METIER");
        int id = -1;

        // Ajouter
        try {
            Tournament t = tournamentMetier.ajouter(
                    "MAVEN CUP 2026", "Valorant",
                    Tournament.Format.SINGLE_ELIMINATION,
                    8, new BigDecimal("3000"), new BigDecimal("15"),
                    LocalDateTime.now().plusDays(15),
                    LocalDateTime.now().plusDays(12),
                    "Test Maven tournoi", 1);
            id = t.getId();
            pass("ajouter() → ID: " + id + " | " + t.getName());
        } catch (Exception e) { fail("ajouter()", e); }

        // Rechercher par ID
        try {
            Tournament t = tournamentMetier.rechercherParId(id);
            pass("rechercherParId(" + id + ") → " + t.getName());
        } catch (Exception e) { fail("rechercherParId()", e); }

        // Rechercher par nom
        try {
            List<Tournament> list = tournamentMetier.rechercherParNom("MAVEN");
            pass("rechercherParNom('MAVEN') → " + list.size() + " résultat(s)");
        } catch (Exception e) { fail("rechercherParNom()", e); }

        // Rechercher par jeu
        try {
            List<Tournament> list = tournamentMetier.rechercherParJeu("Valorant");
            pass("rechercherParJeu('Valorant') → " + list.size() + " résultat(s)");
        } catch (Exception e) { fail("rechercherParJeu()", e); }

        // Rechercher par status
        try {
            List<Tournament> list = tournamentMetier.rechercherParStatus(Tournament.Status.UPCOMING);
            pass("rechercherParStatus(UPCOMING) → " + list.size() + " résultat(s)");
        } catch (Exception e) { fail("rechercherParStatus()", e); }

        // Lister tous
        try {
            List<Tournament> list = tournamentMetier.listerTous();
            pass("listerTous() → " + list.size() + " tournois");
            list.forEach(t -> System.out.println("   → " + t));
        } catch (Exception e) { fail("listerTous()", e); }

        // Inscrire équipe
        try {
            tournamentMetier.inscrireEquipe(id, 1);
            tournamentMetier.inscrireEquipe(id, 2);
            pass("inscrireEquipe() → 2 équipes inscrites");
        } catch (Exception e) { fail("inscrireEquipe()", e); }

        // Modifier
        try {
            tournamentMetier.modifier(id, "MAVEN CUP 2026 - UPDATED", null, null,
                    0, new BigDecimal("5000"), null, null, null, "Description mise à jour");
            pass("modifier() → nom et prize pool mis à jour");
        } catch (Exception e) { fail("modifier()", e); }

        // Demarrer
        try {
            tournamentMetier.demarrer(id);
            pass("demarrer() → statut ONGOING");
        } catch (Exception e) { fail("demarrer()", e); }

        // Terminer
        try {
            tournamentMetier.terminer(id);
            pass("terminer() → statut COMPLETED");
        } catch (Exception e) { fail("terminer()", e); }

        // Supprimer (doit échouer car COMPLETED — on force en UPCOMING d'abord via DAO)
        try {
            // Re-create a fresh one to delete
            Tournament del = tournamentMetier.ajouter(
                    "TO DELETE", "CS2", Tournament.Format.SWISS,
                    4, BigDecimal.ZERO, BigDecimal.ZERO,
                    LocalDateTime.now().plusDays(5), null, null, 1);
            tournamentMetier.supprimer(del.getId());
            pass("supprimer() → tournoi supprimé");
        } catch (Exception e) { fail("supprimer()", e); }
    }

    // =========================================================================
    static void testEventMetier() {
        section("2. EVENT METIER");
        int id = -1;

        // Ajouter
        try {
            Event ev = eventMetier.ajouter(
                    "MAVEN LAN EVENT", Event.Type.LAN, "CS2",
                    "Tunis Arena", false,
                    LocalDateTime.now().plusDays(20),
                    LocalDateTime.now().plusDays(22),
                    100, new BigDecimal("25"), "Événement test", 1);
            id = ev.getId();
            pass("ajouter() → ID: " + id + " | " + ev.getTitle());
        } catch (Exception e) { fail("ajouter()", e); }

        // Rechercher par ID
        try {
            Event ev = eventMetier.rechercherParId(id);
            pass("rechercherParId(" + id + ") → " + ev.getTitle());
        } catch (Exception e) { fail("rechercherParId()", e); }

        // Rechercher par titre
        try {
            List<Event> list = eventMetier.rechercherParTitre("MAVEN");
            pass("rechercherParTitre('MAVEN') → " + list.size() + " résultat(s)");
        } catch (Exception e) { fail("rechercherParTitre()", e); }

        // Rechercher par type
        try {
            List<Event> list = eventMetier.rechercherParType(Event.Type.LAN);
            pass("rechercherParType(LAN) → " + list.size() + " résultat(s)");
        } catch (Exception e) { fail("rechercherParType()", e); }

        // Lister tous
        try {
            List<Event> list = eventMetier.listerTous();
            pass("listerTous() → " + list.size() + " événements");
        } catch (Exception e) { fail("listerTous()", e); }

        // Modifier
        try {
            eventMetier.modifier(id, "MAVEN LAN EVENT - UPDATED",
                    null, null, "Salle 2", null, null, 200,
                    new BigDecimal("30"), null);
            pass("modifier() → titre et capacité mis à jour");
        } catch (Exception e) { fail("modifier()", e); }

        // Publier
        try {
            eventMetier.publier(id);
            pass("publier() → statut PUBLISHED");
        } catch (Exception e) { fail("publier()", e); }

        // Lister publiés
        try {
            List<Event> list = eventMetier.listerPublies();
            pass("listerPublies() → " + list.size() + " publiés");
        } catch (Exception e) { fail("listerPublies()", e); }

        // Inscrire user
        try {
            String ticket = eventMetier.inscrireUser(id, 1);
            pass("inscrireUser() → Ticket: " + ticket);
        } catch (Exception e) { fail("inscrireUser()", e); }

        // Compter inscrits
        try {
            int count = eventMetier.compterInscrits(id);
            pass("compterInscrits() → " + count + " inscrit(s)");
        } catch (Exception e) { fail("compterInscrits()", e); }

        // Annuler
        try {
            eventMetier.annuler(id);
            pass("annuler() → statut CANCELLED");
        } catch (Exception e) { fail("annuler()", e); }

        // Supprimer
        try {
            eventMetier.supprimer(id);
            pass("supprimer() → événement supprimé");
        } catch (Exception e) { fail("supprimer()", e); }
    }

    // =========================================================================
    static void testMatchMetier() {
        section("3. MATCH METIER");
        int id = -1;

        // Ajouter
        try {
            TournamentMatch m = matchMetier.ajouter(
                    1, 99, 1, 1, 2,
                    LocalDateTime.now().plusHours(2));
            id = m.getId();
            pass("ajouter() → ID: " + id + " | " + m);
        } catch (Exception e) { fail("ajouter()", e); }

        // Rechercher
        try {
            TournamentMatch m = matchMetier.rechercherParId(id);
            pass("rechercherParId(" + id + ") → " + m);
        } catch (Exception e) { fail("rechercherParId()", e); }

        // Modifier
        try {
            matchMetier.modifier(id, null, "https://twitch.tv/test");
            pass("modifier() → stream URL mis à jour");
        } catch (Exception e) { fail("modifier()", e); }

        // Demarrer
        try {
            matchMetier.demarrer(id);
            pass("demarrer() → statut LIVE");
        } catch (Exception e) { fail("demarrer()", e); }

        // Lister live
        try {
            List<TournamentMatch> list = matchMetier.listerLive();
            pass("listerLive() → " + list.size() + " match(s) live");
        } catch (Exception e) { fail("listerLive()", e); }

        // Mettre à jour score
        try {
            matchMetier.mettreAJourScore(id, 3, 1);
            pass("mettreAJourScore() → 3-1");
        } catch (Exception e) { fail("mettreAJourScore()", e); }

        // Terminer
        try {
            int settled = matchMetier.terminer(id, 1, 3, 1);
            pass("terminer() → Gagnant Team#1 | " + settled + " paris réglés");
        } catch (Exception e) { fail("terminer()", e); }

        // Lister par tournoi
        try {
            List<TournamentMatch> list = matchMetier.listerParTournoi(1);
            pass("listerParTournoi(1) → " + list.size() + " match(s)");
        } catch (Exception e) { fail("listerParTournoi()", e); }
    }

    // =========================================================================
    static void testBetMetier() {
        section("4. BET METIER");

        // Creer wallet
        try {
            betMetier.creerWallet(1);
            pass("creerWallet(1) → OK");
        } catch (Exception e) { fail("creerWallet()", e); }

        // Consulter solde
        try {
            BigDecimal b = betMetier.consulterSolde(1);
            pass("consulterSolde(1) → $" + b);
        } catch (Exception e) { fail("consulterSolde()", e); }

        // Deposer
        try {
            betMetier.deposer(1, new BigDecimal("100.00"));
            pass("deposer($100) → OK");
        } catch (Exception e) { fail("deposer()", e); }

        // Deposer en dessous du minimum (doit échouer)
        try {
            betMetier.deposer(1, new BigDecimal("2.00"));
            fail("deposer($2) devrait échouer", null);
        } catch (Exception e) {
            pass("deposer($2) rejeté correctement: " + e.getMessage());
        }

        // Placer pari sur match existant
        try {
            // match 1 is COMPLETED, use a live one if exists
            List<TournamentMatch> live = matchMetier.listerLive();
            if (!live.isEmpty()) {
                TournamentMatch m = live.get(0);
                Bet bet = betMetier.placerPari(1, m.getId(), m.getTeam1Id(),
                        new BigDecimal("25.00"), new BigDecimal("2.00"));
                pass("placerPari() → Pari#" + bet.getId() +
                        " | Gain potentiel: $" + bet.getPotentialWin());

                // Annuler pari
                betMetier.annulerPari(bet.getId());
                pass("annulerPari() → remboursé $25");
            } else {
                pass("placerPari() → ignoré (aucun match live)");
            }
        } catch (Exception e) { fail("placerPari()", e); }

        // Lister paris user
        try {
            List<Bet> bets = betMetier.listerParisUser(1);
            pass("listerParisUser(1) → " + bets.size() + " pari(s)");
            bets.forEach(b -> System.out.println("   → " + b));
        } catch (Exception e) { fail("listerParisUser()", e); }

        // Lister tous
        try {
            List<Bet> bets = betMetier.listerTous();
            pass("listerTous() → " + bets.size() + " paris au total");
        } catch (Exception e) { fail("listerTous()", e); }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    static void pass(String msg) { passed++; System.out.println("  ✅ " + msg); }
    static void fail(String msg, Exception e) {
        failed++;
        System.out.println("  ❌ FAIL — " + msg);
        if (e != null) System.out.println("       → " + e.getMessage());
    }
    static void section(String t) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  " + t);
        System.out.println("╚══════════════════════════════════════════╝");
    }
    static void header(String t) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║  " + t + "  ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }
    static void summary() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║            RÉSULTATS DES TESTS                 ║");
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.println("║  ✅ RÉUSSIS : " + passed);
        System.out.println("║  ❌ ÉCHOUÉS : " + failed);
        System.out.println("║  📊 TOTAL   : " + (passed + failed));
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println(failed == 0
                ? "\n🎉 TOUS LES TESTS RÉUSSIS!"
                : "\n⚠️  Certains tests ont échoué.");
    }
}