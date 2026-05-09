package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Formation;
import org.example.Service.FormationService;

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

    // --- JavaFX ListView ---
    @FXML private ListView<Formation> formationListView;

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
        // 1. Configure ListView CellFactory
        formationListView.setCellFactory(param -> new ListCell<Formation>() {
            @Override
            protected void updateItem(Formation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Create Card Layout
                    VBox card = new VBox(5);
                    card.getStyleClass().add("list-card");

                    HBox header = new HBox(10);
                    Label title = new Label(item.getTitre());
                    title.getStyleClass().add("item-title");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Label price = new Label(String.format("%.2f TND", item.getPrix()));
                    price.getStyleClass().add("item-title");
                    price.setStyle("-fx-text-fill: #e91e63;");

                    header.getChildren().addAll(title, spacer, price);

                    Label detail = new Label(item.getJeu() + " | " + item.getNiveau() + " | " + item.getDureeSemaines() + "h");
                    detail.getStyleClass().add("item-detail");

                    HBox footer = new HBox(10);
                    Label badge = new Label(item.getStatut().toUpperCase());
                    badge.getStyleClass().add("badge");
                    if ("active".equalsIgnoreCase(item.getStatut())) badge.getStyleClass().add("badge-active");
                    else if ("archivée".equalsIgnoreCase(item.getStatut())) badge.getStyleClass().add("badge-danger");
                    else badge.getStyleClass().add("badge-warning");

                    footer.getChildren().add(badge);

                    card.getChildren().addAll(header, detail, footer);
                    setGraphic(card);
                }
            }
        });

        formationListView.setItems(formationList);

        // 2. Setup ListView selection listener to fill the form
        formationListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showFormationDetails(newValue)
        );

        // 3. Initialize ComboBox items
        ObservableList<String> jeux = FXCollections.observableArrayList("League of Legends", "Valorant", "Fortnite", "CS:GO", "Rocket League");
        jeuCombo.setItems(jeux);
        
        ObservableList<String> levels = FXCollections.observableArrayList("débutant", "intermédiaire", "avancé");
        niveauCombo.setItems(levels);

        ObservableList<String> statusList = FXCollections.observableArrayList("brouillon", "active", "archivée");
        statutCombo.setItems(statusList);

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
                        0 // Default to 0 sessions for new formation
                );

                this.create(newFormation);
                loadData();
                handleReset(null);
                showAlert("Succès", "Formation ajoutée", "La formation a été ajoutée avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                String msg = e.getMessage();
                if (msg.contains("Data truncated for column 'statut'")) {
                    msg = "Le statut choisi est trop long ou non supporté par la base de données.";
                } else if (msg.contains("Duplicate entry")) {
                    msg = "Une formation avec ce titre existe déjà.";
                }
                showAlert("Erreur Base de Données", "Impossible d'ajouter la formation", msg, Alert.AlertType.ERROR);
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Champs numériques invalides", "Veuillez vérifier la durée, le prix et le nombre de sessions.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        Formation selected = formationListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une formation", "Sélectionnez une formation dans la liste pour la modifier.", Alert.AlertType.WARNING);
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
                // nombreSessions is usually read-only or updated via sessions module

                this.update(selected);
                loadData(); // Refresh table
                showAlert("Succès", "Formation modifiée", "La formation a été mise à jour.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                String msg = e.getMessage();
                if (msg.contains("Data truncated for column 'statut'")) {
                    msg = "Le statut choisi est trop long ou non supporté par la base de données.";
                }
                showAlert("Erreur Base de Données", "Erreur lors de la modification", msg, Alert.AlertType.ERROR);
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Champs numériques invalides", "Veuillez vérifier la durée, le prix et le nombre de sessions.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Formation selected = formationListView.getSelectionModel().getSelectedItem();
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
        titreField.clear();
        descArea.clear();
        jeuCombo.getSelectionModel().clearSelection();
        niveauCombo.getSelectionModel().clearSelection();
        dureeField.clear();
        prixField.clear();
        dateDebutPicker.setValue(null);
        statutCombo.getSelectionModel().clearSelection();
        sessionsField.setText("0");
        formationListView.getSelectionModel().clearSelection();
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
            prixField.getText().trim().isEmpty() || dateDebutPicker.getValue() == null) {
            
            showAlert("Champs requis", "Informations manquantes", "Veuillez remplir tous les champs obligatoires (Titre, Jeu, Niveau, Prix, Durée, Date).", Alert.AlertType.WARNING);
            return false;
        }

        try {
            Integer.parseInt(dureeField.getText());
            float price = Float.parseFloat(prixField.getText());
            if (price < 0 || price > 10000) {
                showAlert("Prix invalide", "Le prix est illogique", "Le prix doit être compris entre 0 et 10,000 TND (ex: 450.0).", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Format invalide", "Vérifiez vos saisies", "La Durée (ex: 20) et le Prix (ex: 300.0) doivent contenir uniquement des chiffres.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
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
}
