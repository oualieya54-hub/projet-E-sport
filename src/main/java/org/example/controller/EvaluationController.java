package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Evaluation;
import org.example.Model.Booking;
import org.example.Service.EvaluationService;
import org.example.Service.BookingService;
import javafx.util.StringConverter;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class
EvaluationController {
    private final EvaluationService evaluationService;

    @FXML private ListView<Evaluation> evaluationListView;
    @FXML private TextField searchField;

    @FXML private ComboBox<Booking> bookingCombo;
    @FXML private TextField noteField;
    @FXML private TextArea commentaireArea;
    @FXML private DatePicker dateEvalPicker;

    private final ObservableList<Evaluation> evaluationList = FXCollections.observableArrayList();

    private final BookingService bookingService;

    public EvaluationController() {
        this.evaluationService = new EvaluationService();
        this.bookingService = new BookingService();
    }

    @FXML
    public void initialize() {
        // 1. Configure ListView CellFactory
        evaluationListView.setCellFactory(param -> new ListCell<Evaluation>() {
            @Override
            protected void updateItem(Evaluation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    VBox card = new VBox(5);
                    card.getStyleClass().add("list-card");

                    HBox header = new HBox(10);
                    Label title = new Label("Évaluation #" + item.getIdEvaluation());
                    title.getStyleClass().add("item-title");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Label note = new Label(item.getNote() + "/100");
                    note.getStyleClass().add("item-title");
                    note.setStyle("-fx-text-fill: #e91e63;");

                    header.getChildren().addAll(title, spacer, note);

                    Label detail = new Label("Réservation: " + item.getIdBooking() + " | Date: " + (item.getDateEval() != null ? item.getDateEval().toLocalDate() : "N/A"));
                    detail.getStyleClass().add("item-detail");

                    Label comment = new Label(item.getCommentaire());
                    comment.getStyleClass().add("item-detail");
                    comment.setStyle("-fx-font-style: italic; -fx-opacity: 0.8;");

                    card.getChildren().addAll(header, detail, comment);
                    setGraphic(card);
                }
            }
        });

        evaluationListView.setItems(evaluationList);

        // 2. Setup ListView selection listener to fill the form
        evaluationListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEvaluationDetails(newValue)
        );

        // 3. Initialize ComboBox items
        try {
            List<Booking> bookings = bookingService.getAll();
            bookingCombo.setItems(FXCollections.observableArrayList(bookings));
            bookingCombo.setConverter(new StringConverter<Booking>() {
                @Override public String toString(Booking b) { return b == null ? "" : "Réserv. #" + b.getIdBooking() + " (Session " + b.getIdSession() + ")"; }
                @Override public Booking fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 4. Load data
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
            // Selection Booking
            for (Booking b : bookingCombo.getItems()) {
                if (b.getIdBooking() == e.getIdBooking()) {
                    bookingCombo.getSelectionModel().select(b);
                    break;
                }
            }
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
        if (validateInput()) {
            try {
                Booking selectedBooking = bookingCombo.getValue();
                Evaluation e = new Evaluation(
                        selectedBooking.getIdBooking(),
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
            } catch (SQLException ex) {
                showAlert("Erreur Base de Données", "Impossible d'ajouter l'évaluation", ex.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception ex) {
                showAlert("Erreur", "Une erreur inattendue est survenue", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        Evaluation selected = evaluationListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une évaluation", "Sélectionnez un élément dans la liste.", Alert.AlertType.WARNING);
            return;
        }

        if (validateInput()) {
            try {
                Booking selectedBooking = bookingCombo.getValue();
                selected.setIdBooking(selectedBooking.getIdBooking());
                selected.setNote(Integer.parseInt(noteField.getText()));
                selected.setCommentaire(commentaireArea.getText());
                if (dateEvalPicker.getValue() != null) {
                    selected.setDateEval(dateEvalPicker.getValue().atTime(LocalTime.now()));
                }
                evaluationService.update(selected);
                loadData();
                showAlert("Succès", "Évaluation mise à jour", null, Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Erreur Base de Données", "Impossible de modifier l'évaluation", ex.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception ex) {
                showAlert("Erreur", "Une erreur inattendue est survenue", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Evaluation selected = evaluationListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                evaluationService.delete(selected.getIdEvaluation());
                loadData();
                handleReset(null);
                showAlert("Succès", "Évaluation supprimée", null, Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                showAlert("Erreur Base de Données", "Suppression impossible", msg, Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        if (bookingCombo.getValue() == null || noteField.getText().trim().isEmpty() ||
            commentaireArea.getText().trim().isEmpty() || dateEvalPicker.getValue() == null) {
            
            showAlert("Champs requis", "Informations manquantes", "Veuillez remplir tous les champs (Réservation, Note, Commentaire, Date).", Alert.AlertType.WARNING);
            return false;
        }

        try {
            int note = Integer.parseInt(noteField.getText());
            if (note < 0 || note > 100) {
                showAlert("Note invalide", "Valeur hors limite", "La note doit être un nombre entre 0 et 100 (ex: 85).", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Format invalide", "Vérifiez la note", "Le champ Note doit contenir uniquement des chiffres (ex: 90).", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    @FXML
    void handleReset(ActionEvent event) {
        bookingCombo.getSelectionModel().clearSelection();
        noteField.clear();
        commentaireArea.clear();
        dateEvalPicker.setValue(null);
        evaluationListView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleFilter() {
        String keyword = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        try {
            List<Evaluation> all = evaluationService.getAll();
            List<Evaluation> filtered = all.stream()
                .filter(e -> keyword.isEmpty() || e.getCommentaire().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
            evaluationList.setAll(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void navigateToDashboard(ActionEvent event) { switchScene(event, "/admin/AcademyMainView.fxml"); }

    @FXML
    void navigateToFormation(ActionEvent event) { switchScene(event, "/admin/FormationView.fxml"); }

    @FXML
    void navigateToSession(ActionEvent event) { switchScene(event, "/admin/SessionView.fxml"); }

    @FXML
    void navigateToBooking(ActionEvent event) { switchScene(event, "/admin/BookingView.fxml"); }

    @FXML
    void navigateToEvaluation(ActionEvent event) { switchScene(event, "/admin/EvaluationView.fxml"); }

    @FXML
    void navigateToCertification(ActionEvent event) { switchScene(event, "/admin/CertificationView.fxml"); }

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
