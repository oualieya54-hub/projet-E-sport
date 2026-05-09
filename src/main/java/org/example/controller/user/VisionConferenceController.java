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
import javafx.scene.input.MouseEvent;
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

public class VisionConferenceController implements Initializable {

    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, String> colJeu;
    @FXML private TableColumn<Session, String> colCoach;
    @FXML private TableColumn<Session, String> colDate;
    @FXML private TableColumn<Session, String> colStatut;

    @FXML private Label sessionTitleLabel;
    @FXML private Label activeCoachLabel;
    @FXML private Label timerLabel;
    @FXML private Label sessionStatusBadge;
    @FXML private Label coachStatusLabel;

    @FXML private ListView<String> chatListView;
    @FXML private TextField chatInput;

    private final SessionService sessionService = new SessionService();
    private final ObservableList<Session> sessions = FXCollections.observableArrayList();
    private final ObservableList<String> chatMessages = FXCollections.observableArrayList();

    private Timeline timeline;
    private int secondsElapsed = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        loadSessions();
        chatListView.setItems(chatMessages);
        
        // Listen for table selection
        sessionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sessionTitleLabel.setText(newVal.getJeu().toUpperCase());
                activeCoachLabel.setText("Coach ID: " + newVal.getIdCoach());
            }
        });
    }

    private void setupTable() {
        colJeu.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getJeu()));
        colCoach.setCellValueFactory(cell -> new SimpleStringProperty("ID: " + cell.getValue().getIdCoach()));
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

    @FXML
    void handleJoinSession(ActionEvent event) {
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Action requise", "Veuillez sélectionner une session dans la liste à gauche.");
            return;
        }

        startTimer();
        sessionStatusBadge.setText("SESSION ACTIVE");
        sessionStatusBadge.setStyle("-fx-background-color: #e91e63; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5;");
        coachStatusLabel.setText("● COACH ONLINE");
        coachStatusLabel.setStyle("-fx-text-fill: #00ff00;");
        
        chatMessages.add("Système: Vous avez rejoint la session de " + selected.getJeu());
    }

    @FXML
    void handleLeaveSession(ActionEvent event) {
        stopTimer();
        sessionStatusBadge.setText("SESSION INACTIVE");
        sessionStatusBadge.setStyle("-fx-background-color: #333d4d; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 5;");
        coachStatusLabel.setText("COACH OFFLINE");
        coachStatusLabel.setStyle("-fx-text-fill: #b0b0b0;");
        chatMessages.add("Système: Vous avez quitté la session.");
    }

    @FXML
    void handleSendMessage() {
        String msg = chatInput.getText();
        if (msg != null && !msg.trim().isEmpty()) {
            chatMessages.add("Moi: " + msg);
            chatInput.clear();
            chatListView.scrollTo(chatMessages.size() - 1);
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
        secondsElapsed = 0;
        timerLabel.setText("00:00:00");
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information");
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
