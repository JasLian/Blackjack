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

        dealer = null;
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
}
