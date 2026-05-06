package org.example.Model;

import java.time.LocalDateTime;

public class Evaluation {
    private int           idEvaluation;
    private int           idBooking;
    private int           note;          // 1–5 recommended
    private String        commentaire;
    private LocalDateTime dateEval;

    // Full constructor (used when reading from DB)
    public Evaluation(int idEvaluation, int idBooking, int note,
                      String commentaire, LocalDateTime dateEval) {
        this.idEvaluation = idEvaluation;
        this.idBooking    = idBooking;
        this.note         = note;
        this.commentaire  = commentaire;
        this.dateEval     = dateEval;
    }

    // Convenience constructor (before DB insert — date_eval defaults to NOW())
    public Evaluation(int idBooking, int note, String commentaire) {
        this.idEvaluation = 0;
        this.idBooking    = idBooking;
        this.note         = note;
        this.commentaire  = commentaire;
        this.dateEval     = LocalDateTime.now();
    }

    // Getters
    public int           getIdEvaluation() { return idEvaluation; }
    public int           getIdBooking()    { return idBooking; }
    public int           getNote()         { return note; }
    public String        getCommentaire()  { return commentaire; }
    public LocalDateTime getDateEval()    { return dateEval; }

    // Setters
    public void setIdEvaluation(int idEvaluation)          { this.idEvaluation = idEvaluation; }
    public void setIdBooking(int idBooking)                { this.idBooking = idBooking; }
    public void setNote(int note)                          { this.note = note; }
    public void setCommentaire(String commentaire)         { this.commentaire = commentaire; }
    public void setDateEval(LocalDateTime dateEval)       { this.dateEval = dateEval; }

    @Override
    public String toString() {
        return "Evaluation { id=" + idEvaluation + ", idBooking=" + idBooking +
               ", note=" + note + ", dateEval=" + dateEval + " }";
    }
}
