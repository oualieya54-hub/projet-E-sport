package org.example.controller.user;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.Model.Session;
import org.example.Service.SessionService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class BookingLiveSessionController implements Initializable {

    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, String> colJeu;
    @FXML private TableColumn<Session, String> colCoach;
    @FXML private TableColumn<Session, String> colDate;
    @FXML private TableColumn<Session, String> colStatut;

    @FXML private Label timerLabel;
    @FXML private Label sessionStatusBadge;
    @FXML private Label coachStatusLabel;
    @FXML private VBox chatBox;
    @FXML private TextField chatInput;
    @FXML private ListView<String> participantsList;

    private final SessionService sessionService = new SessionService();
    private final ObservableList<Session> sessions = FXCollections.observableArrayList();
    private Timeline timeline;
    private int secondsElapsed = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        loadSessions();
        setupMockParticipants();
    }

    private void setupTable() {
        colJeu.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getJeu()));
        colCoach.setCellValueFactory(cell -> new SimpleStringProperty("Coach #" + cell.getValue().getIdCoach()));
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateHeure().toString()));
        colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));
        sessionTable.setItems(sessions);
    }

    private void loadSessions() {
        try {
            List<Session> all = sessionService.getDisponibilites();
            sessions.setAll(all);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupMockParticipants() {
        participantsList.setItems(FXCollections.observableArrayList());
    }

    @FXML
    void handleJoinSession() {
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez choisir une session pour rejoindre le live.");
            return;
        }
        startTimer();
        sessionStatusBadge.setText("SESSION ACTIVE");
        sessionStatusBadge.setStyle("-fx-background-color: #e91e63; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 5;");
        coachStatusLabel.setText("● COACH ONLINE");
        coachStatusLabel.setStyle("-fx-text-fill: #00ff00;");
    }

    @FXML
    void handleLeaveSession() {
        stopTimer();
        sessionStatusBadge.setText("SESSION INACTIVE");
        sessionStatusBadge.setStyle("-fx-background-color: #333d4d; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 5;");
        coachStatusLabel.setText("COACH OFFLINE");
        coachStatusLabel.setStyle("-fx-text-fill: #b0b0b0;");
    }

    @FXML
    void handleSendMessage() {
        String msg = chatInput.getText();
        if (msg != null && !msg.trim().isEmpty()) {
            Label label = new Label("Moi: " + msg);
            label.setStyle("-fx-text-fill: white; -fx-padding: 5; -fx-background-color: #311b92; -fx-background-radius: 5;");
            chatBox.getChildren().add(label);
            chatInput.clear();
        }
    }

    private void startTimer() {
        if (timeline != null) timeline.stop();
        secondsElapsed = 0;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            int h = secondsElapsed / 3600;
            int m = (secondsElapsed % 3600) / 60;
            int s = secondsElapsed % 60;
            timerLabel.setText(String.format("%02d:%02d:%02d", h, m, s));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) timeline.stop();
        timerLabel.setText("00:00:00");
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML void handleCameraToggle() { System.out.println("Camera toggled"); }
    @FXML void handleMicToggle() { System.out.println("Mic toggled"); }
    @FXML void handleShareScreen() { System.out.println("Sharing screen"); }

    @FXML void navigateToDashboard(Event event) { switchScene("/user/UserDashboard.fxml", sessionTable); }
    @FXML void navigateToBooking(Event event) { /* Already here */ }
    @FXML void navigateToProgress(Event event) { switchScene("/user/ProgressCenter.fxml", sessionTable); }

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
