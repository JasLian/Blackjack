// BlackjackGameLogic.java
// Written by Jason Liang
// Defines the BlackjackGameLogic class

import java.util.ArrayList;
import java.util.LinkedList;

public class BlackjackGameLogic {

    // method calculates who won a game of blackjack given a player's and dealer's hand
    public String whoWon(ArrayList<Card> playerHand1, ArrayList<Card> dealerHand){

        // calculate both hands' totals
        int playerVal = handTotal(playerHand1);
        int dealerVal = handTotal(dealerHand);

        // logic to check the result of the game based on the hand totals
        if (playerVal < 21){

            if (playerVal == dealerVal){
                return "push";
            }

            if (dealerVal > 21 || playerVal > dealerVal){
                return "player";
            }

            return "dealer";

        }
        else if (playerVal == 21){

            if (playerHand1.size() == 2){

                if (dealerVal == 21 && dealerHand.size() == 2){
                    return "push";
                }

                return "player";
            }
            else if (dealerVal == 21 && dealerHand.size() == 2){
                return "dealer";
            }
            else{

                if (dealerVal == 21){
                    return "push";
                }
            }

            return "player";
        }

        return "dealer";

    }

    // method calculates the best possible total for a given poker hand
    public int handTotal(ArrayList<Card> hand){

        int total = 0;
        LinkedList<Card> aces = new LinkedList<>();

        // first total all cards that are not aces, and storing all aces instead
        for (Card c : hand) {

            if (c.value == 1){
                aces.add(c);
                continue;
            }

            total += Math.min(c.value, 10);
        }

        // for every ace, attempt to add a value of 11 without going bust
        while (!aces.isEmpty()){

            aces.remove();

            if (total + 11 <= 21 - aces.size()){ // add 11 accounting for remaining aces in hand
                total += 11;
            }
            else{
                total += 1;
            }

        }

        return total;
    }

    // method decides if the dealer should draw based on their current hand
    public boolean evaluateBankerDraw(ArrayList<Card> hand){ return handTotal(hand) <= 16; }
}
