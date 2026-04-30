package com.esport.metier;

import com.esport.dao.EventDAO;
import com.esport.model.Event;
import com.esport.util.AppUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * EventMetier — all business operations for events.
 *
 * Operations:
 *  - ajouter()        → Add an event
 *  - modifier()       → Modify an event
 *  - supprimer()      → Delete an event
 *  - rechercher()     → Search by ID / title / type
 *  - publier()        → Publish an event
 *  - annuler()        → Cancel an event
 *  - inscrireUser()   → Register a user → returns ticket code
 */
public class EventMetier {

    private final EventDAO dao = new EventDAO();

    // =========================================================================
    //  AJOUTER — Add a new event
    // =========================================================================
    public Event ajouter(String title, Event.Type type, String game,
                         String location, boolean isOnline,
                         LocalDateTime startDate, LocalDateTime endDate,
                         Integer capacity, BigDecimal ticketPrice,
                         String description, int organizerId) throws Exception {

        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Le titre de l'événement est obligatoire.");
        if (startDate == null)
            throw new IllegalArgumentException("La date de début est obligatoire.");
        if (endDate != null && endDate.isBefore(startDate))
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        if (capacity != null && capacity < 1)
            throw new IllegalArgumentException("La capacité doit être supérieure à 0.");

        Event ev = new Event(title, type, game, startDate, endDate,
                ticketPrice != null ? ticketPrice : BigDecimal.ZERO);
        ev.setLocation(location);
        ev.setOnline(isOnline);
        ev.setCapacity(capacity);
        ev.setDescription(description);
        ev.setOrganizerId(organizerId);

        int id = dao.create(ev);
        if (id < 0) throw new Exception("Échec de la création de l'événement.");
        ev.setId(id);

        System.out.println("✅ Événement créé: " + ev);
        return ev;
    }

    // =========================================================================
    //  MODIFIER — Modify an event
    // =========================================================================
    public Event modifier(int id, String title, Event.Type type, String game,
                          String location, LocalDateTime startDate,
                          LocalDateTime endDate, Integer capacity,
                          BigDecimal ticketPrice, String description) throws Exception {

        Optional<Event> opt = dao.findById(id);
        if (opt.isEmpty())
            throw new Exception("Événement #" + id + " introuvable.");

        Event ev = opt.get();
        if (ev.getStatus() == Event.Status.CANCELLED)
            throw new Exception("Impossible de modifier un événement annulé.");
        if (ev.getStatus() == Event.Status.COMPLETED)
            throw new Exception("Impossible de modifier un événement terminé.");

        if (title       != null && !title.isBlank()) ev.setTitle(title);
        if (type        != null)                     ev.setType(type);
        if (game        != null)                     ev.setGame(game);
        if (location    != null)                     ev.setLocation(location);
        if (startDate   != null)                     ev.setStartDate(startDate);
        if (endDate     != null)                     ev.setEndDate(endDate);
        if (capacity    != null)                     ev.setCapacity(capacity);
        if (ticketPrice != null)                     ev.setTicketPrice(ticketPrice);
        if (description != null)                     ev.setDescription(description);

        boolean ok = dao.update(ev);
        if (!ok) throw new Exception("Échec de la mise à jour de l'événement #" + id);

        System.out.println("✅ Événement modifié: " + ev);
        return ev;
    }

    // =========================================================================
    //  SUPPRIMER — Delete an event
    // =========================================================================
    public void supprimer(int id) throws Exception {
        Optional<Event> opt = dao.findById(id);
        if (opt.isEmpty())
            throw new Exception("Événement #" + id + " introuvable.");

        boolean ok = dao.delete(id);
        if (!ok) throw new Exception("Échec de la suppression de l'événement #" + id);
        System.out.println("🗑 Événement #" + id + " supprimé.");
    }

    // =========================================================================
    //  RECHERCHER PAR ID
    // =========================================================================
    public Event rechercherParId(int id) throws Exception {
        Optional<Event> opt = dao.findById(id);
        if (opt.isEmpty())
            throw new Exception("Aucun événement trouvé avec l'ID #" + id);
        return opt.get();
    }

    // =========================================================================
    //  RECHERCHER PAR TITRE
    // =========================================================================
    public List<Event> rechercherParTitre(String titre) throws SQLException {
        List<Event> results = dao.findByTitle(titre);
        System.out.println("🔍 Titre '" + titre + "' → " + results.size() + " résultat(s)");
        return results;
    }

    // =========================================================================
    //  RECHERCHER PAR TYPE
    // =========================================================================
    public List<Event> rechercherParType(Event.Type type) throws SQLException {
        List<Event> results = dao.findByType(type);
        System.out.println("🔍 Type '" + type + "' → " + results.size() + " résultat(s)");
        return results;
    }

    // =========================================================================
    //  LISTER TOUS
    // =========================================================================
    public List<Event> listerTous() throws SQLException {
        return dao.findAll();
    }

    // =========================================================================
    //  LISTER PUBLIES
    // =========================================================================
    public List<Event> listerPublies() throws SQLException {
        return dao.findPublished();
    }

    // =========================================================================
    //  PUBLIER — Publish event (DRAFT → PUBLISHED)
    // =========================================================================
    public void publier(int id) throws Exception {
        Event ev = rechercherParId(id);
        if (ev.getStatus() != Event.Status.DRAFT)
            throw new Exception("Seul un événement en 'draft' peut être publié.");

        dao.updateStatus(id, Event.Status.PUBLISHED);
        System.out.println("📢 Événement #" + id + " publié.");
    }

    // =========================================================================
    //  ANNULER — Cancel an event
    // =========================================================================
    public void annuler(int id) throws Exception {
        Event ev = rechercherParId(id);
        if (ev.getStatus() == Event.Status.COMPLETED)
            throw new Exception("Impossible d'annuler un événement terminé.");

        dao.updateStatus(id, Event.Status.CANCELLED);
        System.out.println("❌ Événement #" + id + " annulé.");
    }

    // =========================================================================
    //  INSCRIRE USER — Register a user, returns ticket code
    // =========================================================================
    public String inscrireUser(int eventId, int userId) throws Exception {
        Event ev = rechercherParId(eventId);

        if (ev.getStatus() != Event.Status.PUBLISHED)
            throw new Exception("L'événement n'est pas ouvert aux inscriptions.");

        if (ev.getCapacity() != null) {
            int current = dao.countRegistrations(eventId);
            if (current >= ev.getCapacity())
                throw new Exception("Événement complet! Capacité max: " + ev.getCapacity());
        }

        String ticketCode = AppUtil.generateTicketCode();
        int regId = dao.registerUser(eventId, userId, ticketCode);
        if (regId < 0) throw new Exception("Échec de l'inscription.");

        System.out.println("🎟 User #" + userId + " inscrit à l'événement #" + eventId +
                " | Ticket: " + ticketCode);
        return ticketCode;
    }

    // =========================================================================
    //  MARQUER PRESENCE
    // =========================================================================
    public void marquerPresence(int eventId, int userId) throws Exception {
        boolean ok = dao.markAttended(eventId, userId);
        if (!ok) throw new Exception("Utilisateur #" + userId + " non inscrit à cet événement.");
        System.out.println("✅ Présence marquée: User #" + userId + " → Événement #" + eventId);
    }

    // =========================================================================
    //  COMPTER INSCRITS
    // =========================================================================
    public int compterInscrits(int eventId) throws SQLException {
        return dao.countRegistrations(eventId);
    }
}