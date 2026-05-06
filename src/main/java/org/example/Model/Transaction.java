package org.example.Model;

import java.time.LocalDateTime;

public class Transaction {
    private int idTransaction;
    private int idUser;
    private int idProduit;
    private Integer idCommande;
    private String type;
    private int quantite;
    private Double prixUnitaire;
    private Integer note;
    private String commentaire;
    private String dataJson;
    private LocalDateTime dateAction;

    public Transaction() {
    }

    public Transaction(int idUser, int idProduit, Integer idCommande, String type, int quantite, Double prixUnitaire) {
        this.idUser = idUser;
        this.idProduit = idProduit;
        this.idCommande = idCommande;
        this.type = type;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public Transaction(int idUser, int idProduit, Integer note, String commentaire) {
        this.idUser = idUser;
        this.idProduit = idProduit;
        this.note = note;
        this.commentaire = commentaire;
        this.type = "avis"; // Par défaut pour ce constructeur
    }

    public int getIdTransaction() { return idTransaction; }
    public void setIdTransaction(int idTransaction) { this.idTransaction = idTransaction; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }

    public Integer getIdCommande() { return idCommande; }
    public void setIdCommande(Integer idCommande) { this.idCommande = idCommande; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public String getDataJson() { return dataJson; }
    public void setDataJson(String dataJson) { this.dataJson = dataJson; }

    public LocalDateTime getDateAction() { return dateAction; }
    public void setDateAction(LocalDateTime dateAction) { this.dateAction = dateAction; }

    @Override
    public String toString() {
        return "Transaction{" +
                "idTransaction=" + idTransaction +
                ", idUser=" + idUser +
                ", idProduit=" + idProduit +
                ", idCommande=" + idCommande +
                ", type='" + type + '\'' +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", note=" + note +
                ", commentaire='" + commentaire + '\'' +
                ", dateAction=" + dateAction +
                '}';
    }
}