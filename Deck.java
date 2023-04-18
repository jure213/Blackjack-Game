package application;

public class Deck {

	private Card[] cards;
    private int topCardIndex;

    public Deck() {
        initialize();
        shuffle();
    }

    private void initialize() {
        cards = new Card[52];
        topCardIndex = 0;
        int index = 0;

        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards[index++] = new Card(suit, rank);
            }
        }
    }

    public void shuffle() {
        for (int i = cards.length - 1; i > 0; i--) {
            int randIndex = (int) (Math.random() * (i + 1));
            Card temp = cards[i];
            cards[i] = cards[randIndex];
            cards[randIndex] = temp;
        }
        topCardIndex = 0;
    }

    public Card dealCard() {
        if (topCardIndex >= cards.length) {
            throw new IllegalStateException("No more cards left in the deck");
        }
        return cards[topCardIndex++];
    }
}
