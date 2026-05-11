package org.example.controller.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Model.Booking;
import org.example.Model.Formation;
import org.example.Model.Session;
import org.example.Service.BookingService;
import org.example.Service.EmailService;
import org.example.Service.SessionService;
import org.example.Utils.EmailValidator;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FormationDetailsController {

    @FXML private Label titreLabel;
    @FXML private Label jeuLabel;
    @FXML private Label prixLabel;
    @FXML private Label descLabel;
    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, String> colDate;
    @FXML private TableColumn<Session, String> colType;
    @FXML private TableColumn<Session, String> colCoach;

    @FXML private TextField userPrenomField;
    @FXML private TextField userNameField;
    @FXML private TextField userEmailField;

    private Formation formation;
    private final SessionService sessionService = new SessionService();
    private final BookingService bookingService = new BookingService();
    private final EmailService emailService = new EmailService();
    private final ObservableList<Session> sessionList = FXCollections.observableArrayList();

    public void setFormation(Formation f) {
        this.formation = f;
        titreLabel.setText(f.getTitre().toUpperCase());
        jeuLabel.setText(f.getJeu() + " | " + f.getNiveau());
        prixLabel.setText(f.getPrix() + " TND");
        descLabel.setText(f.getDescription());
        loadSessions();
    }

    private void loadSessions() {
        try {
            colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateHeure().toString()));
            colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTypeSession()));
            colCoach.setCellValueFactory(cell -> new SimpleStringProperty("Coach ID: " + cell.getValue().getIdCoach()));
            
            List<Session> sessions = sessionService.getSessionsByFormation(formation.getIdFormation());
            sessionList.setAll(sessions);
            sessionTable.setItems(sessionList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleConfirmBooking() {
        Session selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise",
                    "Veuillez sélectionner une session dans le tableau pour vous inscrire.");
            return;
        }

        String prenom = userPrenomField.getText() != null ? userPrenomField.getText().trim() : "";
        String nom = userNameField.getText() != null ? userNameField.getText().trim() : "";
        String email = userEmailField.getText() != null ? userEmailField.getText().trim() : "";

        if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs requis",
                    "Veuillez remplir le prénom, le nom et l’e-mail pour vous inscrire.");
            return;
        }

        if (!EmailValidator.isValid(email)) {
            showAlert(Alert.AlertType.WARNING, "E-mail invalide", EmailValidator.reasonIfInvalid(email));
            return;
        }

        try {
            // Correct constructor: idBooking (0), idSession, idEleve (mock 1), statutPaiement
            Booking b = new Booking(0, selectedSession.getIdSession(), 1, "confirmé");
            bookingService.book(b);

            String fullName = prenom + " " + nom;
            String subject = "Confirmation d’inscription — " + formation.getTitre();
            String body = buildConfirmationBody(fullName, email, selectedSession);

            EmailService.SendResult mailResult = emailService.sendPlainText(email, subject, body);
            if (mailResult.sent) {
                showAlert(Alert.AlertType.INFORMATION, "Inscription confirmée",
                        "Bonjour " + fullName + ",\n\nVotre inscription à la session du "
                                + selectedSession.getDateHeure() + " est enregistrée.\n\n"
                                + "Un e-mail de confirmation vous a été envoyé à : " + email);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Inscription enregistrée",
                        "Bonjour " + fullName + ",\n\nVotre inscription à la session du "
                                + selectedSession.getDateHeure() + " est enregistrée.\n\n"
                                + "E-mail de confirmation : " + mailResult.message);
            }
            closeView();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de valider l'inscription : " + e.getMessage());
        }
    }

    private String buildConfirmationBody(String fullName, String email, Session selectedSession) {
        return "Bonjour " + fullName + ",\n\n"
                + "Nous confirmons votre inscription à la formation suivante :\n"
                + "• Formation : " + formation.getTitre() + "\n"
                + "• Jeu : " + formation.getJeu() + " — Niveau : " + formation.getNiveau() + "\n"
                + "• Session : " + selectedSession.getDateHeure() + "\n"
                + "• Type : " + selectedSession.getTypeSession() + "\n\n"
                + "Adresse enregistrée : " + email + "\n\n"
                + "Merci et à bientôt sur Game Pilot Academy.\n";
    }

    @FXML
    void handleCancel() {
        closeView();
    }

    private void closeView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/user/UserDashboard.fxml"));
            Stage stage = (Stage) titreLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
