package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Certification;
import org.example.Model.Formation;
import org.example.Service.CertificationService;
import org.example.Service.FormationService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CertificationController {
    private final CertificationService certificationService;

    @FXML private ListView<Certification> certificationListView;
    @FXML private TextField searchField;

    @FXML private ComboBox<String> eleveCombo;
    @FXML private ComboBox<Formation> formationCombo;
    @FXML private ComboBox<String> niveauCombo;
    @FXML private TextField scoreField;
    @FXML private DatePicker datePicker;

    private final ObservableList<Certification> certificationList = FXCollections.observableArrayList();

    private final FormationService formationService;

    public CertificationController() {
        this.certificationService = new CertificationService();
        this.formationService = new FormationService();
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
        
        // Populate Students (Dummy)
        eleveCombo.setItems(FXCollections.observableArrayList("Ahmed Ben Ali", "Sonia Mansour", "Firas Gharbi", "Yasmine Trabelsi"));

        // Populate Formations
        try {
            List<Formation> formations = formationService.getAll();
            formationCombo.setItems(FXCollections.observableArrayList(formations));
            formationCombo.setConverter(new StringConverter<Formation>() {
                @Override public String toString(Formation f) { return f == null ? "" : f.getTitre(); }
                @Override public Formation fromString(String string) { return null; }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
            // Student mapping (dummy)
            int eleveId = c.getIdEleve();
            if (eleveId == 42) eleveCombo.getSelectionModel().select("Ahmed Ben Ali");
            else if (eleveId == 43) eleveCombo.getSelectionModel().select("Sonia Mansour");
            else if (eleveId == 44) eleveCombo.getSelectionModel().select("Firas Gharbi");
            else eleveCombo.getSelectionModel().select("Yasmine Trabelsi");

            // Formation selection
            for (Formation f : formationCombo.getItems()) {
                if (f.getIdFormation() == c.getIdFormation()) {
                    formationCombo.getSelectionModel().select(f);
                    break;
                }
            }
            
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
            // Student mapping
            String eleveName = eleveCombo.getValue();
            int eleveId = 42; 
            if ("Sonia Mansour".equals(eleveName)) eleveId = 43;
            else if ("Firas Gharbi".equals(eleveName)) eleveId = 44;
            else if ("Yasmine Trabelsi".equals(eleveName)) eleveId = 45;

            Formation selectedForm = formationCombo.getValue();

            Certification c = new Certification(
                    eleveId,
                    selectedForm != null ? selectedForm.getIdFormation() : 0,
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
                // Student mapping
                String eleveName = eleveCombo.getValue();
                int eleveId = 42; 
                if ("Sonia Mansour".equals(eleveName)) eleveId = 43;
                else if ("Firas Gharbi".equals(eleveName)) eleveId = 44;
                else if ("Yasmine Trabelsi".equals(eleveName)) eleveId = 45;

                Formation selectedForm = formationCombo.getValue();

                selected.setIdEleve(eleveId);
                selected.setIdFormation(selectedForm != null ? selectedForm.getIdFormation() : 0);
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
        eleveCombo.getSelectionModel().clearSelection();
        formationCombo.getSelectionModel().clearSelection();
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
