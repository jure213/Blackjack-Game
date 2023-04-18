package application;

public class Hand {

	  private static final int MAX_CARDS = 11; //teoretično največje možno število kart

	    private Card[] cards;
	    private int cardCount;

	    public Hand() {
	        cards = new Card[MAX_CARDS];
	        cardCount = 0;
	    }

	    public void addCard(Card card) {
	        if (cardCount >= MAX_CARDS) {
	            throw new IllegalStateException("Cannot add more cards to the hand");
	        }
	        cards[cardCount++] = card;
	    }

	    public int getValue() {
	        int value = 0;
	        int aceCount = 0;

	        for (int i = 0; i < cardCount; i++) {
	            Card card = cards[i];
	            int cardValue = card.getFaceValue();
	            if (cardValue == 11) {
	                aceCount++;
	            }
	            value += cardValue;
	        }
	        while (value > 21 && aceCount > 0) {
	            value -= 10;
	            aceCount--;
	        }
	        return value;
	    }

	    public boolean isBust() {
	        return getValue() > 21;
	    }

	    

	    public Card[] getCards() {
	        Card[] cardCopy = new Card[cardCount];
	        System.arraycopy(cards, 0, cardCopy, 0, cardCount);
	        return cardCopy;
	    }
}
