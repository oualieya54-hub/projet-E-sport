package com.esport.metier;

import com.esport.dao.BetDAO;
import com.esport.dao.MatchDAO;
import com.esport.model.Bet;
import com.esport.model.TournamentMatch;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * BetMetier — all business operations for bets and wallet.
 *
 * Operations:
 *  - creerWallet()       → Initialize wallet for a user
 *  - consulterSolde()    → Check balance
 *  - deposer()           → Deposit funds
 *  - placerPari()        → Place a bet
 *  - reglerparis()       → Settle all bets for a match
 *  - rechercherPari()    → Find a bet by ID
 *  - listerParisUser()   → List all bets for a user
 *  - listerParisMatch()  → List all bets on a match
 *  - annulerPari()       → Cancel a pending bet and refund
 */
public class BetMetier {

    private final BetDAO   betDAO   = new BetDAO();
    private final MatchDAO matchDAO = new MatchDAO();

    private static final BigDecimal MIN_BET     = new BigDecimal("1.00");
    private static final BigDecimal MAX_BET     = new BigDecimal("1000.00");
    private static final BigDecimal MIN_DEPOSIT = new BigDecimal("5.00");

    // =========================================================================
    //  CREER WALLET
    // =========================================================================
    public void creerWallet(int userId) throws SQLException {
        betDAO.createWallet(userId);
        System.out.println("💰 Wallet créé/vérifié pour user #" + userId);
    }

    // =========================================================================
    //  CONSULTER SOLDE
    // =========================================================================
    public BigDecimal consulterSolde(int userId) throws SQLException {
        BigDecimal balance = betDAO.getWalletBalance(userId);
        System.out.println("💰 Solde user #" + userId + ": $" + balance);
        return balance;
    }

    // =========================================================================
    //  DEPOSER — Deposit funds into wallet
    // =========================================================================
    public void deposer(int userId, BigDecimal montant) throws Exception {
        if (montant == null || montant.compareTo(MIN_DEPOSIT) < 0)
            throw new IllegalArgumentException("Dépôt minimum: $" + MIN_DEPOSIT);

        betDAO.deposit(userId, montant);
        BigDecimal newBalance = betDAO.getWalletBalance(userId);
        System.out.println("✅ Dépôt de $" + montant + " effectué. Nouveau solde: $" + newBalance);
    }

    // =========================================================================
    //  PLACER PARI — Place a bet on a match
    // =========================================================================
    public Bet placerPari(int userId, int matchId, int betOnTeamId,
                          BigDecimal montant, BigDecimal cote) throws Exception {

        // Validations
        if (montant.compareTo(MIN_BET) < 0)
            throw new IllegalArgumentException("Mise minimum: $" + MIN_BET);
        if (montant.compareTo(MAX_BET) > 0)
            throw new IllegalArgumentException("Mise maximum: $" + MAX_BET);
        if (cote.compareTo(BigDecimal.ONE) <= 0)
            throw new IllegalArgumentException("La cote doit être supérieure à 1.0");

        // Check match exists and is live or scheduled
        Optional<TournamentMatch> matchOpt = matchDAO.findById(matchId);
        if (matchOpt.isEmpty())
            throw new Exception("Match #" + matchId + " introuvable.");

        TournamentMatch match = matchOpt.get();
        if (match.getStatus() == TournamentMatch.Status.COMPLETED)
            throw new Exception("Ce match est déjà terminé, impossible de parier.");
        if (match.getStatus() == TournamentMatch.Status.CANCELLED)
            throw new Exception("Ce match est annulé.");

        // Check team is in the match
        if (betOnTeamId != match.getTeam1Id() && betOnTeamId != match.getTeam2Id())
            throw new Exception("L'équipe #" + betOnTeamId + " ne participe pas à ce match.");

        // Check balance
        BigDecimal balance = betDAO.getWalletBalance(userId);
        if (balance.compareTo(montant) < 0)
            throw new Exception("Solde insuffisant. Disponible: $" + balance + ", Requis: $" + montant);

        // Deduct and place bet
        betDAO.deduct(userId, montant);
        Bet bet = new Bet(userId, matchId, betOnTeamId, montant, cote);
        int id = betDAO.create(bet);
        if (id < 0) {
            betDAO.deposit(userId, montant); // refund
            throw new Exception("Échec du pari — remboursement effectué.");
        }
        bet.setId(id);

        System.out.println("🎲 Pari placé: " + bet);
        System.out.println("   Gain potentiel: $" + bet.getPotentialWin());
        System.out.println("   Solde restant: $" + betDAO.getWalletBalance(userId));
        return bet;
    }

    // =========================================================================
    //  REGLER PARIS — Settle all bets for a completed match
    // =========================================================================
    public int reglerParis(int matchId, int winnerTeamId) throws Exception {
        Optional<TournamentMatch> opt = matchDAO.findById(matchId);
        if (opt.isEmpty())
            throw new Exception("Match #" + matchId + " introuvable.");

        int settled = betDAO.settleBets(matchId, winnerTeamId);
        System.out.println("🏁 " + settled + " paris réglés pour le match #" + matchId +
                " | Équipe gagnante: #" + winnerTeamId);
        return settled;
    }

    // =========================================================================
    //  RECHERCHER PARI PAR ID
    // =========================================================================
    public Bet rechercherPari(int id) throws Exception {
        Optional<Bet> opt = betDAO.findById(id);
        if (opt.isEmpty())
            throw new Exception("Pari #" + id + " introuvable.");
        return opt.get();
    }

    // =========================================================================
    //  LISTER PARIS D'UN USER
    // =========================================================================
    public List<Bet> listerParisUser(int userId) throws SQLException {
        List<Bet> bets = betDAO.findByUser(userId);
        System.out.println("📋 User #" + userId + " → " + bets.size() + " pari(s)");
        return bets;
    }

    // =========================================================================
    //  LISTER PARIS D'UN MATCH
    // =========================================================================
    public List<Bet> listerParisMatch(int matchId) throws SQLException {
        List<Bet> bets = betDAO.findByMatch(matchId);
        System.out.println("📋 Match #" + matchId + " → " + bets.size() + " pari(s)");
        return bets;
    }

    // =========================================================================
    //  ANNULER PARI — Cancel a pending bet and refund the amount
    // =========================================================================
    public void annulerPari(int betId) throws Exception {
        Bet bet = rechercherPari(betId);

        if (bet.getStatus() != Bet.Status.PENDING)
            throw new Exception("Seul un pari 'pending' peut être annulé.");

        bet.setStatus(Bet.Status.CANCELLED);
        betDAO.update(bet);
        betDAO.deposit(bet.getUserId(), bet.getAmount()); // refund

        System.out.println("🚫 Pari #" + betId + " annulé. $" + bet.getAmount() +
                " remboursé à l'user #" + bet.getUserId());
    }

    // =========================================================================
    //  LISTER TOUS LES PARIS
    // =========================================================================
    public List<Bet> listerTous() throws SQLException {
        return betDAO.findAll();
    }
}