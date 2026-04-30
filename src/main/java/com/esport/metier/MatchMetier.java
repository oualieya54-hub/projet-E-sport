package com.esport.metier;

import com.esport.dao.MatchDAO;
import com.esport.model.TournamentMatch;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MatchMetier — all business operations for tournament matches.
 */
public class MatchMetier {

    private final MatchDAO dao    = new MatchDAO();
    private final BetMetier betMetier = new BetMetier();

    // =========================================================================
    //  AJOUTER — Schedule a new match
    // =========================================================================
    public TournamentMatch ajouter(int tournamentId, int round, int matchNumber,
                                   int team1Id, int team2Id,
                                   LocalDateTime scheduledAt) throws Exception {
        if (team1Id == team2Id)
            throw new IllegalArgumentException("Les deux équipes doivent être différentes.");
        if (scheduledAt == null)
            throw new IllegalArgumentException("La date est obligatoire.");

        TournamentMatch m = new TournamentMatch(tournamentId, round,
                matchNumber, team1Id, team2Id, scheduledAt);
        int id = dao.create(m);
        if (id < 0) throw new Exception("Échec de la création du match.");
        m.setId(id);
        System.out.println("✅ Match créé: " + m);
        return m;
    }

    // =========================================================================
    //  MODIFIER — Update match details
    // =========================================================================
    public TournamentMatch modifier(int id, LocalDateTime scheduledAt,
                                    String streamUrl) throws Exception {
        Optional<TournamentMatch> opt = dao.findById(id);
        if (opt.isEmpty()) throw new Exception("Match #" + id + " introuvable.");
        TournamentMatch m = opt.get();
        if (m.getStatus() == TournamentMatch.Status.COMPLETED)
            throw new Exception("Impossible de modifier un match terminé.");
        if (scheduledAt != null) m.setScheduledAt(scheduledAt);
        if (streamUrl   != null) m.setStreamUrl(streamUrl);
        dao.update(m);
        System.out.println("✅ Match #" + id + " modifié.");
        return m;
    }

    // =========================================================================
    //  SUPPRIMER
    // =========================================================================
    public void supprimer(int id) throws Exception {
        Optional<TournamentMatch> opt = dao.findById(id);
        if (opt.isEmpty()) throw new Exception("Match #" + id + " introuvable.");
        dao.delete(id);
        System.out.println("🗑 Match #" + id + " supprimé.");
    }

    // =========================================================================
    //  RECHERCHER PAR ID
    // =========================================================================
    public TournamentMatch rechercherParId(int id) throws Exception {
        Optional<TournamentMatch> opt = dao.findById(id);
        if (opt.isEmpty()) throw new Exception("Match #" + id + " introuvable.");
        return opt.get();
    }

    // =========================================================================
    //  DEMARRER — SCHEDULED → LIVE
    // =========================================================================
    public void demarrer(int id) throws Exception {
        TournamentMatch m = rechercherParId(id);
        if (m.getStatus() != TournamentMatch.Status.SCHEDULED)
            throw new Exception("Le match n'est pas en statut 'scheduled'.");
        dao.updateStatus(id, TournamentMatch.Status.LIVE);
        System.out.println("🔴 Match #" + id + " est maintenant LIVE!");
    }

    // =========================================================================
    //  METTRE A JOUR LE SCORE
    // =========================================================================
    public void mettreAJourScore(int id, int scoreT1, int scoreT2) throws Exception {
        TournamentMatch m = rechercherParId(id);
        if (m.getStatus() != TournamentMatch.Status.LIVE)
            throw new Exception("Le score ne peut être mis à jour que pour un match LIVE.");
        dao.updateScore(id, scoreT1, scoreT2);
        System.out.println("📊 Score mis à jour: Team" + m.getTeam1Id() + " " +
                scoreT1 + " - " + scoreT2 + " Team" + m.getTeam2Id());
    }

    // =========================================================================
    //  TERMINER — Complete match + auto-settle bets
    // =========================================================================
    public int terminer(int id, int winnerId, int scoreT1, int scoreT2) throws Exception {
        TournamentMatch m = rechercherParId(id);
        if (m.getStatus() == TournamentMatch.Status.COMPLETED)
            throw new Exception("Ce match est déjà terminé.");
        if (winnerId != m.getTeam1Id() && winnerId != m.getTeam2Id())
            throw new Exception("L'équipe gagnante doit être team1 ou team2.");

        dao.completeMatch(id, winnerId, scoreT1, scoreT2);
        int settled = betMetier.reglerParis(id, winnerId);

        System.out.println("🏁 Match #" + id + " terminé! Score: " +
                scoreT1 + "-" + scoreT2 + " | Gagnant: Team #" + winnerId +
                " | " + settled + " paris réglés");
        return settled;
    }

    // =========================================================================
    //  ANNULER
    // =========================================================================
    public void annuler(int id) throws Exception {
        TournamentMatch m = rechercherParId(id);
        if (m.getStatus() == TournamentMatch.Status.COMPLETED)
            throw new Exception("Impossible d'annuler un match terminé.");
        dao.updateStatus(id, TournamentMatch.Status.CANCELLED);
        System.out.println("❌ Match #" + id + " annulé.");
    }

    // =========================================================================
    //  QUERIES
    // =========================================================================
    public List<TournamentMatch> listerParTournoi(int tournamentId) throws SQLException {
        return dao.findByTournament(tournamentId);
    }

    public List<TournamentMatch> listerParRound(int tournamentId, int round) throws SQLException {
        return dao.findByRound(tournamentId, round);
    }

    public List<TournamentMatch> listerLive() throws SQLException {
        return dao.findLive();
    }

    public List<TournamentMatch> listerTous() throws SQLException {
        return dao.findAll();
    }
}