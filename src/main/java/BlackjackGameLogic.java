import java.util.ArrayList;

public class BlackjackGameLogic {

    public String whoWon(ArrayList<Card> playerHand1, ArrayList<Card> dealerHand){

        int playerVal = handTotal(playerHand1);

        int dealerVal = handTotal(dealerHand);

        if (playerVal < 21){

            if (playerVal == dealerVal){
                return "push";
            }

            if (dealerVal > 21 || playerVal > dealerVal){
                return "player";
            }

            return "dealer";

        }
        else if (playerVal == 21){ return "player"; }

        return "dealer";

    }

    public int handTotal(ArrayList<Card> hand){

        int total = 0;

        for (Card c : hand) {
            total += c.value;
        }

        return total;
    }

    public boolean evaluateBankerDraw(ArrayList<Card> hand){ return handTotal(hand) <= 16; }
}
