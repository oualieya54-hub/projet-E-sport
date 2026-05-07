package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Evaluation;
import org.example.Service.EvaluationService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class EvaluationController {
    private final EvaluationService evaluationService;

    @FXML private TableView<Evaluation> evaluationTable;
    @FXML private TableColumn<Evaluation, Integer> colIdEvaluation;
    @FXML private TableColumn<Evaluation, Integer> colIdBooking;
    @FXML private TableColumn<Evaluation, Integer> colNote;
    @FXML private TableColumn<Evaluation, String> colCommentaire;
    @FXML private TableColumn<Evaluation, LocalDateTime> colDateEval;

    @FXML private TextField idBookingField;
    @FXML private TextField noteField;
    @FXML private TextArea commentaireArea;
    @FXML private DatePicker dateEvalPicker;

    private final ObservableList<Evaluation> evaluationList = FXCollections.observableArrayList();

    public EvaluationController() {
        this.evaluationService = new EvaluationService();
    }

    @FXML
    public void initialize() {
        colIdEvaluation.setCellValueFactory(new PropertyValueFactory<>("idEvaluation"));
        colIdBooking.setCellValueFactory(new PropertyValueFactory<>("idBooking"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colCommentaire.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        colDateEval.setCellValueFactory(new PropertyValueFactory<>("dateEval"));

        evaluationTable.setItems(evaluationList);

        evaluationTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEvaluationDetails(newValue)
        );

        loadData();
    }

    private void loadData() {
        try {
            List<Evaluation> data = evaluationService.getAll();
            evaluationList.setAll(data);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les évaluations", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showEvaluationDetails(Evaluation e) {
        if (e != null) {
            idBookingField.setText(String.valueOf(e.getIdBooking()));
            noteField.setText(String.valueOf(e.getNote()));
            commentaireArea.setText(e.getCommentaire());
            if (e.getDateEval() != null) {
                dateEvalPicker.setValue(e.getDateEval().toLocalDate());
            }
        } else {
            handleReset(null);
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        try {
            Evaluation e = new Evaluation(
                    Integer.parseInt(idBookingField.getText()),
                    Integer.parseInt(noteField.getText()),
                    commentaireArea.getText()
            );
            if (dateEvalPicker.getValue() != null) {
                e.setDateEval(dateEvalPicker.getValue().atTime(LocalTime.now()));
            }
            evaluationService.create(e);
            loadData();
            handleReset(null);
            showAlert("Succès", "Évaluation ajoutée", "L'évaluation a été enregistrée.", Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Erreur", "Saisie invalide", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        Evaluation selected = evaluationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setIdBooking(Integer.parseInt(idBookingField.getText()));
                selected.setNote(Integer.parseInt(noteField.getText()));
                selected.setCommentaire(commentaireArea.getText());
                if (dateEvalPicker.getValue() != null) {
                    selected.setDateEval(dateEvalPicker.getValue().atTime(LocalTime.now()));
                }
                evaluationService.update(selected);
                loadData();
                showAlert("Succès", "Évaluation mise à jour", null, Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Erreur", "Mise à jour impossible", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Evaluation selected = evaluationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                evaluationService.delete(selected.getIdEvaluation());
                loadData();
                handleReset(null);
                showAlert("Succès", "Évaluation supprimée", null, Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Erreur", "Suppression impossible", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleReset(ActionEvent event) {
        idBookingField.clear();
        noteField.clear();
        commentaireArea.clear();
        dateEvalPicker.setValue(null);
        evaluationTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Backend delegates ---
    public void create(Evaluation e) throws SQLException { evaluationService.create(e); }
    public List<Evaluation> getAll() throws SQLException { return evaluationService.getAll(); }
    public Evaluation getByBooking(int idBooking) throws SQLException { return evaluationService.getByBooking(idBooking); }
    public void update(Evaluation e) throws SQLException { evaluationService.update(e); }
    public void delete(int idEvaluation) throws SQLException { evaluationService.delete(idEvaluation); }
    public double getNoteMoyenneCoach(int idCoach) throws SQLException { return evaluationService.getNoteMoyenneCoach(idCoach); }
    public List<Evaluation> getByFormation(int idFormation) throws SQLException { return evaluationService.getByFormation(idFormation); }
    public boolean hasAlreadyEvaluated(int idBooking) throws SQLException { return evaluationService.hasAlreadyEvaluated(idBooking); }
    public void createSafe(Evaluation e) throws SQLException { evaluationService.createSafe(e); }
}
