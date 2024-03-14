import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlackjackTest {

    @Test
    public void test_CardClass(){

        Card newCard = new Card("Diamond", 6);

        assertEquals("Diamond", newCard.suit);
        assertEquals(6, newCard.value);
    }

    @Test
    public void test_whoWon(){
        ArrayList<Card> player = new ArrayList<>();
        player.add(new Card("Diamonds", 10));

        ArrayList<Card> dealer = new ArrayList<>();
        dealer.add(new Card("Clubs", 10));

        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        assertEquals("push", logicObj.whoWon(player, dealer));

        dealer.add(new Card("Spades", 2));
        assertEquals("dealer", logicObj.whoWon(player,dealer));

        dealer.add(new Card("Spades", 9));
        assertEquals("dealer", logicObj.whoWon(player, dealer));

        dealer.add(new Card("Hearts", 4));
        assertEquals("player", logicObj.whoWon(player, dealer));

        dealer = new ArrayList<>();
        dealer.add(new Card("Diamonds", 4));

        assertEquals("player", logicObj.whoWon(player, dealer));

        player.add(new Card("Hearts", 11));
        assertEquals("player", logicObj.whoWon(player, dealer));

        player.add(new Card("Spades", 2));
        assertEquals("dealer", logicObj.whoWon(player, dealer));

        dealer.add(new Card("Clubs", 17));
        assertEquals("dealer", logicObj.whoWon(player, dealer));

        dealer.add(new Card("Clubs", 3));
        assertEquals("dealer", logicObj.whoWon(player, dealer));

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 1));
        player.add(new Card("Clubs", 12));

        dealer.add(new Card("Clubs", 1));
        dealer.add(new Card("Clubs", 12));

        assertEquals("push", logicObj.whoWon(player, dealer));

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 1));
        player.add(new Card("Clubs", 11));

        dealer.add(new Card("Clubs", 11));
        dealer.add(new Card("Clubs", 12));

        assertEquals("player", logicObj.whoWon(player, dealer));

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 10));
        player.add(new Card("Clubs", 12));

        dealer.add(new Card("Clubs", 1));
        dealer.add(new Card("Clubs", 12));

        assertEquals("dealer", logicObj.whoWon(player, dealer));

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 1));
        player.add(new Card("Clubs", 10));

        dealer.add(new Card("Clubs", 1));
        dealer.add(new Card("Clubs", 12));
        dealer.add(new Card("Clubs", 12));

        assertEquals("player", logicObj.whoWon(player, dealer));

        player.clear();
        dealer.clear();

        player.add(new Card("Clubs", 1));
        player.add(new Card("Clubs", 12));
        player.add(new Card("Clubs", 12));

        dealer.add(new Card("Clubs", 1));
        dealer.add(new Card("Clubs", 10));

        assertEquals("dealer", logicObj.whoWon(player, dealer));

    }

    @Test
    public void test_HandTotal(){

        ArrayList<Card> hand = new ArrayList<>();
        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        assertEquals(0, logicObj.handTotal(hand));

        hand.add(new Card("Hearts", 5));
        assertEquals(5, logicObj.handTotal(hand));

        hand.add(new Card("Hearts", 5));
        assertEquals(10, logicObj.handTotal(hand));

        hand.add(new Card("Hearts", 1));
        assertEquals(21, logicObj.handTotal(hand));

        hand = new ArrayList<>();

        hand.add(new Card("Hearts", 13));
        hand.add(new Card("Hearts", 1));
        assertEquals(21, logicObj.handTotal(hand));

        hand = new ArrayList<>();
        hand.add(new Card("Hearts", 11));
        hand.add(new Card("Hearts", 12));
        hand.add(new Card("Hearts", 13));
        assertEquals(30, logicObj.handTotal(hand));

    }

    @Test
    public void test_HandsWithAces(){

        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        ArrayList<Card> hand = new ArrayList<>();

        hand.add(new Card("Diamonds", 1));
        assertEquals(11, logicObj.handTotal(hand));

        hand.add(new Card("Diamonds", 1));
        assertEquals(12, logicObj.handTotal(hand));

        hand.add(new Card("Diamonds", 1));
        assertEquals(13, logicObj.handTotal(hand));

        hand.add(new Card("Diamonds", 10));
        assertEquals(13, logicObj.handTotal(hand));

        hand.add(new Card("Diamonds", 1));
        assertEquals(14, logicObj.handTotal(hand));

        hand.add(new Card("Diamonds", 5));
        assertEquals(19, logicObj.handTotal(hand));

        hand.add(new Card("Diamonds", 1));
        assertEquals(20, logicObj.handTotal(hand));

        hand.add(new Card("Diamonds", 1));
        assertEquals(21, logicObj.handTotal(hand));

        hand.add(new Card("Diamonds", 1));
        assertEquals(22, logicObj.handTotal(hand));

    }

    @Test
    public void test_evaluateBankerDraw(){
        ArrayList<Card> dealer = new ArrayList<>();
        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        assertTrue(logicObj.evaluateBankerDraw(dealer));

        dealer.add(new Card("Diamonds", 10));
        assertTrue(logicObj.evaluateBankerDraw(dealer));
        dealer.add(new Card("Diamonds", 6));
        assertTrue(logicObj.evaluateBankerDraw(dealer));

        dealer.add(new Card("Diamonds", 1));
        assertFalse(logicObj.evaluateBankerDraw(dealer));
        dealer.add(new Card("Diamonds", 5));
        assertFalse(logicObj.evaluateBankerDraw(dealer));

    }

    @Test
    public void test_generateDeck(){

        BlackjackDealer dealer = new BlackjackDealer();

        dealer.generateDeck();

        assertEquals(52, dealer.deckSize());

        Map<String, Integer> suitCount = new HashMap<>();
        suitCount.put("Diamonds", 0);
        suitCount.put("Clubs", 0);
        suitCount.put("Hearts", 0);
        suitCount.put("Spades", 0);

        Map<Integer, Integer> faceCount = new HashMap<>();
        for (int i = 1; i <= 13; i++){
            faceCount.put(i, 0);
        }

        while (dealer.deckSize() != 0){

            Card drewCard = dealer.drawOne();

            suitCount.replace(drewCard.suit, suitCount.get(drewCard.suit) + 1);
            faceCount.replace(drewCard.value, faceCount.get(drewCard.value) + 1);

        }

        assertEquals(0, dealer.deckSize());

        assertEquals(13, suitCount.get("Diamonds"));
        assertEquals(13, suitCount.get("Clubs"));
        assertEquals(13, suitCount.get("Hearts"));
        assertEquals(13, suitCount.get("Spades"));

        for (int i = 1; i <= 13; i++){
            assertEquals(4, faceCount.get(i));
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
            assertNotEquals(first.suit, second.suit);
        }
        else if (first.suit.equals(second.suit)){
            assertNotEquals(first.value, second.value);
        }

    }

    @Test
    public void test_shuffleDeck(){

        BlackjackDealer dealer = new BlackjackDealer();

        dealer.generateDeck();

        for (int i = 0; i < 10; i++){
            dealer.drawOne();
        }

        assertEquals(42, dealer.deckSize());

        dealer.shuffleDeck();
        assertEquals(52, dealer.deckSize());

    }

    @Test
    public void test_evaluateWinnings(){

        BlackjackGame game = new BlackjackGame();
        game.currentBet = 100.0;
        game.playerHand = new ArrayList<>();
        game.bankerHand = new ArrayList<>();

        game.playerHand.add(new Card("Clubs", 10));
        game.bankerHand.add(new Card("Clubs", 10));

        assertEquals(0.0, game.evaluateWinnings()); // both sides tied when < 21

        game.playerHand.add(new Card("Clubs", 2));

        assertEquals(100.0, game.evaluateWinnings()); // when the player has a higher hand

        game.bankerHand.add(new Card("Clubs", 3));
        assertEquals(-100.0, game.evaluateWinnings()); // when the dealer has a higher hand

        game.playerHand.add(new Card("Clubs", 9));
        assertEquals(100.0, game.evaluateWinnings()); // when the player hits blackjack

        game.bankerHand.add(new Card("Clubs", 8));
        assertEquals(0, game.evaluateWinnings()); // when both sides hits blackjacks

        game.playerHand = new ArrayList<>();
        game.bankerHand = new ArrayList<>();


        game.playerHand.add(new Card("Spades", 1));
        game.playerHand.add(new Card("Spades", 11));
        game.bankerHand.add(new Card("Spades", 5));

        assertEquals(150.0, game.evaluateWinnings()); // when the player hits blackjack with Ace and a 10

        game.playerHand = new ArrayList<>();
        game.playerHand.add(new Card("Spades", 10));
        game.playerHand.add(new Card("Spades", 12));
        game.playerHand.add(new Card("Spades", 11));

        assertEquals(-100.0, game.evaluateWinnings()); // when the player busts

        game.playerHand = new ArrayList<>();
        game.playerHand.add(new Card("Spades", 10));
        game.playerHand.add(new Card("Spades", 12));
        game.bankerHand.add(new Card("Spades", 10));
        game.bankerHand.add(new Card("Spades", 10));

        assertEquals(100.0, game.evaluateWinnings()); //when the dealer busts

        game.playerHand.add(new Card("Spades", 4));

        assertEquals(-100.0, game.evaluateWinnings()); // when both sides busts

        game.currentBet = 1000.0;

        assertEquals(-1000.0, game.evaluateWinnings()); // when the bet amount changes
    }


}
