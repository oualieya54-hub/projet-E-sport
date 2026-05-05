package com.esport.Controllers;

import com.esport.Models.Tournament;
import com.esport.Models.TournamentFormat;
import com.esport.Service.TournamentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TournamentController {

    @FXML
    private TextField nameInput;
    
    @FXML
    private TextField gameInput;
    
    @FXML
    private TextField teamsInput;
    
    @FXML
    private TextField prizeInput;

    @FXML
    private ComboBox<TournamentFormat> formatComboBox;

    @FXML
    private Label feedbackLabel;

    private final TournamentService tournamentService = new TournamentService();

    @FXML
    public void initialize() {
        // Populate the combo box with the Enums we created earlier!
        formatComboBox.getItems().setAll(TournamentFormat.values());
        formatComboBox.getSelectionModel().selectFirst(); // Select the first one by default
    }

    @FXML
    public void handleSaveTournament(ActionEvent actionEvent) {
        String name = nameInput.getText();
        String game = gameInput.getText();
        String teamsText = teamsInput.getText();
        String prizeText = prizeInput.getText();
        TournamentFormat format = formatComboBox.getValue();

        if (name.isEmpty() || game.isEmpty() || teamsText.isEmpty() || prizeText.isEmpty() || format == null) {
            feedbackLabel.setText("Error: Please fill all fields!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            int maxTeams = Integer.parseInt(teamsText);
            BigDecimal prizePool = new BigDecimal(prizeText);

            // Entry fee is just a dummy calculation for this simple example
            BigDecimal entryFee = new BigDecimal("10.00"); 

            Tournament newTournament = new Tournament(
                    name, 
                    game, 
                    format, 
                    maxTeams, 
                    prizePool, 
                    entryFee,
                    LocalDateTime.now().plusDays(14), // Starts in 2 weeks
                    LocalDateTime.now().plusDays(10)  // Registration closes in 10 days
            );

            tournamentService.addTournament(newTournament);

            feedbackLabel.setText("Success! Tournament '" + name + "' created.");
            feedbackLabel.setStyle("-fx-text-fill: green;");
            
            // Clear inputs
            nameInput.clear();
            gameInput.clear();
            teamsInput.clear();
            prizeInput.clear();

        } catch (NumberFormatException e) {
            feedbackLabel.setText("Error: Teams must be whole numbers, Prize must be a decimal!");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            feedbackLabel.setText(e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
