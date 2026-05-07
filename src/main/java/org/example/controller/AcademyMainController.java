package org.example.controller;

import javafx.event.ActionEvent;
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
    void navigateToFormation(Object event) { switchScene(event, "/FormationView.fxml"); }

    @FXML
    void navigateToSession(Object event) { switchScene(event, "/SessionView.fxml"); }

    @FXML
    void navigateToBooking(Object event) { switchScene(event, "/BookingView.fxml"); }

    @FXML
    void navigateToEvaluation(Object event) { switchScene(event, "/EvaluationView.fxml"); }

    @FXML
    void navigateToCertification(Object event) { switchScene(event, "/CertificationView.fxml"); }

    private void switchScene(Object event, String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage;
            
            if (event instanceof ActionEvent) {
                stage = (Stage) ((Node) ((ActionEvent) event).getSource()).getScene().getWindow();
            } else if (event instanceof MouseEvent) {
                stage = (Stage) ((Node) ((MouseEvent) event).getSource()).getScene().getWindow();
            } else {
                return;
            }
            
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
