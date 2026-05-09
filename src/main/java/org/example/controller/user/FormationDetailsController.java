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
import org.example.Service.SessionService;

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

    @FXML private TextField userNameField;
    @FXML private TextField userEmailField;

    private Formation formation;
    private final SessionService sessionService = new SessionService();
    private final BookingService bookingService = new BookingService();
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
            showAlert("Sélection requise", "Veuillez sélectionner une session dans le tableau pour vous inscrire.");
            return;
        }

        if (userNameField.getText().isEmpty() || userEmailField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir vos informations pour vous inscrire.");
            return;
        }

        try {
            // Correct constructor: idBooking (0), idSession, idEleve (mock 1), statutPaiement
            Booking b = new Booking(0, selectedSession.getIdSession(), 1, "confirmé");
            bookingService.book(b);
            showAlert("Succès", "Inscription réussie à la session du " + selectedSession.getDateHeure());
            closeView();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de valider l'inscription : " + e.getMessage());
        }
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

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
