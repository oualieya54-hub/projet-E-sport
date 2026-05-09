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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Model.Certification;
import org.example.Service.CertificationService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ProgressCenterController implements Initializable {

    @FXML private ListView<Certification> certListView;
    @FXML private VBox evaluationBox;
    @FXML private Label evaluationResultLabel;
    
    // Evaluation Questions Mock
    @FXML private Label questionLabel;
    @FXML private RadioButton opt1, opt2, opt3;
    private ToggleGroup optionsGroup;

    private final CertificationService certificationService = new CertificationService();
    private final ObservableList<Certification> certList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCertList();
        loadCertifications();
        setupEvaluation();
    }

    @FXML
    void handleRefresh() {
        loadCertifications();
    }

    private void setupCertList() {
        certListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Certification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-background-color: #1a2332; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #4caf50; -fx-border-width: 1;");
                    Label title = new Label("CERTIFICATION #" + item.getIdCertification());
                    title.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                    Label details = new Label("Formation ID: " + item.getIdFormation() + " | Score: " + item.getScoreFinal() + "%");
                    details.setStyle("-fx-text-fill: white;");
                    Label date = new Label("Obtenu le: " + item.getDateObtention());
                    date.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 11;");
                    
                    Button btnDownload = new Button("📥 Télécharger");
                    btnDownload.getStyleClass().add("btn-secondary");
                    btnDownload.setStyle("-fx-font-size: 10; -fx-padding: 5 10;");

                    card.getChildren().addAll(title, details, date, btnDownload);
                    setGraphic(card);
                }
            }
        });
        certListView.setItems(certList);
    }

    private void loadCertifications() {
        // Leave empty for now, will be populated after user integration
        certList.clear();
    }

    private void setupEvaluation() {
        optionsGroup = new ToggleGroup();
        opt1.setToggleGroup(optionsGroup);
        opt2.setToggleGroup(optionsGroup);
        opt3.setToggleGroup(optionsGroup);
        
        questionLabel.setText("Sélectionnez une formation terminée pour passer l'évaluation.");
        opt1.setText("---");
        opt2.setText("---");
        opt3.setText("---");
    }

    @FXML
    void handleSubmitEvaluation() {
        if (optionsGroup.getSelectedToggle() == null) {
            evaluationResultLabel.setText("Veuillez choisir une réponse.");
            evaluationResultLabel.setStyle("-fx-text-fill: #e91e63;");
            return;
        }
        
        evaluationResultLabel.setText("Félicitations ! Score: 95%. Votre certificat sera généré bientôt.");
        evaluationResultLabel.setStyle("-fx-text-fill: #4caf50;");
    }

    @FXML void navigateToDashboard(javafx.event.Event event) { switchScene("/user/UserDashboard.fxml", certListView); }
    @FXML void navigateToBooking(javafx.event.Event event) { switchScene("/user/BookingLiveSession.fxml", certListView); }
    @FXML void navigateToProgress(javafx.event.Event event) { /* Already here */ }

    private void switchScene(String fxmlPath, Node node) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) node.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
