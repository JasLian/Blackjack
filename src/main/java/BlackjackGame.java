// BlackjackGame.java
// Written by Jason Liang
// Defines the BlackjackGame class

import java.util.ArrayList;

public class BlackjackGame {

    ArrayList<Card> playerHand;
    ArrayList<Card> bankerHand;

    BlackjackDealer theDealer;
    BlackjackGameLogic gameLogic;

    double currentBet;
    double totalWinnings;

    // constructor
    public BlackjackGame(){
        theDealer = new BlackjackDealer();
        gameLogic = new BlackjackGameLogic();
    }

    // method calculates the winnings for a given round based on the bet
    public double evaluateWinnings(){
        double winnings;

        String winner = gameLogic.whoWon(playerHand, bankerHand);

        if (winner.equals("push")){
            winnings = 0.0;
        }
        else if (winner.equals("player")){

            winnings = currentBet;

            if (playerHand.size() == 2 && gameLogic.handTotal(playerHand) == 21){
                winnings *= 1.5; // 150% of bet if player hit blackjack
            }

        }
        else{
            winnings = -1 * currentBet;
        }

        return winnings;
    }

}
