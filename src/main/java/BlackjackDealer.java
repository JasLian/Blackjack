// BlackjackDealer.java
// Written by Jason Liang
// Defines the BlackjackDealer class

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

public class BlackjackDealer {

    private final String[] suits = {"Hearts", "Diamonds", "Spades", "Clubs"};

    private LinkedList<Card> deck;

    // method generate a deck of 52 poker cards as a linked list
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

    // method shuffles all cards back into the deck by generating a new deck
    public void shuffleDeck(){ generateDeck(); }

    // method draws one card from the deck by removing the first element/top card from the deck
    public Card drawOne(){ return deck.remove(); }

    // methods draws two cards and return the hand as an ArrayList
    public ArrayList<Card> dealHand(){

        ArrayList<Card> newHand = new ArrayList<>();

        newHand.add(drawOne());
        newHand.add(drawOne());

        return newHand;
    }

    // method returns deck size
    public int deckSize(){ return deck.size(); }


}
