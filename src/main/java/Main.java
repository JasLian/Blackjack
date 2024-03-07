public class Main {

    public static void main(String[] args){

        BlackjackDealer dealer = new BlackjackDealer();
        dealer.generateDeck();

        System.out.println("The deck is: ");

        for (Card card : dealer.deck){
            System.out.println(card.suit + " " + card.value);
        }
    }
}
