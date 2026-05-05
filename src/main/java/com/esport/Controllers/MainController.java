package com.esport.Controllers;

import com.esport.Models.Tournament;
import com.esport.Models.TournamentFormat;
import com.esport.Service.TournamentService;
import com.esport.Service.BetService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MainController {

    @FXML
    private ListView<Tournament> tournamentListView;

    @FXML
    private TextField tName;
    @FXML
    private TextField tGame;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Button startTournamentBtn;

    private final TournamentService tournamentService = new TournamentService();
    private final BetService betService = new BetService();

    @FXML
    public void initialize() {
        refreshList();
    }

    private void refreshList() {
        tournamentListView.getItems().clear();
        tournamentListView.getItems().addAll(tournamentService.getAllTournaments());
    }

    @FXML
    public void handleAddTournament() {
        if(tName.getText().isEmpty() || tGame.getText().isEmpty()) {
            statusLabel.setText("Please fill all fields!");
            return;
        }

        Tournament t = new Tournament(tName.getText(), tGame.getText(), TournamentFormat.SINGLE_ELIMINATION, 16, new BigDecimal("1000.00"), new BigDecimal("50.00"), LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(6));
        tournamentService.addTournament(t);
        statusLabel.setText("Tournament Added successfully!");
        refreshList();
    }

    @FXML
    public void handleStartTournament() {
        Tournament selected = tournamentListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select an upcoming tournament to start.");
            return;
        }

        // Advanced Logic: Start tournament & generate brackets
        tournamentService.startTournament(selected.getId());
        statusLabel.setText("Tournament Started & Brackets Generated!");
        refreshList();
    }
    
    @FXML
    public void handleCalculateOdds() {
        // Advanced Logic demonstration: Dynamic Pari Mutuel odds
        // Simulating checking odds for team 1 in match 1
        BigDecimal odds = betService.calculateDynamicOdds(1, 101);
        statusLabel.setText("Dynamic Odds for Team 1: " + odds);
    }
}
