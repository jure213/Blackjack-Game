package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class BlackjackGame extends Application {

    // deklaracija objektov, ki nastopajo
    private Deck deck;
    private Hand dealerHand;
    private Hand playerHand;

    // Betting system variables
    private double playerBalance = 1000.0;
    private double currentBet = 0.0;
    private boolean dealerCardHidden = true; // Track if dealer's second card is hidden

    // deklaracija grafičnih elementov
    private VBox leftStackPane;
    private StackPane rightStackPane;
    private Button dealButton, hitButton, standButton;
    private HBox dealerCards, playerCards;
    private Label dealerScore, playerScore, messageLabel, title;
    private Label balanceLabel;
    private TextField betInput;
    private Button placeBetButton;

    // metoda main()
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();

        Scene startScene = new Scene(createStartLayout(primaryStage), 800, 600);
        primaryStage.setTitle("Blackjack Game");
        primaryStage.setScene(startScene);
        primaryStage.show();

        // Don't start game automatically - wait for bet to be placed
    }

    private void initialize() {

        leftStackPane = new VBox(20);
        rightStackPane = new StackPane();

        // deklaracija tekstovnih polj
        dealerScore = new Label();
        playerScore = new Label();
        messageLabel = new Label();
        title = new Label("BLACKJACK");
        balanceLabel = new Label("Balance: $" + String.format("%.2f", playerBalance));

        dealerCards = new HBox(10);
        playerCards = new HBox(10);

        // Betting controls
        betInput = new TextField();
        betInput.setPromptText("Enter bet amount");
        betInput.setMaxWidth(150);
        placeBetButton = new Button("PLACE BET");
        placeBetButton.setOnAction(this::placeBet);

        // dodajanje elementov na levo ploščo
        leftStackPane.getChildren().addAll(dealerScore, dealerCards, playerCards, playerScore, messageLabel);
        leftStackPane.setAlignment(Pos.CENTER);

        // inicializacija gumbov
        dealButton = new Button("DEAL");
        hitButton = new Button("HIT");
        standButton = new Button("STAND");

        dealButton.setOnAction(this::dealCards);
        hitButton.setOnAction(this::hit);
        standButton.setOnAction(this::stand);

        // dodajanje elementov na desno ploščo
        VBox bettingControls = new VBox(10, balanceLabel, betInput, placeBetButton);
        bettingControls.setAlignment(Pos.CENTER);
        
        VBox buttons = new VBox(20, dealButton, hitButton, standButton);
        buttons.setAlignment(Pos.CENTER);
        
        VBox rightControls = new VBox(40, bettingControls, buttons);
        rightControls.setAlignment(Pos.CENTER);
        rightStackPane.getChildren().add(rightControls);

        // urejanje izgleda elementov s CSS
        rightStackPane.setPadding(new Insets(0, 20, 0, 0));
        dealButton.setStyle(
                "-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        hitButton.setStyle(
                "-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        standButton.setStyle(
                "-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        placeBetButton.setStyle(
                "-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-size: 16; -fx-font-weight: bold;");
        title.setStyle("-fx-font-size: 36; -fx-text-fill: black; -fx-font-weight: bold;");
        dealerScore.setStyle("-fx-font-size: 13; -fx-text-fill: black; -fx-font-weight: bold;");
        playerScore.setStyle("-fx-font-size: 13; -fx-text-fill: black; -fx-font-weight: bold;");
        messageLabel.setStyle("-fx-font-size: 20; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 0 0 0 20;");
        balanceLabel.setStyle("-fx-font-size: 18; -fx-text-fill: white; -fx-font-weight: bold;");
        betInput.setStyle("-fx-font-size: 14;");
        
        // Initially disable game buttons until bet is placed
        disableDealButton();
        disableGameButtons();
    }

    private BorderPane createGameLayout() {

        BorderPane gameLayout = new BorderPane();

        gameLayout.setLeft(leftStackPane);
        gameLayout.setRight(rightStackPane);

        gameLayout.setStyle("-fx-background-color: #28734d;");
        gameLayout.setBottom(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        return gameLayout;
    }

    private void startNewGame() {

        // deljenje prvih kart
        deck = new Deck();
        dealerHand = new Hand();
        playerHand = new Hand();
        dealerCardHidden = true; // Hide dealer's second card
        
        playerHand.addCard(deck.dealCard());
        dealerHand.addCard(deck.dealCard());
        playerHand.addCard(deck.dealCard());
        dealerHand.addCard(deck.dealCard());
        disableDealButton();
        disableBettingControls();

        updateView();

        // preverimo ali je 21 že po prvih dveh kartah
        if (playerHand.getValue() == 21) {
            // Reveal dealer card for blackjack
            dealerCardHidden = false;
            updateCardDisplay(dealerCards, dealerHand, false);
            dealerScore.setText("Dealer Score: " + dealerHand.getValue());
            
            double winnings = currentBet * 2.5; // Blackjack pays 3:2 (bet + 1.5x bet = 2.5x bet)
            playerBalance += winnings;
            updateBalance();
            displayMessage("BLACKJACK! You win $" + String.format("%.2f", winnings - currentBet) + "!");
            disableGameButtons();
            enableDealButton();
            enableBettingControls();
        }
    }

    private void updateView() {

        // posodobitev igralnih kart
        updateCardDisplay(dealerCards, dealerHand, true);
        updateCardDisplay(playerCards, playerHand, true);

        // prikaz trenutnih točk za igralca in delivca
        if (dealerCardHidden && dealerHand.getCards().length >= 2) {
            // Show only first card value when second card is hidden
            Card firstCard = dealerHand.getCards()[0];
            dealerScore.setText("Dealer Score: " + firstCard.getFaceValue() + " + ?");
        } else {
            dealerScore.setText("Dealer Score: " + dealerHand.getValue());
        }
        playerScore.setText("Player Score: " + playerHand.getValue());

        messageLabel.setText("");
        // omogočamo funkiconalnost igralnih gumbov
        enableGameButtons();
    }

    private void updateCardDisplay(HBox cardBox, Hand hand, boolean animate) {

        cardBox.getChildren().clear();

        Region padding = new Region();
        padding.setMinWidth(20);
        cardBox.getChildren().add(padding);

        Card[] cards = hand.getCards();
        boolean isDealerHand = (cardBox == dealerCards);
        
        for (int i = 0; i < cards.length; i++) {
            Card card = cards[i];
            
            // Show card back for dealer's second card if hidden
            String imagePath;
            if (isDealerHand && i == 1 && dealerCardHidden) {
                imagePath = "cards2/card_back.svg.png";
            } else {
                imagePath = "cards2/" + card.getImageFilename();
            }
            
            ImageView cardImage = new ImageView(
                    new Image((getClass().getResourceAsStream(imagePath))));

            // určimo velikost kart na zaslonu
            cardImage.setFitWidth(140);
            cardImage.setFitHeight(210);

            if (animate) {
                // Set initial state for animation
                cardImage.setScaleX(0.1);
                cardImage.setScaleY(0.1);
                cardImage.setOpacity(0.0);

                // Create scale transition
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), cardImage);
                scaleTransition.setFromX(0.1);
                scaleTransition.setFromY(0.1);
                scaleTransition.setToX(1.0);
                scaleTransition.setToY(1.0);

                // Create fade transition
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), cardImage);
                fadeTransition.setFromValue(0.0);
                fadeTransition.setToValue(1.0);

                // Combine animations
                ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
                parallelTransition.setDelay(Duration.millis(i * 200)); // Stagger animations
                parallelTransition.play();
            }

            cardBox.getChildren().add(cardImage);
        }
    }

    private void addSingleCardWithAnimation(HBox cardBox, Card card) {
        ImageView cardImage = new ImageView(
                new Image((getClass().getResourceAsStream("cards2/" + card.getImageFilename()))));

        // določimo velikost kart na zaslonu
        cardImage.setFitWidth(140);
        cardImage.setFitHeight(210);

        // Set initial state for animation
        cardImage.setScaleX(0.1);
        cardImage.setScaleY(0.1);
        cardImage.setOpacity(0.0);

        // Create scale transition
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), cardImage);
        scaleTransition.setFromX(0.1);
        scaleTransition.setFromY(0.1);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        // Create fade transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), cardImage);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        // Combine animations
        ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
        parallelTransition.play();

        cardBox.getChildren().add(cardImage);
    }

    private void displayMessage(String message) {
        messageLabel.setText(message);
    }

    private void disableGameButtons() {
        // onemogoči gumba hit in stand
        hitButton.setDisable(true);
        standButton.setDisable(true);
    }

    private void enableGameButtons() {
        // omogoči gumba hit in stand
        hitButton.setDisable(false);
        standButton.setDisable(false);
    }

    private void disableDealButton() {
        // onemogoči gumb deal
        dealButton.setDisable(true);
    }

    private void enableDealButton() {
        // omogoči gumb deal
        dealButton.setDisable(false);
    }

    private void dealCards(ActionEvent event) {
        startNewGame();
    }

    private void hit(ActionEvent event) {
        Card newCard = deck.dealCard();
        playerHand.addCard(newCard);
        addSingleCardWithAnimation(playerCards, newCard);
        playerScore.setText("Player Score: " + playerHand.getValue());

        if (playerHand.getValue() > 21) {
            // Reveal dealer's hidden card
            dealerCardHidden = false;
            updateCardDisplay(dealerCards, dealerHand, false);
            dealerScore.setText("Dealer Score: " + dealerHand.getValue());
            
            // Bet already deducted, just show loss message
            updateBalance();
            displayMessage("You busted! Lost $" + String.format("%.2f", currentBet));
            disableGameButtons();
            enableDealButton();
            enableBettingControls();
        }
    }

    private void stand(ActionEvent event) {
        disableGameButtons();
        
        // First, reveal the dealer's hidden card with animation
        dealerCardHidden = false;
        
        // Redraw dealer cards to reveal the second card
        updateCardDisplay(dealerCards, dealerHand, false);
        dealerScore.setText("Dealer Score: " + dealerHand.getValue());
        
        // Wait a moment before dealer draws more cards
        PauseTransition pause = new PauseTransition(Duration.millis(600));
        pause.setOnFinished(e -> {
            // Now dealer draws additional cards if needed
            drawDealerCardsSequentially(0);
        });
        pause.play();
    }
    
    private void drawDealerCardsSequentially(int cardIndex) {
        if (dealerHand.getValue() >= 17) {
            // Dealer is done drawing, determine winner
            determineWinner();
            return;
        }
        
        // Draw one card
        Card newCard = deck.dealCard();
        dealerHand.addCard(newCard);
        addSingleCardWithAnimation(dealerCards, newCard);
        dealerScore.setText("Dealer Score: " + dealerHand.getValue());
        
        // Wait before drawing next card
        PauseTransition pause = new PauseTransition(Duration.millis(800));
        pause.setOnFinished(e -> drawDealerCardsSequentially(cardIndex + 1));
        pause.play();
    }
    
    private void determineWinner() {
        // Determine winner and update balance
        if (dealerHand.getValue() > 21) {
            // Dealer busts - player wins (return bet + winnings = 2x bet)
            playerBalance += currentBet * 2;
            updateBalance();
            displayMessage("Dealer busted! You win $" + String.format("%.2f", currentBet) + "!");
        } else if (dealerHand.getValue() > playerHand.getValue()) {
            // Dealer wins - bet already deducted
            updateBalance();
            displayMessage("Dealer wins! Lost $" + String.format("%.2f", currentBet));
        } else if (dealerHand.getValue() < playerHand.getValue()) {
            // Player wins - return bet + winnings = 2x bet
            playerBalance += currentBet * 2;
            updateBalance();
            displayMessage("You win $" + String.format("%.2f", currentBet) + "!");
        } else {
            // Push - return bet only
            playerBalance += currentBet;
            updateBalance();
            displayMessage("It's a draw! Bet returned.");
        }

        enableDealButton();
        enableBettingControls();
    }

    private void placeBet(ActionEvent event) {
        try {
            double betAmount = Double.parseDouble(betInput.getText());
            
            if (betAmount <= 0) {
                displayMessage("Bet must be greater than 0!");
                return;
            }
            
            if (betAmount > playerBalance) {
                displayMessage("Insufficient balance!");
                return;
            }
            
            // Deduct bet from balance immediately
            currentBet = betAmount;
            playerBalance -= currentBet;
            updateBalance();
            displayMessage("Bet placed: $" + String.format("%.2f", currentBet));
            enableDealButton();
            betInput.clear();
            
        } catch (NumberFormatException e) {
            displayMessage("Please enter a valid bet amount!");
        }
    }

    private void updateBalance() {
        balanceLabel.setText("Balance: $" + String.format("%.2f", playerBalance));
        
        if (playerBalance <= 0) {
            displayMessage("Game Over! You're out of money.");
            disableDealButton();
            disableGameButtons();
            disableBettingControls();
        }
    }

    private void disableBettingControls() {
        betInput.setDisable(true);
        placeBetButton.setDisable(true);
    }

    private void enableBettingControls() {
        betInput.setDisable(false);
        placeBetButton.setDisable(false);
        currentBet = 0.0; // Reset bet after round
        disableDealButton(); // Need to place new bet
    }

    private VBox createStartLayout(Stage primaryStage) {
        VBox startLayout = new VBox(20);
        startLayout.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("WELCOME TO BLACKJACK");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button startButton = new Button("Start Game");

        Image firstImage = new Image(getClass().getResourceAsStream("cards2/s_ace.png"));
        Image secondImage = new Image(getClass().getResourceAsStream("cards2/h_queen.png"));

        ImageView firstImageView = new ImageView(firstImage);
        ImageView secondImageView = new ImageView(secondImage);
        firstImageView.setFitHeight(240);
        firstImageView.setFitWidth(180);
        secondImageView.setFitHeight(240);
        secondImageView.setFitWidth(180);

        firstImageView.setRotate(15);
        secondImageView.setRotate(-15);

        HBox imageContainer = new HBox(10);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.getChildren().addAll(firstImageView, secondImageView);

        startLayout.getChildren().addAll(imageContainer, welcomeLabel, startButton);

        startLayout.setStyle("-fx-background-color: #28734d");
        startButton.setStyle(
                "-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        welcomeLabel.setPadding(new Insets(30, 0, 0, 0));

        startButton.setOnAction(event -> {
            Scene gameScene = new Scene(createGameLayout(), 800, 600);
            primaryStage.setScene(gameScene);
        });

        return startLayout;
    }
}