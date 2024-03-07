import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

public class BlackjackDealer {

    private final String[] suits = {"Hearts", "Diamonds", "Spades", "Clubs"};

    private LinkedList<Card> deck;

    public void generateDeck(){

        deck = new LinkedList<>();

        for (int i = 1; i <= 13; i++){
            deck.add(new Card(suits[0], i));
            deck.add(new Card(suits[1], i));
            deck.add(new Card(suits[2], i));
            deck.add(new Card(suits[3], i));
        }

        Collections.shuffle(deck);

    }

    public void shuffleDeck(){ generateDeck(); }

    public Card drawOne(){ return deck.remove(); }

    public ArrayList<Card> dealHand(){

        ArrayList<Card> newHand = new ArrayList<>();

        newHand.add(drawOne());
        newHand.add(drawOne());

        return newHand;
    }

    public int deckSize(){ return deck.size(); }


}
