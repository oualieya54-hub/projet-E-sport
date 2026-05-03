package com.esport.iservice;

import com.esport.models.Tournament;
import java.util.List;

public interface ITournamentService {
    void addTournament(Tournament tournament);
    void updateTournament(Tournament tournament);
    void deleteTournament(int id);
    Tournament getTournamentById(int id);
    List<Tournament> getAllTournaments();
    
    // Advanced logic
    void startTournament(int tournamentId); // Automatically generates bracket
}
