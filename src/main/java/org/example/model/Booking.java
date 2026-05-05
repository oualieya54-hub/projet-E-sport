package org.example.Model;

public class Booking {
    private int idBooking;
    private int idSession;
    private int idEleve;
    private String statutPaiement;

    public Booking(int idBooking, int idSession, int idEleve, String statutPaiement) {
        this.idBooking      = idBooking;
        this.idSession      = idSession;
        this.idEleve        = idEleve;
        this.statutPaiement = statutPaiement;
    }

    public int    getIdBooking()      { return idBooking; }
    public int    getIdSession()      { return idSession; }
    public int    getIdEleve()        { return idEleve; }
    public String getStatutPaiement() { return statutPaiement; }

    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }

    public void setIdSession(int idSession) {
        this.idSession = idSession;
    }

    public void setIdEleve(int idEleve) {
        this.idEleve = idEleve;
    }

    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    @Override
    public String toString() {
        return "Booking { id=" + idBooking + ", idSession=" + idSession + ", statut='" + statutPaiement + "' }";
    }
}
