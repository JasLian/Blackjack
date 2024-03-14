import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlackjackTest {

    // Test the construction of a Card obj
    @Test
    public void test_CardClass(){
        Card newCard = new Card("Diamond", 6);

        assertEquals("Diamond", newCard.suit, "Card suit does not match what was assigned");
        assertEquals(6, newCard.value, "Card value does not match what was assigned");
    }

    @Test
    public void test_whoWon(){
        ArrayList<Card> player = new ArrayList<>();
        player.add(new Card("Diamonds", 10));

        ArrayList<Card> dealer = new ArrayList<>();
        dealer.add(new Card("Clubs", 10));

        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        assertEquals("push", logicObj.whoWon(player, dealer), "whoWon does not correctly identify a push");

        dealer.add(new Card("Spades", 2));
        assertEquals("dealer", logicObj.whoWon(player,dealer), "Dealer didn't win with a higher hand");

        dealer.add(new Card("Spades", 9));
        assertEquals("dealer", logicObj.whoWon(player, dealer), "Dealer didn't win even with a hand of 21");

        dealer.add(new Card("Hearts", 4));
        assertEquals("player", logicObj.whoWon(player, dealer), "Player didn't win when dealer busted");

        dealer = new ArrayList<>();
        dealer.add(new Card("Diamonds", 4));

        assertEquals("player", logicObj.whoWon(player, dealer), "Player didn't win with a higher hand");

        player.add(new Card("Hearts", 11));
        assertEquals("player", logicObj.whoWon(player, dealer), "Player didn't win with a higher hand (2 face cards)");

        player.add(new Card("Spades", 2));
        assertEquals("dealer", logicObj.whoWon(player, dealer), "Dealer didn't win when player busted");

        dealer.add(new Card("Clubs", 10));
        dealer.add(new Card("Clubs", 10));
        assertEquals("dealer", logicObj.whoWon(player, dealer), "Dealer didn't win when the player busted (Both sides busted)");

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 1));
        player.add(new Card("Clubs", 12));

        dealer.add(new Card("Clubs", 1));
        dealer.add(new Card("Clubs", 12));

        assertEquals("push", logicObj.whoWon(player, dealer), "No push identified when both sides hit blackjack");

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 1));
        player.add(new Card("Clubs", 11));

        dealer.add(new Card("Clubs", 11));
        dealer.add(new Card("Clubs", 12));

        assertEquals("player", logicObj.whoWon(player, dealer), "Player did not with a blackjack (Dealer = 20)");

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 10));
        player.add(new Card("Clubs", 12));

        dealer.add(new Card("Clubs", 1));
        dealer.add(new Card("Clubs", 12));

        assertEquals("dealer", logicObj.whoWon(player, dealer), "Dealer did not with a blackjack (Player = 20)");

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 1));
        player.add(new Card("Clubs", 10));

        dealer.add(new Card("Clubs", 1));
        dealer.add(new Card("Clubs", 12));
        dealer.add(new Card("Clubs", 12));

        assertEquals("player", logicObj.whoWon(player, dealer), "Player did not with a blackjack (Dealer no blackjack)");

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 1));
        player.add(new Card("Clubs", 12));
        player.add(new Card("Clubs", 12));

        dealer.add(new Card("Clubs", 1));
        dealer.add(new Card("Clubs", 10));

        assertEquals("dealer", logicObj.whoWon(player, dealer), "Dealer did not with a blackjack (Player no blackjack)");

    }

    @Test
    public void test_HandTotal(){

        ArrayList<Card> hand = new ArrayList<>();
        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        assertEquals(0, logicObj.handTotal(hand), "Empty hand didn't total 0");

        hand.add(new Card("Hearts", 5));
        assertEquals(5, logicObj.handTotal(hand), "Single card hand total is incorrect");

        hand.add(new Card("Hearts", 5));
        assertEquals(10, logicObj.handTotal(hand), "Two card hand totaled is incorrect");

        hand.add(new Card("Hearts", 1));
        assertEquals(21, logicObj.handTotal(hand), "Did not add an Ace correct");

        hand = new ArrayList<>();

        hand.add(new Card("Hearts", 13));
        hand.add(new Card("Hearts", 1));
        assertEquals(21, logicObj.handTotal(hand), "Did not calculate blackjack correctly");

        hand = new ArrayList<>();
        hand.add(new Card("Hearts", 11));
        hand.add(new Card("Hearts", 12));
        hand.add(new Card("Hearts", 13));
        assertEquals(30, logicObj.handTotal(hand), "Did not total face cards incorrectly");

    }

    @Test
    public void test_HandsWithAces(){

        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        ArrayList<Card> hand = new ArrayList<>();

        hand.add(new Card("Diamonds", 1));
        assertEquals(11, logicObj.handTotal(hand), "Did not calculate the highest hand with only 1 Ace");

        hand.add(new Card("Diamonds", 1));
        assertEquals(12, logicObj.handTotal(hand), "Did not calculate the highest hand with 2 Aces");

        hand.add(new Card("Diamonds", 1));
        assertEquals(13, logicObj.handTotal(hand), "Did not calculate the best possible hand with 3 Aces");

        hand.add(new Card("Diamonds", 10));
        assertEquals(13, logicObj.handTotal(hand), "Did not calculate the best possible hand with 3 Aces and a 10");

        hand.add(new Card("Diamonds", 1));
        assertEquals(14, logicObj.handTotal(hand), "Did not calculate the best possible hand with 4 Aces and a 10");

        hand.add(new Card("Diamonds", 5));
        assertEquals(19, logicObj.handTotal(hand), "Did not calculate the best possible hand with 4 Aces and 10 + 5");

        hand.add(new Card("Diamonds", 1));
        assertEquals(20, logicObj.handTotal(hand), "Did not calculate the best possible hand with 5 Aces and a 10 + 5");

        hand.add(new Card("Diamonds", 1));
        assertEquals(21, logicObj.handTotal(hand), "Did not calculate the best possible hand with 6 Aces and a 10 + 5");

        hand.add(new Card("Diamonds", 1));
        assertEquals(22, logicObj.handTotal(hand), "Did not calculate the best possible hand with 7 Aces and a 10 + 5");

    }

    @Test
    public void test_evaluateBankerDraw(){
        ArrayList<Card> dealer = new ArrayList<>();
        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        assertTrue(logicObj.evaluateBankerDraw(dealer));

        dealer.add(new Card("Diamonds", 10));
        assertTrue(logicObj.evaluateBankerDraw(dealer), "Dealer should draw with hand of 10");
        dealer.add(new Card("Diamonds", 6));
        assertTrue(logicObj.evaluateBankerDraw(dealer), "Dealer should draw with hand of 16");

        dealer.add(new Card("Diamonds", 1));
        assertFalse(logicObj.evaluateBankerDraw(dealer), "Dealer shouldn't draw with hand of 17");
        dealer.add(new Card("Diamonds", 5));
        assertFalse(logicObj.evaluateBankerDraw(dealer), "Dealer shouldn't draw with hand of 22");

    }

    @Test
    public void test_generateDeck(){

        BlackjackDealer dealer = new BlackjackDealer();

        dealer.generateDeck();

        assertEquals(52, dealer.deckSize(), "Deck size for new deck is not incorrect");

        Map<String, Integer> suitCount = new HashMap<>();
        suitCount.put("Diamonds", 0);
        suitCount.put("Clubs", 0);
        suitCount.put("Hearts", 0);
        suitCount.put("Spades", 0);

        Map<Integer, Integer> faceCount = new HashMap<>();
        for (int i = 1; i <= 13; i++){
            faceCount.put(i, 0);
        }

        // empty the deck and counts the frequency of each suit and value
        while (dealer.deckSize() != 0){

            Card drewCard = dealer.drawOne();

            suitCount.replace(drewCard.suit, suitCount.get(drewCard.suit) + 1);
            faceCount.replace(drewCard.value, faceCount.get(drewCard.value) + 1);

        }

        assertEquals(0, dealer.deckSize(), "The deck size is not 0 after being emptied");

        assertEquals(13, suitCount.get("Diamonds"), "Incorrect number of Diamonds were seen");
        assertEquals(13, suitCount.get("Clubs"), "Incorrect number of Clubs were seen");
        assertEquals(13, suitCount.get("Hearts"), "Incorrect number of Hearts were seen");
        assertEquals(13, suitCount.get("Spades"), "Incorrect number of Spades were seen");

        for (int i = 1; i <= 13; i++){
            assertEquals(4, faceCount.get(i), "Incorrect number of " + i + " cards were seen");
        }

    }

    @Test
    public void test_dealHand(){

        BlackjackDealer dealer = new BlackjackDealer();
        dealer.generateDeck();
        ArrayList<Card> hand = dealer.dealHand();

        assertEquals(2, hand.size());
        assertEquals(50, dealer.deckSize());

        Card first = hand.get(0), second = hand.get(1);

        if (first.value == second.value){
            assertNotEquals(first.suit, second.suit, "Two duplicate cards were given");
        }
        else if (first.suit.equals(second.suit)){
            assertNotEquals(first.value, second.value, "Two duplicate cards were given");
        }

    }

    @Test
    public void test_shuffleDeck(){

        BlackjackDealer dealer = new BlackjackDealer();

        dealer.generateDeck();

        for (int i = 0; i < 10; i++){
            dealer.drawOne();
        }

        assertEquals(42, dealer.deckSize(), "Deck size is not correct after drawing 8 cards");

        dealer.shuffleDeck();
        assertEquals(52, dealer.deckSize(), "Deck does not contain 52 cards after reshuffling");

    }

    @Test
    public void test_evaluateWinnings(){

        BlackjackGame game = new BlackjackGame();
        game.currentBet = 100.0;
        game.playerHand = new ArrayList<>();
        game.bankerHand = new ArrayList<>();

        game.playerHand.add(new Card("Clubs", 10));
        game.bankerHand.add(new Card("Clubs", 10));

        assertEquals(0.0, game.evaluateWinnings(), "Incorrect winnings calculated when tied");

        game.playerHand.add(new Card("Clubs", 2));

        assertEquals(100.0, game.evaluateWinnings(), "Incorrect winnings calculated when the player wins");

        game.bankerHand.add(new Card("Clubs", 3));
        assertEquals(-100.0, game.evaluateWinnings(), "Incorrect winnings calculated when the player loses");

        game.playerHand.add(new Card("Clubs", 9));
        assertEquals(100.0, game.evaluateWinnings(), "Incorrect winnings calculated when the player hits 21");

        game.bankerHand.add(new Card("Clubs", 8));
        assertEquals(0, game.evaluateWinnings(), "Incorrect winnings calculated when both sides hit 21");

        game.playerHand = new ArrayList<>();
        game.bankerHand = new ArrayList<>();

        game.playerHand.add(new Card("Spades", 1));
        game.playerHand.add(new Card("Spades", 11));
        game.bankerHand.add(new Card("Spades", 5));
        assertEquals(150.0, game.evaluateWinnings(), "Incorrect winnings when the player hits blackjack");

        game.bankerHand = new ArrayList<>();
        game.bankerHand.add(new Card("Clubs", 12));
        game.bankerHand.add(new Card("Clubs", 1));

        assertEquals(0, game.evaluateWinnings(), "Incorrect winnings when both sides hit blackjack");

        game.playerHand = new ArrayList<>();

        assertEquals(-100, game.evaluateWinnings(), "Incorrect winnings when the player loses to blackjack");

        game.currentBet = 1000.0;

        assertEquals(-1000.0, game.evaluateWinnings(), "Incorrect winnings calculated when the bet changes");
    }


}
