package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Formation;
import org.example.Service.FormationService;

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

public class FormationController {

    private final FormationService formationService;

    public FormationController() {
        this.formationService = new FormationService();
    }

    // --- Backend methods ---
    public void create(Formation f) throws SQLException {
        formationService.create(f);
    }

    public List<Formation> getAll() throws SQLException {
        return formationService.getAll();
    }

    public Formation getById(int id) throws SQLException {
        return formationService.getById(id);
    }

    public void update(Formation f) throws SQLException {
        formationService.update(f);
    }

    public void delete(int idFormation) throws SQLException {
        formationService.delete(idFormation);
    }

    public List<Formation> getActive() throws SQLException {
        return formationService.getActive();
    }

    public List<Formation> getByJeu(String jeu) throws SQLException {
        return formationService.getByJeu(jeu);
    }

    public List<Formation> getByCoach(int idCoach) throws SQLException {
        return formationService.getByCoach(idCoach);
    }

    public void activer(int idFormation) throws SQLException {
        formationService.activer(idFormation);
    }

    public void archiver(int idFormation) throws SQLException {
        formationService.archiver(idFormation);
    }

    // --- JavaFX Header / Filters ---
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterJeuCombo;
    @FXML private ComboBox<String> filterNiveauCombo;
    @FXML private ComboBox<String> filterStatutCombo;

    // --- JavaFX TableView ---
    @FXML private TableView<Formation> formationTable;
    @FXML private TableColumn<Formation, Integer> colId;
    @FXML private TableColumn<Formation, String> colTitre;
    @FXML private TableColumn<Formation, String> colJeu;
    @FXML private TableColumn<Formation, String> colNiveau;
    @FXML private TableColumn<Formation, Integer> colDuree;
    @FXML private TableColumn<Formation, Float> colPrix;
    @FXML private TableColumn<Formation, String> colStatut;
    @FXML private TableColumn<Formation, LocalDate> colDate;

    // --- JavaFX Form Fields ---
    @FXML private TextField titreField;
    @FXML private TextArea descArea;
    @FXML private ComboBox<String> jeuCombo;
    @FXML private ComboBox<String> niveauCombo;
    @FXML private TextField dureeField;
    @FXML private TextField prixField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private ComboBox<String> statutCombo;
    @FXML private TextField sessionsField;
    
    // ObservableList for the TableView
    private final ObservableList<Formation> formationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Configure TableView columns
        colId.setCellValueFactory(new PropertyValueFactory<>("idFormation"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colJeu.setCellValueFactory(new PropertyValueFactory<>("jeu"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("dureeSemaines"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));

        formationTable.setItems(formationList);

        // 2. Setup TableView selection listener to fill the form
        formationTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showFormationDetails(newValue)
        );

        // 3. Initialize ComboBox items (example items for 'jeu')
        ObservableList<String> jeux = FXCollections.observableArrayList("League of Legends", "Valorant", "Fortnite", "CS:GO", "Rocket League");
        jeuCombo.setItems(jeux);
        
        ObservableList<String> filterJeux = FXCollections.observableArrayList("Tous");
        filterJeux.addAll(jeux);
        filterJeuCombo.setItems(filterJeux);

        // Select "Tous" by default for filters
        filterJeuCombo.getSelectionModel().select("Tous");
        filterNiveauCombo.getSelectionModel().select("Tous");
        filterStatutCombo.getSelectionModel().select("Tous");

        // 4. Load initial data
        loadData();
    }

    private void loadData() {
        try {
            List<Formation> data = this.getAll();
            formationList.setAll(data);
        } catch (SQLException e) {
            showAlert("Erreur de chargement", "Impossible de charger les formations depuis la base de données.", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showFormationDetails(Formation formation) {
        if (formation != null) {
            titreField.setText(formation.getTitre());
            descArea.setText(formation.getDescription() != null ? formation.getDescription() : "");
            jeuCombo.getSelectionModel().select(formation.getJeu());
            niveauCombo.getSelectionModel().select(formation.getNiveau());
            dureeField.setText(String.valueOf(formation.getDureeSemaines()));
            prixField.setText(String.valueOf(formation.getPrix()));
            dateDebutPicker.setValue(formation.getDateDebut());
            statutCombo.getSelectionModel().select(formation.getStatut());
            sessionsField.setText(String.valueOf(formation.getNombreSessions()));
        } else {
            handleReset(null);
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        if (validateInput()) {
            try {
                // For simplicity, assuming idCoach is set to 1 for the current logged-in admin/coach
                int currentCoachId = 1; 

                Formation newFormation = new Formation(
                        0, // ID will be generated by DB
                        titreField.getText(),
                        descArea.getText(),
                        jeuCombo.getValue(),
                        niveauCombo.getValue(),
                        Integer.parseInt(dureeField.getText()),
                        Float.parseFloat(prixField.getText()),
                        currentCoachId,
                        dateDebutPicker.getValue(),
                        statutCombo.getValue() != null ? statutCombo.getValue() : "brouillon",
                        Integer.parseInt(sessionsField.getText())
                );

                this.create(newFormation);
                loadData();
                handleReset(null);
                showAlert("Succès", "Formation ajoutée", "La formation a été ajoutée avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur BD", "Erreur lors de l'ajout", e.getMessage(), Alert.AlertType.ERROR);
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Champs numériques invalides", "Veuillez vérifier la durée, le prix et le nombre de sessions.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        Formation selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une formation", "Sélectionnez une formation dans le tableau pour la modifier.", Alert.AlertType.WARNING);
            return;
        }

        if (validateInput()) {
            try {
                selected.setTitre(titreField.getText());
                selected.setDescription(descArea.getText());
                selected.setJeu(jeuCombo.getValue());
                selected.setNiveau(niveauCombo.getValue());
                selected.setDureeSemaines(Integer.parseInt(dureeField.getText()));
                selected.setPrix(Float.parseFloat(prixField.getText()));
                selected.setDateDebut(dateDebutPicker.getValue());
                selected.setStatut(statutCombo.getValue());
                selected.setNombreSessions(Integer.parseInt(sessionsField.getText()));

                this.update(selected);
                loadData(); // Refresh table
                showAlert("Succès", "Formation modifiée", "La formation a été mise à jour.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur BD", "Erreur lors de la modification", e.getMessage(), Alert.AlertType.ERROR);
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Champs numériques invalides", "Veuillez vérifier la durée, le prix et le nombre de sessions.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Formation selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette formation ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();
            
            if (confirm.getResult() == ButtonType.YES) {
                try {
                    this.delete(selected.getIdFormation());
                    loadData();
                    handleReset(null);
                    showAlert("Succès", "Formation supprimée", "La formation a été supprimée.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Erreur BD", "Erreur lors de la suppression", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une formation", "Sélectionnez une formation dans le tableau pour la supprimer.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void handleReset(ActionEvent event) {
        formationTable.getSelectionModel().clearSelection();
        titreField.clear();
        descArea.clear();
        jeuCombo.getSelectionModel().clearSelection();
        niveauCombo.getSelectionModel().clearSelection();
        dureeField.clear();
        prixField.clear();
        dateDebutPicker.setValue(null);
        statutCombo.getSelectionModel().clearSelection();
        sessionsField.clear();
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        loadData();
        searchField.clear();
        filterJeuCombo.getSelectionModel().select("Tous");
        filterNiveauCombo.getSelectionModel().select("Tous");
        filterStatutCombo.getSelectionModel().select("Tous");
    }

    @FXML
    void handleFilter() {
        String keyword = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String jeu = filterJeuCombo.getValue();
        String niveau = filterNiveauCombo.getValue();
        String statut = filterStatutCombo.getValue();

        try {
            // Get all from DB
            List<Formation> allFormations = this.getAll();

            // Filter locally (or you could create custom methods in the backend controller)
            List<Formation> filtered = allFormations.stream()
                .filter(f -> keyword.isEmpty() || f.getTitre().toLowerCase().contains(keyword))
                .filter(f -> "Tous".equals(jeu) || jeu == null || f.getJeu().equals(jeu))
                .filter(f -> "Tous".equals(niveau) || niveau == null || f.getNiveau().equals(niveau))
                .filter(f -> "Tous".equals(statut) || statut == null || f.getStatut().equals(statut))
                .collect(Collectors.toList());

            formationList.setAll(filtered);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du filtrage", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInput() {
        if (titreField.getText().trim().isEmpty() || jeuCombo.getValue() == null ||
            niveauCombo.getValue() == null || dureeField.getText().trim().isEmpty() ||
            prixField.getText().trim().isEmpty() || sessionsField.getText().trim().isEmpty()) {
            
            showAlert("Champs requis", "Informations manquantes", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.WARNING);
            return false;
        }

        try {
            Integer.parseInt(dureeField.getText());
            Integer.parseInt(sessionsField.getText());
            Float.parseFloat(prixField.getText());
        } catch (NumberFormatException e) {
            showAlert("Format invalide", "Erreur de format", "Durée, Prix et Nombre de sessions doivent être des nombres.", Alert.AlertType.WARNING);
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
