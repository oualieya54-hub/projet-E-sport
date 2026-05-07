package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private void openBoutique() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BoutiqueView.fxml"));
            Parent root = loader.load();

            Stage clientStage = new Stage();
            clientStage.setTitle("Game Pilot - Boutique Client");

            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            clientStage.setScene(scene);
            clientStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur ouverture de la boutique : " + e.getMessage());
        }
    }
}
