package com.esport.Controllers;

import com.esport.Models.TournamentMatch;
import com.esport.Service.MatchService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;

public class MatchController {

    @FXML
    private TextField tournamentIdInput;
    
    @FXML
    private TextField roundInput;
    
    @FXML
    private TextField team1IdInput;
    
    @FXML
    private TextField team2IdInput;

    @FXML
    private Label feedbackLabel;

    private final MatchService matchService = new MatchService();

    @FXML
    public void handleSaveMatch(ActionEvent actionEvent) {
        String tIdStr = tournamentIdInput.getText();
        String roundStr = roundInput.getText();
        String t1Str = team1IdInput.getText();
        String t2Str = team2IdInput.getText();

        if (tIdStr.isEmpty() || roundStr.isEmpty() || t1Str.isEmpty() || t2Str.isEmpty()) {
            feedbackLabel.setText("Error: Please fill all fields!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            int tournamentId = Integer.parseInt(tIdStr);
            int round = Integer.parseInt(roundStr);
            int team1Id = Integer.parseInt(t1Str);
            int team2Id = Integer.parseInt(t2Str);

            // Create match object (Match Number is hardcoded as 1 for simplicity)
            TournamentMatch newMatch = new TournamentMatch(
                    tournamentId, 
                    round, 
                    1, 
                    team1Id, 
                    team2Id, 
                    LocalDateTime.now().plusDays(2) // Match happens in 2 days
            );

            // Save to DB
            matchService.addMatch(newMatch);

            feedbackLabel.setText("Success! Match scheduled between Team " + team1Id + " and Team " + team2Id);
            feedbackLabel.setStyle("-fx-text-fill: green;");
            
            // Clear inputs
            tournamentIdInput.clear();
            roundInput.clear();
            team1IdInput.clear();
            team2IdInput.clear();

        } catch (NumberFormatException e) {
            feedbackLabel.setText("Error: All fields must be whole numbers (IDs)!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            feedbackLabel.setText("DB Error: " + e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
