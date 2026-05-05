package com.esport.Controllers;

import com.esport.Models.Bet;
import com.esport.Service.BetService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class BetController {

    @FXML
    private TextField userIdInput;
    
    @FXML
    private TextField matchIdInput;
    
    @FXML
    private TextField teamIdInput;
    
    @FXML
    private TextField amountInput;

    @FXML
    private Label feedbackLabel;
    
    @FXML
    private Label oddsLabel;

    private final BetService betService = new BetService();

    @FXML
    public void handlePlaceBet(ActionEvent actionEvent) {
        String uIdStr = userIdInput.getText();
        String mIdStr = matchIdInput.getText();
        String tIdStr = teamIdInput.getText();
        String amountStr = amountInput.getText();

        if (uIdStr.isEmpty() || mIdStr.isEmpty() || tIdStr.isEmpty() || amountStr.isEmpty()) {
            feedbackLabel.setText("Error: Please fill all fields!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            int userId = Integer.parseInt(uIdStr);
            int matchId = Integer.parseInt(mIdStr);
            int teamId = Integer.parseInt(tIdStr);
            BigDecimal amount = new BigDecimal(amountStr);

            // Our advanced logic: Before creating the bet, let's see what the current odds are!
            BigDecimal currentOdds = betService.calculateDynamicOdds(matchId, teamId);
            oddsLabel.setText("Current Dynamic Odds: " + currentOdds + "x");

            Bet newBet = new Bet(userId, matchId, teamId, amount, currentOdds);

            betService.placeBet(newBet);

            feedbackLabel.setText("Success! Placed $" + amount + " on Team " + teamId + " (Potential Win: $" + newBet.getPotentialWin() + ")");
            feedbackLabel.setStyle("-fx-text-fill: green;");
            
            // Clear inputs
            userIdInput.clear();
            matchIdInput.clear();
            teamIdInput.clear();
            amountInput.clear();

        } catch (NumberFormatException e) {
            feedbackLabel.setText("Error: IDs must be whole numbers, Amount must be a decimal!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            feedbackLabel.setText("DB Error: " + e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
