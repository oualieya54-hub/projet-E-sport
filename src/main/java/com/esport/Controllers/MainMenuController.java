package com.esport.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    private void openWindow(String fxmlFile, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not open window: " + fxmlFile);
        }
    }

    @FXML
    public void openEventWindow(ActionEvent event) {
        openWindow("/com/esport/views/EventView.fxml", "Manage Events", 400, 500);
    }

    @FXML
    public void openTournamentWindow(ActionEvent event) {
        openWindow("/com/esport/views/TournamentView.fxml", "Manage Tournaments", 400, 550);
    }

    @FXML
    public void openMatchWindow(ActionEvent event) {
        openWindow("/com/esport/views/MatchView.fxml", "Schedule Matches", 400, 500);
    }

    @FXML
    public void openBetWindow(ActionEvent event) {
        openWindow("/com/esport/views/BetView.fxml", "Place Bets", 400, 550);
    }
}
