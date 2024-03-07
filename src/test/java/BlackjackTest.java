import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class BlackjackTest {

    @Test
    public void testCardClass(){

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
    public void testHandTotal(){

        ArrayList<Card> hand = new ArrayList<>();
        BlackjackGameLogic logicObj = new BlackjackGameLogic();

        assertEquals(0, logicObj.handTotal(hand));

        hand.add(new Card("Hearts", 5));
        assertEquals(5, logicObj.handTotal(hand));

        int total = 5, val;
        for (int i = 0; i < 10; i++){
            val = (int)(Math.random() % 10) + 1;
            hand.add(new Card("Hearts", val));
            total += val;

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


}
