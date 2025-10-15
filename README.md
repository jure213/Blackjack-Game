# Maturitetna-seminarska-naloga

Maturitetna seminarska naloga

Igra Blackjack

Avtor: Jure Rus, T4C

## Prerequisites

- Java 21 (OpenJDK)
- Maven 3.x

## Project Structure

``` bash
Blackjack-Game/
├── src/
│   └── main/
│       ├── java/
│       │   └── application/
│       │       ├── BlackjackGame.java
│       │       ├── Card.java
│       │       ├── Deck.java
│       │       └── Hand.java
│       └── resources/
│           └── application/
│               └── cards2/          # Card images
├── pom.xml                          # Maven configuration
└── README.md
```

## How to Run

### Using Maven (Recommended)

```bash
mvn clean javafx:run
```

### Build JAR

```bash
mvn clean package
```

## Game Features

- Classic Blackjack gameplay
- **Betting System**: Start with $1000 and place bets each round
- **Smooth Card Animations**: Cards pop in with scale and fade effects
- Graphical card display with high-quality card images
- Deal, Hit, and Stand actions
- Automatic dealer logic (dealer hits until 17)
- Win/Loss detection with balance tracking
- Blackjack detection (21 with first two cards pays 3:2)
- Real-time balance updates

## How to Play

1. Enter a bet amount in the text field
2. Click "PLACE BET" to lock in your bet
3. Click "DEAL" to start the round
4. Choose "HIT" to draw another card or "STAND" to stay
5. Watch the smooth card animations as cards appear!
6. Win 2x your bet for regular wins, 2.5x for Blackjack!

## Technologies Used

- Java 21
- JavaFX 21.0.1
- Maven for dependency management
