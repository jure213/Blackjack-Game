package application;

public class Card {

	public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getFaceValue() {
        switch (rank) {
            case ACE:
                return 11;
            case KING:
            case QUEEN:
            case JACK:
            case TEN:
                return 10;
            case NINE:
                return 9;
            case EIGHT:
                return 8;
            case SEVEN:
                return 7;
            case SIX:
                return 6;
            case FIVE:
                return 5;
            case FOUR:
                return 4;
            case THREE:
                return 3;
            case TWO:
                return 2;
            default:
                throw new IllegalArgumentException("Invalid card rank");
        }
    }

    public String getImageFilename() {
        String suitInitial = suit.name().substring(0, 1).toLowerCase();
        String rankName = rank.name().toLowerCase();
        return suitInitial + "_" + rankName + ".png";
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
	
}