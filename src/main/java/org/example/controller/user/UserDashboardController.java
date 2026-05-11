package org.example.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Model.Formation;
import org.example.Service.FormationService;
import org.example.Utils.DbErrorMapper;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserDashboardController implements Initializable {

    @FXML private FlowPane formationsFlowPane;
    @FXML private TextField searchField;
    @FXML private Label totalBookingsLabel;
    @FXML private Label completedFormationsLabel;
    @FXML private Label certificationsLabel;

    private final FormationService formationService = new FormationService();
    private final ObservableList<Formation> allFormations = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadFormations();
        updateStats();
    }

    private void loadFormations() {
        try {
            List<Formation> list = formationService.getAll();
            allFormations.setAll(list);
            displayFormations(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", DbErrorMapper.toUserMessage(e));
        }
    }

    private void displayFormations(List<Formation> list) {
        formationsFlowPane.getChildren().clear();
        for (Formation f : list) {
            formationsFlowPane.getChildren().add(createFormationCard(f));
        }
    }

    private VBox createFormationCard(Formation f) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(280);
        card.setMinHeight(250);
        card.setStyle("-fx-background-color: #1a2332; -fx-padding: 20; -fx-background-radius: 15; -fx-border-color: #00bcd4; -fx-border-width: 1;");

        Label title = new Label(f.getTitre());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        title.setWrapText(true);

        Label jeu = new Label("🎮 " + f.getJeu().toUpperCase());
        jeu.setStyle("-fx-text-fill: #00bcd4; -fx-font-size: 12;");

        Label price = new Label(f.getPrix() + " TND");
        price.setStyle("-fx-text-fill: #e91e63; -fx-font-size: 16; -fx-font-weight: bold;");

        // Extra details (Hidden by default)
        VBox extraInfo = new VBox(10);
        extraInfo.setVisible(false);
        extraInfo.setManaged(false);
        
        Label level = new Label("⭐ Niveau: " + f.getNiveau());
        level.setStyle("-fx-text-fill: #9c27b0;");
        
        Label duree = new Label("⏳ Durée: " + f.getDureeSemaines() + " semaines");
        duree.setStyle("-fx-text-fill: #b0b0b0;");

        Label fullDesc = new Label(f.getDescription());
        fullDesc.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 11;");
        fullDesc.setWrapText(true);
        
        extraInfo.getChildren().addAll(level, duree, fullDesc);

        Button btnDetails = new Button("Détails");
        btnDetails.getStyleClass().add("btn-secondary");
        btnDetails.setMaxWidth(Double.MAX_VALUE);
        btnDetails.setOnAction(e -> {
            boolean isVisible = !extraInfo.isVisible();
            extraInfo.setVisible(isVisible);
            extraInfo.setManaged(isVisible);
            btnDetails.setText(isVisible ? "Réduire" : "Détails");
        });

        Button btnBook = new Button("S'inscrire");
        btnBook.getStyleClass().add("btn-primary");
        btnBook.setMaxWidth(Double.MAX_VALUE);
        btnBook.setOnAction(e -> showFormationDetails(f));

        card.getChildren().addAll(title, jeu, price, extraInfo, btnDetails, btnBook);
        return card;
    }

    private void showFormationDetails(Formation f) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/FormationDetailsView.fxml"));
            Parent root = loader.load();
            
            FormationDetailsController controller = loader.getController();
            controller.setFormation(f);
            
            Stage stage = (Stage) formationsFlowPane.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStats() {
        totalBookingsLabel.setText("0");
        completedFormationsLabel.setText("0");
        certificationsLabel.setText("0");
    }

    @FXML
    void handleSearch() {
        String query = searchField.getText().toLowerCase();
        List<Formation> filtered = allFormations.stream()
                .filter(f -> f.getTitre().toLowerCase().contains(query) || f.getJeu().toLowerCase().contains(query))
                .collect(Collectors.toList());
        displayFormations(filtered);
    }

    @FXML
    void navigateToDashboard(Event event) { switchScene("/user/UserDashboard.fxml", formationsFlowPane); }

    @FXML
    void navigateToBooking(Event event) { switchScene("/user/BookingLiveSession.fxml", formationsFlowPane); }

    @FXML
    void navigateToProgress(Event event) { switchScene("/user/ProgressCenter.fxml", formationsFlowPane); }

    private void switchScene(String fxmlPath, Node node) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page demandée.");
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
