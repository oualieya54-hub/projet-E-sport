package com.esport.iservice;

import com.esport.models.TournamentMatch;
import java.util.List;

public interface IMatchService {
    void addMatch(TournamentMatch match);
    void updateMatch(TournamentMatch match);
    void deleteMatch(int id);
    TournamentMatch getMatchById(int id);
    List<TournamentMatch> getMatchesByTournament(int tournamentId);
}
