package com.esport.iservice;

import com.esport.models.Bet;
import java.math.BigDecimal;
import java.util.List;

public interface IBetService {
    void placeBet(Bet bet);
    void updateBet(Bet bet);
    void deleteBet(int id);
    Bet getBetById(int id);
    List<Bet> getBetsByMatch(int matchId);
    
    // Advanced logic: Pari Mutuel dynamic odds
    BigDecimal calculateDynamicOdds(int matchId, int teamId);
    void settleBetsForMatch(int matchId, int winningTeamId);
}
