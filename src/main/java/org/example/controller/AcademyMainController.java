package org.example.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AcademyMainController {

    @FXML
    void navigateToDashboard(Event event) { switchScene(event, "/AcademyMainView.fxml"); }

    @FXML
    void navigateToFormation(Event event) { switchScene(event, "/FormationView.fxml"); }

    @FXML
    void navigateToSession(Event event) { switchScene(event, "/SessionView.fxml"); }

    @FXML
    void navigateToBooking(Event event) { switchScene(event, "/BookingView.fxml"); }

    @FXML
    void navigateToEvaluation(Event event) { switchScene(event, "/EvaluationView.fxml"); }

    @FXML
    void navigateToCertification(Event event) { switchScene(event, "/CertificationView.fxml"); }

    @FXML
    void navigateToVision(MouseEvent event) { switchScene(event, "/user/VisionConferenceView.fxml"); }

    @FXML
    void navigateToUserMode(ActionEvent event) { switchScene(event, "/user/UserDashboard.fxml"); }

    private void switchScene(Event event, String fxmlPath) {
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
}
