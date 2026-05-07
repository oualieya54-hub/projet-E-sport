package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Certification;
import org.example.Service.CertificationService;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CertificationController {
    private final CertificationService certificationService;

    @FXML private TableView<Certification> certificationTable;
    @FXML private TableColumn<Certification, Integer> colIdCertification;
    @FXML private TableColumn<Certification, Integer> colIdEleve;
    @FXML private TableColumn<Certification, Integer> colIdFormation;
    @FXML private TableColumn<Certification, String> colNiveauObtenu;
    @FXML private TableColumn<Certification, Float> colScoreFinal;
    @FXML private TableColumn<Certification, LocalDate> colDateObtention;

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
        colIdCertification.setCellValueFactory(new PropertyValueFactory<>("idCertification"));
        colIdEleve.setCellValueFactory(new PropertyValueFactory<>("idEleve"));
        colIdFormation.setCellValueFactory(new PropertyValueFactory<>("idFormation"));
        colNiveauObtenu.setCellValueFactory(new PropertyValueFactory<>("niveauObtenu"));
        colScoreFinal.setCellValueFactory(new PropertyValueFactory<>("scoreFinal"));
        colDateObtention.setCellValueFactory(new PropertyValueFactory<>("dateObtention"));

        certificationTable.setItems(certificationList);

        certificationTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCertificationDetails(newValue)
        );

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
        Certification selected = certificationTable.getSelectionModel().getSelectedItem();
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
        Certification selected = certificationTable.getSelectionModel().getSelectedItem();
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
        certificationTable.getSelectionModel().clearSelection();
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
