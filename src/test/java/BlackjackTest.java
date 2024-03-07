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

    }

    @Test
    public void test_HandTotal(){

        ArrayList<Card> hand = new ArrayList<>();
        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        assertEquals(0, logicObj.handTotal(hand));

        hand.add(new Card("Hearts", 5));
        assertEquals(5, logicObj.handTotal(hand));

        int total = 5, val;
        for (int i = 0; i < 10; i++){
            val = (int)(Math.random() % 13) + 1;
            hand.add(new Card("Hearts", val));
            total += Math.min(val, 10);

            assertEquals(total, logicObj.handTotal(hand));
        }

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


}
