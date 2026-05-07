package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Session;
import org.example.Service.SessionService;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SessionController {
    
    private final SessionService sessionService;

    public SessionController() {
        this.sessionService = new SessionService();
    }

    // --- Backend methods ---
    public void create(Session s) throws SQLException {
        sessionService.create(s);
    }

    public List<Session> getDisponibilites() throws SQLException {
        // Returning all sessions for the dashboard view instead of just available ones
        // If there's a getAll() in service, we should use it. For now, we use getDisponibilites().
        return sessionService.getDisponibilites();
    }

    public Session getById(int idSession) throws SQLException {
        return sessionService.getById(idSession);
    }

    public void update(Session s) throws SQLException {
        sessionService.update(s);
    }

    public void cancel(int idSession) throws SQLException {
        sessionService.cancel(idSession);
    }
    
    public void delete(int idSession) throws SQLException {
        // Typically, we might just cancel it, but assuming a delete logic if required
        // We will call cancel() here to represent the delete action since SessionService uses cancel()
        sessionService.cancel(idSession);
    }

    public List<Session> getSessionsByCoach(int idCoach) throws SQLException {
        return sessionService.getSessionsByCoach(idCoach);
    }

    public List<Session> getSessionsByJeu(String jeu) throws SQLException {
        return sessionService.getSessionsByJeu(jeu);
    }

    public List<Session> getSessionsFutures() throws SQLException {
        return sessionService.getSessionsFutures();
    }

    public boolean isCoachDisponible(int idCoach, LocalDateTime dateHeure) throws SQLException {
        return sessionService.isCoachDisponible(idCoach, dateHeure);
    }

    // --- JavaFX TableView ---
    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, Integer> colId;
    @FXML private TableColumn<Session, LocalDateTime> colDateHeure;
    @FXML private TableColumn<Session, String> colJeu;
    @FXML private TableColumn<Session, Float> colPrix;
    @FXML private TableColumn<Session, Integer> colDuree;
    @FXML private TableColumn<Session, Integer> colCapacite;
    @FXML private TableColumn<Session, String> colType;
    @FXML private TableColumn<Session, String> colStatut;

    // --- JavaFX Form Fields ---
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField jeuField;
    @FXML private TextField prixField;
    @FXML private TextField dureeField;
    @FXML private TextField capaciteField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextField idCoachField;
    @FXML private TextField idFormationField;

    // ObservableList for the TableView
    private final ObservableList<Session> sessionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Configure TableView columns
        colId.setCellValueFactory(new PropertyValueFactory<>("idSession"));
        colDateHeure.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        colJeu.setCellValueFactory(new PropertyValueFactory<>("jeu"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("dureeMinutes"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capaciteMax"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeSession"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        sessionTable.setItems(sessionList);

        // 2. Setup TableView selection listener to fill the form
        sessionTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showSessionDetails(newValue)
        );

        // 3. Load initial data
        loadData();
    }

    private void loadData() {
        try {
            List<Session> data = this.getDisponibilites();
            sessionList.setAll(data);
        } catch (SQLException e) {
            showAlert("Erreur de chargement", "Impossible de charger les sessions.", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        loadData();
    }

    private void showSessionDetails(Session session) {
        if (session != null) {
            if (session.getDateHeure() != null) {
                datePicker.setValue(session.getDateHeure().toLocalDate());
                timeField.setText(String.format("%02d:%02d", session.getDateHeure().getHour(), session.getDateHeure().getMinute()));
            }
            jeuField.setText(session.getJeu());
            prixField.setText(String.valueOf(session.getPrix()));
            dureeField.setText(String.valueOf(session.getDureeMinutes()));
            capaciteField.setText(String.valueOf(session.getCapaciteMax()));
            typeCombo.getSelectionModel().select(session.getTypeSession());
            statutCombo.getSelectionModel().select(session.getStatut());
            idCoachField.setText(String.valueOf(session.getIdCoach()));
            
            if (session.getIdFormation() != null && session.getIdFormation() > 0) {
                idFormationField.setText(String.valueOf(session.getIdFormation()));
            } else {
                idFormationField.setText("");
            }
        } else {
            handleReset(null);
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        if (validateInput()) {
            try {
                LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(timeField.getText()));
                
                Integer idFormation = null;
                if (!idFormationField.getText().trim().isEmpty()) {
                    idFormation = Integer.parseInt(idFormationField.getText());
                }

                Session newSession = new Session(
                        0, // ID auto-generated
                        dateTime,
                        jeuField.getText(),
                        Float.parseFloat(prixField.getText()),
                        Integer.parseInt(idCoachField.getText()),
                        Integer.parseInt(dureeField.getText()),
                        Integer.parseInt(capaciteField.getText()),
                        typeCombo.getValue(),
                        statutCombo.getValue(),
                        idFormation
                );

                this.create(newSession);
                loadData();
                handleReset(null);
                showAlert("Succès", "Session ajoutée", "La session a été ajoutée avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur BD", "Erreur lors de l'ajout", e.getMessage(), Alert.AlertType.ERROR);
            } catch (DateTimeParseException e) {
                showAlert("Erreur de format", "Heure invalide", "L'heure doit être au format HH:mm (ex: 14:30)", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une session", "Sélectionnez une session dans le tableau pour la modifier.", Alert.AlertType.WARNING);
            return;
        }

        if (validateInput()) {
            try {
                LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(timeField.getText()));
                
                Integer idFormation = null;
                if (!idFormationField.getText().trim().isEmpty()) {
                    idFormation = Integer.parseInt(idFormationField.getText());
                }

                selected.setDateHeure(dateTime);
                selected.setJeu(jeuField.getText());
                selected.setPrix(Float.parseFloat(prixField.getText()));
                selected.setIdCoach(Integer.parseInt(idCoachField.getText()));
                selected.setDureeMinutes(Integer.parseInt(dureeField.getText()));
                selected.setCapaciteMax(Integer.parseInt(capaciteField.getText()));
                selected.setTypeSession(typeCombo.getValue());
                selected.setStatut(statutCombo.getValue());
                selected.setIdFormation(idFormation);

                this.update(selected);
                loadData();
                showAlert("Succès", "Session modifiée", "La session a été mise à jour.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur BD", "Erreur lors de la modification", e.getMessage(), Alert.AlertType.ERROR);
            } catch (DateTimeParseException e) {
                showAlert("Erreur de format", "Heure invalide", "L'heure doit être au format HH:mm (ex: 14:30)", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment annuler/supprimer cette session ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();
            
            if (confirm.getResult() == ButtonType.YES) {
                try {
                    this.delete(selected.getIdSession());
                    loadData();
                    handleReset(null);
                    showAlert("Succès", "Session supprimée/annulée", "L'action a été effectuée.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Erreur BD", "Erreur lors de la suppression", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une session", "Sélectionnez une session dans le tableau pour la supprimer.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void handleReset(ActionEvent event) {
        sessionTable.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        timeField.clear();
        jeuField.clear();
        prixField.clear();
        dureeField.clear();
        capaciteField.clear();
        typeCombo.getSelectionModel().clearSelection();
        statutCombo.getSelectionModel().clearSelection();
        idCoachField.clear();
        idFormationField.clear();
    }

    private boolean validateInput() {
        if (datePicker.getValue() == null || timeField.getText().trim().isEmpty() ||
            jeuField.getText().trim().isEmpty() || prixField.getText().trim().isEmpty() ||
            dureeField.getText().trim().isEmpty() || capaciteField.getText().trim().isEmpty() ||
            typeCombo.getValue() == null || statutCombo.getValue() == null ||
            idCoachField.getText().trim().isEmpty()) {
            
            showAlert("Champs requis", "Informations manquantes", "Veuillez remplir tous les champs obligatoires (sauf ID Formation).", Alert.AlertType.WARNING);
            return false;
        }

        try {
            Float.parseFloat(prixField.getText());
            Integer.parseInt(dureeField.getText());
            Integer.parseInt(capaciteField.getText());
            Integer.parseInt(idCoachField.getText());
            if (!idFormationField.getText().trim().isEmpty()) {
                Integer.parseInt(idFormationField.getText());
            }
        } catch (NumberFormatException e) {
            showAlert("Format invalide", "Erreur de format", "Les champs Prix, Durée, Capacité et IDs doivent être numériques.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    @FXML
    void navigateToFormation(ActionEvent event) { switchScene(event, "/FormationView.fxml"); }

    @FXML
    void navigateToSession(ActionEvent event) { switchScene(event, "/SessionView.fxml"); }

    @FXML
    void navigateToBooking(ActionEvent event) { switchScene(event, "/BookingView.fxml"); }

    @FXML
    void navigateToEvaluation(ActionEvent event) { switchScene(event, "/EvaluationView.fxml"); }

    @FXML
    void navigateToCertification(ActionEvent event) { switchScene(event, "/CertificationView.fxml"); }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
