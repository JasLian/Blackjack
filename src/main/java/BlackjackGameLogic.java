import java.util.ArrayList;
import java.util.LinkedList;

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
        else if (playerVal == 21){

            if (dealerVal == 21){
                return "push";
            }

            return "player";
        }

        return "dealer";

    }

    public int handTotal(ArrayList<Card> hand){

        int total = 0;
        LinkedList<Card> aces = new LinkedList<>();

        for (Card c : hand) {

            if (c.value == 1){
                aces.add(c);
                continue;
            }

            total += Math.min(c.value, 10);
        }

        while (!aces.isEmpty()){

            aces.remove();

            if (total + 11 <= 21 - aces.size()){
                total += 11;
            }
            else{
                total += 1;
            }

        }

        return total;
    }

    public boolean evaluateBankerDraw(ArrayList<Card> hand){ return handTotal(hand) <= 16; }
}
