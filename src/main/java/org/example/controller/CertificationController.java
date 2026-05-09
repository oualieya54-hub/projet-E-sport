package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Certification;
import org.example.Service.CertificationService;

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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CertificationController {
    private final CertificationService certificationService;

    @FXML private ListView<Certification> certificationListView;
    @FXML private TextField searchField;

    @FXML private TextField idEleveField;
    @FXML private TextField idFormationField;
    @FXML private ComboBox<String> niveauCombo;
    @FXML private TextField scoreField;
    @FXML private DatePicker datePicker;

    private final ObservableList<Certification> certificationList = FXCollections.observableArrayList();

    public CertificationController() {
        this.certificationService = new CertificationService();
    }

    @FXML
    public void initialize() {
        // 1. Configure ListView CellFactory
        certificationListView.setCellFactory(param -> new ListCell<Certification>() {
            @Override
            protected void updateItem(Certification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    VBox card = new VBox(5);
                    card.getStyleClass().add("list-card");

                    HBox header = new HBox(10);
                    Label title = new Label("Certificat #" + item.getIdCertification());
                    title.getStyleClass().add("item-title");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Label score = new Label(item.getScoreFinal() + "%");
                    score.getStyleClass().add("item-title");
                    score.setStyle("-fx-text-fill: #e91e63;");

                    header.getChildren().addAll(title, spacer, score);

                    Label detail = new Label("Élève: " + item.getIdEleve() + " | Formation: " + item.getIdFormation() + " | " + (item.getDateObtention() != null ? item.getDateObtention() : "N/A"));
                    detail.getStyleClass().add("item-detail");

                    HBox footer = new HBox(10);
                    Label badge = new Label(item.getNiveauObtenu() != null ? item.getNiveauObtenu().toUpperCase() : "N/A");
                    badge.getStyleClass().add("badge");
                    if ("PRO".equalsIgnoreCase(item.getNiveauObtenu())) badge.getStyleClass().add("badge-active");
                    else if ("GOLD".equalsIgnoreCase(item.getNiveauObtenu())) badge.getStyleClass().add("badge-warning");
                    else badge.getStyleClass().add("badge-secondary");

                    footer.getChildren().add(badge);

                    card.getChildren().addAll(header, detail, footer);
                    setGraphic(card);
                }
            }
        });

        certificationListView.setItems(certificationList);

        // 2. Setup ListView selection listener to fill the form
        certificationListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCertificationDetails(newValue)
        );

        // 3. Initialize ComboBox items
        niveauCombo.setItems(FXCollections.observableArrayList("Bronze", "Silver", "Gold", "Pro"));

        // 4. Load data
        loadData();
    }

    private void loadData() {
        try {
            List<Certification> data = certificationService.getAll();
            certificationList.setAll(data);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les certifications", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showCertificationDetails(Certification c) {
        if (c != null) {
            idEleveField.setText(String.valueOf(c.getIdEleve()));
            idFormationField.setText(String.valueOf(c.getIdFormation()));
            niveauCombo.getSelectionModel().select(c.getNiveauObtenu());
            scoreField.setText(String.valueOf(c.getScoreFinal()));
            datePicker.setValue(c.getDateObtention());
        } else {
            handleReset(null);
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        try {
            Certification c = new Certification(
                    Integer.parseInt(idEleveField.getText()),
                    Integer.parseInt(idFormationField.getText()),
                    niveauCombo.getValue(),
                    Float.parseFloat(scoreField.getText())
            );
            if (datePicker.getValue() != null) {
                c.setDateObtention(datePicker.getValue());
            }
            certificationService.create(c);
            loadData();
            handleReset(null);
            showAlert("Succès", "Certification ajoutée", null, Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Erreur", "Saisie invalide", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        Certification selected = certificationListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setIdEleve(Integer.parseInt(idEleveField.getText()));
                selected.setIdFormation(Integer.parseInt(idFormationField.getText()));
                selected.setNiveauObtenu(niveauCombo.getValue());
                selected.setScoreFinal(Float.parseFloat(scoreField.getText()));
                selected.setDateObtention(datePicker.getValue());
                
                certificationService.update(selected);
                loadData();
                showAlert("Succès", "Certification mise à jour", null, Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Erreur", "Mise à jour impossible", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Certification selected = certificationListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                certificationService.delete(selected.getIdCertification());
                loadData();
                handleReset(null);
                showAlert("Succès", "Certification supprimée", null, Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Erreur", "Suppression impossible", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleReset(ActionEvent event) {
        idEleveField.clear();
        idFormationField.clear();
        niveauCombo.getSelectionModel().clearSelection();
        scoreField.clear();
        datePicker.setValue(null);
        certificationListView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleFilter() {
        String keyword = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        try {
            List<Certification> all = certificationService.getAll();
            List<Certification> filtered = all.stream()
                .filter(c -> keyword.isEmpty() || String.valueOf(c.getIdCertification()).contains(keyword) || (c.getNiveauObtenu() != null && c.getNiveauObtenu().toLowerCase().contains(keyword)))
                .collect(Collectors.toList());
            certificationList.setAll(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void navigateToDashboard(ActionEvent event) { switchScene(event, "/AcademyMainView.fxml"); }

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

    // --- Backend delegates ---
    public void create(Certification c) throws SQLException { certificationService.create(c); }
    public List<Certification> getAll() throws SQLException { return certificationService.getAll(); }
    public List<Certification> getByEleve(int idEleve) throws SQLException { return certificationService.getByEleve(idEleve); }
    public List<Certification> getByFormation(int idFormation) throws SQLException { return certificationService.getByFormation(idFormation); }
    public void update(Certification c) throws SQLException { certificationService.update(c); }
    public void delete(int idCertification) throws SQLException { certificationService.delete(idCertification); }
    public boolean hasAlreadyCertified(int idEleve, int idFormation) throws SQLException { return certificationService.hasAlreadyCertified(idEleve, idFormation); }
    public void createSafe(Certification c) throws SQLException { certificationService.createSafe(c); }
    public String calculerNiveau(float score) { return certificationService.calculerNiveau(score); }
    public List<Certification> getByNiveau(String niveau) throws SQLException { return certificationService.getByNiveau(niveau); }
}
