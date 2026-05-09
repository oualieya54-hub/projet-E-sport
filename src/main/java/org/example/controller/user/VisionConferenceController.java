package org.example.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.Model.Booking;
import org.example.Model.Session;
import org.example.Service.BookingService;
import org.example.Service.SessionService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class VisionConferenceController implements Initializable {

    @FXML private ListView<Session> sessionListView;
    @FXML private Label coachStatusLabel;
    @FXML private Label timerLabel;
    @FXML private VBox chatBox;
    @FXML private TextField chatInput;
    @FXML private ListView<String> participantsList;

    private final SessionService sessionService = new SessionService();
    private final BookingService bookingService = new BookingService();
    private final ObservableList<Session> mySessions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupListView();
        loadMySessions();
        setupMockParticipants();
    }

    private void setupListView() {
        sessionListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Session item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-background-color: #1a2332; -fx-padding: 10; -fx-background-radius: 5;");
                    Label title = new Label(item.getJeu().toUpperCase());
                    title.setStyle("-fx-text-fill: #00bcd4; -fx-font-weight: bold;");
                    Label details = new Label("Coach ID: " + item.getIdCoach() + " | " + item.getDureeMinutes() + " min");
                    details.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 11;");
                    Label date = new Label(item.getDateHeure().toString());
                    date.setStyle("-fx-text-fill: #ffffff;");
                    card.getChildren().addAll(title, details, date);
                    setGraphic(card);
                }
            }
        });
        sessionListView.setItems(mySessions);
    }

    private void loadMySessions() {
        try {
            // For demo purposes, we load all sessions. 
            // In a real app, we would filter by bookings of the current user.
            List<Session> all = sessionService.getDisponibilites();
            mySessions.setAll(all);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupMockParticipants() {
        participantsList.setItems(FXCollections.observableArrayList(
            "Coach Slim (Coach)",
            "Moi (Eleve)",
            "Ahmed Ben Ali",
            "Sonia Mansour"
        ));
    }

    @FXML
    void handleSendMessage() {
        String msg = chatInput.getText();
        if (msg != null && !msg.trim().isEmpty()) {
            Label label = new Label("Moi: " + msg);
            label.setStyle("-fx-text-fill: #ffffff; -fx-padding: 5; -fx-background-color: #311b92; -fx-background-radius: 5;");
            chatBox.getChildren().add(label);
            chatInput.clear();
        }
    }

    @FXML void handleJoinSession() {
        showAlert("Vision Conference", "Connexion en cours...", "Tentative de connexion au serveur de conférence e-sport.");
    }

    @FXML void handleLeaveSession() {
        showAlert("Vision Conference", "Session quittée", "Vous avez quitté la conférence.");
    }

    @FXML void handleCameraToggle() { System.out.println("Camera toggled"); }
    @FXML void handleMicToggle() { System.out.println("Mic toggled"); }
    @FXML void handleShareScreen() { System.out.println("Sharing screen"); }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void navigateBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AcademyMainView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
