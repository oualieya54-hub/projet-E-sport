package org.example.Model;

import java.time.LocalDateTime;

public class Booking {
    private int idBooking;
    private int idSession;
    private int idEleve;
    private String statutPaiement;
    private LocalDateTime dateReservation;
    private String modePaiement;

    public Booking(int idBooking, int idSession, int idEleve, String statutPaiement, LocalDateTime dateReservation, String modePaiement) {
        this.idBooking      = idBooking;
        this.idSession      = idSession;
        this.idEleve        = idEleve;
        this.statutPaiement = statutPaiement;
        this.dateReservation = dateReservation;
        this.modePaiement   = modePaiement;
    }

    // Additional constructor for easier object creation before DB insert
    public Booking(int idBooking, int idSession, int idEleve, String statutPaiement) {
        this.idBooking      = idBooking;
        this.idSession      = idSession;
        this.idEleve        = idEleve;
        this.statutPaiement = statutPaiement;
        this.dateReservation = LocalDateTime.now();
        this.modePaiement   = "carte"; // Default as per DB
    }

    public int    getIdBooking()      { return idBooking; }
    public int    getIdSession()      { return idSession; }
    public int    getIdEleve()        { return idEleve; }
    public String getStatutPaiement() { return statutPaiement; }
    public LocalDateTime getDateReservation() { return dateReservation; }
    public String getModePaiement() { return modePaiement; }

    public void setIdBooking(int idBooking) { this.idBooking = idBooking; }
    public void setIdSession(int idSession) { this.idSession = idSession; }
    public void setIdEleve(int idEleve) { this.idEleve = idEleve; }
    public void setStatutPaiement(String statutPaiement) { this.statutPaiement = statutPaiement; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    @Override
    public String toString() {
        return "Booking { id=" + idBooking + ", idSession=" + idSession + ", statut='" + statutPaiement + "', modePaiement='" + modePaiement + "', dateReservation=" + dateReservation + " }";
    }
}
