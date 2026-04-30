package com.esport.metier;

import com.esport.dao.TournamentDAO;
import com.esport.model.Tournament;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * TournamentMetier — all business operations for tournaments.
 *
 * Operations:
 *  - ajouter()     → Add a tournament
 *  - modifier()    → Modify a tournament
 *  - supprimer()   → Delete a tournament
 *  - rechercher()  → Search by ID
 *  - rechercher by name, game, status
 *  - listerTous()  → List all
 *  - demarrer()    → Start tournament
 *  - terminer()    → Complete tournament
 *  - inscrireEquipe() → Register a team
 */
public class TournamentMetier {

    private final TournamentDAO dao = new TournamentDAO();

    // =========================================================================
    //  AJOUTER — Add a new tournament
    // =========================================================================
    public Tournament ajouter(String name, String game, Tournament.Format format,
                              int maxTeams, BigDecimal prizePool, BigDecimal entryFee,
                              LocalDateTime startDate, LocalDateTime regDeadline,
                              String description, int createdBy) throws Exception {

        // Validation
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Le nom du tournoi est obligatoire.");
        if (game == null || game.isBlank())
            throw new IllegalArgumentException("Le jeu est obligatoire.");
        if (maxTeams < 2)
            throw new IllegalArgumentException("Un tournoi nécessite au moins 2 équipes.");
        if (startDate == null)
            throw new IllegalArgumentException("La date de début est obligatoire.");
        if (startDate.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("La date de début doit être dans le futur.");
        if (prizePool != null && prizePool.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Le prize pool ne peut pas être négatif.");

        Tournament t = new Tournament(name, game, format, maxTeams,
                prizePool != null ? prizePool : BigDecimal.ZERO,
                entryFee  != null ? entryFee  : BigDecimal.ZERO,
                startDate, regDeadline);
        t.setDescription(description);
        t.setCreatedBy(createdBy);

        int id = dao.create(t);
        if (id < 0) throw new Exception("Échec de la création du tournoi.");
        t.setId(id);

        System.out.println("✅ Tournoi créé: " + t);
        return t;
    }

    // =========================================================================
    //  MODIFIER — Modify an existing tournament
    // =========================================================================
    public Tournament modifier(int id, String name, String game, Tournament.Format format,
                               int maxTeams, BigDecimal prizePool, BigDecimal entryFee,
                               LocalDateTime startDate, LocalDateTime regDeadline,
                               String description) throws Exception {

        Optional<Tournament> opt = dao.findById(id);
        if (opt.isEmpty())
            throw new Exception("Tournoi #" + id + " introuvable.");

        Tournament t = opt.get();
        if (t.getStatus() == Tournament.Status.COMPLETED)
            throw new Exception("Impossible de modifier un tournoi terminé.");

        // Apply changes
        if (name        != null && !name.isBlank())  t.setName(name);
        if (game        != null && !game.isBlank())  t.setGame(game);
        if (format      != null)                     t.setFormat(format);
        if (maxTeams    > 0)                         t.setMaxTeams(maxTeams);
        if (prizePool   != null)                     t.setPrizePool(prizePool);
        if (entryFee    != null)                     t.setEntryFee(entryFee);
        if (startDate   != null)                     t.setStartDate(startDate);
        if (regDeadline != null)                     t.setRegistrationDeadline(regDeadline);
        if (description != null)                     t.setDescription(description);

        boolean ok = dao.update(t);
        if (!ok) throw new Exception("Échec de la mise à jour du tournoi #" + id);

        System.out.println("✅ Tournoi modifié: " + t);
        return t;
    }

    // =========================================================================
    //  SUPPRIMER — Delete a tournament by ID
    // =========================================================================
    public void supprimer(int id) throws Exception {
        Optional<Tournament> opt = dao.findById(id);
        if (opt.isEmpty())
            throw new Exception("Tournoi #" + id + " introuvable.");

        Tournament t = opt.get();
        if (t.getStatus() == Tournament.Status.ONGOING)
            throw new Exception("Impossible de supprimer un tournoi en cours.");

        boolean ok = dao.delete(id);
        if (!ok) throw new Exception("Échec de la suppression du tournoi #" + id);
        System.out.println("🗑 Tournoi #" + id + " supprimé.");
    }

    // =========================================================================
    //  RECHERCHER PAR ID — Find one tournament by ID
    // =========================================================================
    public Tournament rechercherParId(int id) throws Exception {
        Optional<Tournament> opt = dao.findById(id);
        if (opt.isEmpty())
            throw new Exception("Aucun tournoi trouvé avec l'ID #" + id);
        return opt.get();
    }

    // =========================================================================
    //  RECHERCHER PAR NOM — Search tournaments by name
    // =========================================================================
    public List<Tournament> rechercherParNom(String nom) throws SQLException {
        List<Tournament> results = dao.findByName(nom);
        System.out.println("🔍 Recherche '" + nom + "' → " + results.size() + " résultat(s)");
        return results;
    }

    // =========================================================================
    //  RECHERCHER PAR JEU — Search tournaments by game
    // =========================================================================
    public List<Tournament> rechercherParJeu(String jeu) throws SQLException {
        List<Tournament> results = dao.findByGame(jeu);
        System.out.println("🔍 Jeu '" + jeu + "' → " + results.size() + " résultat(s)");
        return results;
    }

    // =========================================================================
    //  RECHERCHER PAR STATUS — Filter by status
    // =========================================================================
    public List<Tournament> rechercherParStatus(Tournament.Status status) throws SQLException {
        List<Tournament> results = dao.findByStatus(status);
        System.out.println("🔍 Status '" + status + "' → " + results.size() + " résultat(s)");
        return results;
    }

    // =========================================================================
    //  LISTER TOUS — List all tournaments
    // =========================================================================
    public List<Tournament> listerTous() throws SQLException {
        return dao.findAll();
    }

    // =========================================================================
    //  DEMARRER — Start a tournament (UPCOMING → ONGOING)
    // =========================================================================
    public void demarrer(int id) throws Exception {
        Tournament t = rechercherParId(id);
        if (t.getStatus() != Tournament.Status.UPCOMING)
            throw new Exception("Seul un tournoi 'upcoming' peut être démarré.");

        int teams = dao.countRegisteredTeams(id);
        if (teams < 2)
            throw new Exception("Il faut au moins 2 équipes inscrites pour démarrer. Actuellement: " + teams);

        dao.updateStatus(id, Tournament.Status.ONGOING);
        System.out.println("▶ Tournoi #" + id + " démarré! (" + teams + " équipes)");
    }

    // =========================================================================
    //  TERMINER — Complete a tournament
    // =========================================================================
    public void terminer(int id) throws Exception {
        Tournament t = rechercherParId(id);
        if (t.getStatus() == Tournament.Status.COMPLETED)
            throw new Exception("Ce tournoi est déjà terminé.");

        dao.updateStatus(id, Tournament.Status.COMPLETED);
        System.out.println("🏁 Tournoi #" + id + " terminé.");
    }

    // =========================================================================
    //  INSCRIRE EQUIPE — Register a team to a tournament
    // =========================================================================
    public void inscrireEquipe(int tournamentId, int teamId) throws Exception {
        Tournament t = rechercherParId(tournamentId);

        if (t.getStatus() != Tournament.Status.UPCOMING)
            throw new Exception("Les inscriptions sont fermées (tournoi pas en statut 'upcoming').");

        if (!dao.isRegistrationOpen(tournamentId))
            throw new Exception("La deadline d'inscription est dépassée.");

        int current = dao.countRegisteredTeams(tournamentId);
        if (current >= t.getMaxTeams())
            throw new Exception("Tournoi complet! Max: " + t.getMaxTeams() + " équipes.");

        int regId = dao.registerTeam(tournamentId, teamId);
        if (regId < 0) throw new Exception("Échec de l'inscription de l'équipe.");
        System.out.println("✅ Équipe #" + teamId + " inscrite au tournoi #" + tournamentId +
                " (" + (current + 1) + "/" + t.getMaxTeams() + ")");
    }
}