package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class BlackjackGame extends Application {

		//deklaracija objektov, ki nastopajo
	 	private Deck deck;
	 	private Hand dealerHand;
	    private Hand playerHand;
	    
	    //deklaracija grafičnih elementov
	    private VBox leftStackPane;
	    private StackPane rightStackPane;
	    private Button dealButton, hitButton, standButton;
	    private HBox dealerCards, playerCards;
	    private Label dealerScore, playerScore, messageLabel, title;
	
	    //metoda main()
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

	    	startNewGame();
	    }
    
    private void initialize() {
    	
        leftStackPane = new VBox(20);
        rightStackPane = new StackPane();

        //deklaracija tekstovnih polj
        dealerScore = new Label();
        playerScore = new Label();
        messageLabel = new Label();
        title = new Label("BLACKJACK");
                   
        dealerCards = new HBox(10);
        playerCards = new HBox(10);
        
        //dodajanje elementov na levo ploščo
        leftStackPane.getChildren().addAll(dealerScore, dealerCards, playerCards, playerScore, messageLabel);
        leftStackPane.setAlignment(Pos.CENTER);
        
        //inicializacija gumbov 
        dealButton = new Button("DEAL");
        hitButton = new Button("HIT");
        standButton = new Button("STAND");

        dealButton.setOnAction(this::dealCards);
        hitButton.setOnAction(this::hit);
        standButton.setOnAction(this::stand);

        //dodajanje elementov na desno ploščo
        VBox buttons = new VBox(20, dealButton, hitButton, standButton);
        buttons.setAlignment(Pos.CENTER);
        rightStackPane.getChildren().add(buttons);
        
        //urejanje izgleda elementov s CSS
        rightStackPane.setPadding(new Insets(0, 20, 0, 0)); 
        dealButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        hitButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        standButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        title.setStyle("-fx-font-size: 36; -fx-text-fill: black; -fx-font-weight: bold;");
        dealerScore.setStyle("-fx-font-size: 13; -fx-text-fill: black; -fx-font-weight: bold;");
        playerScore.setStyle("-fx-font-size: 13; -fx-text-fill: black; -fx-font-weight: bold;");
        messageLabel.setStyle("-fx-font-size: 20; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 0 0 0 20;");
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
    	
    		//deljenje prvih kart
    	    deck = new Deck();
    	    dealerHand = new Hand();
    	    playerHand = new Hand();
    	    playerHand.addCard(deck.dealCard());
    	    dealerHand.addCard(deck.dealCard());
    	    playerHand.addCard(deck.dealCard());
    	    dealerHand.addCard(deck.dealCard());
    	    disableDealButton();

    	    updateView();

    	    //preverimo ali je 21 že po prvih dveh kartah
    	    if (playerHand.getValue() == 21) {
    	        displayMessage("Congratulations! You got Blackjack and a win!");
    	        disableGameButtons();
    	        enableDealButton();
    	    }
    }

    private void updateView() {
    	
    	//posodobitev igralnih kart
        updateCardDisplay(dealerCards, dealerHand);
        updateCardDisplay(playerCards, playerHand);

        //prikaz treuntih točk za igralca in delivca
        dealerScore.setText("Dealer Score: " + dealerHand.getValue());
        playerScore.setText("Player Score: " + playerHand.getValue());

        messageLabel.setText("");
        //omogočamo funkiconalnost igralnih gumbov
        enableGameButtons();   
    }
    
    private void updateCardDisplay(HBox cardBox, Hand hand) {
    	
        cardBox.getChildren().clear();
        
        Region padding = new Region();
        padding.setMinWidth(20); 
        cardBox.getChildren().add(padding);
        
        for (Card card : hand.getCards()) {
            ImageView cardImage = new ImageView(new Image((getClass().getResourceAsStream("cards2/" + card.getImageFilename()))));

           //določimo velikost kart na zaslonu
            cardImage.setFitWidth(140); 
            cardImage.setFitHeight(210); 

            cardBox.getChildren().add(cardImage);
        }
    }


    private void displayMessage(String message) {
        messageLabel.setText(message);
    }

    private void disableGameButtons() {
    	//onemogoči gumba hit in stand
        hitButton.setDisable(true);
        standButton.setDisable(true);
    }

    private void enableGameButtons() {
    	//omogoči gumba hit in stand
        hitButton.setDisable(false);
        standButton.setDisable(false);
    }
    
    private void disableDealButton() {
    	//onemogoči gumb deal
    	dealButton.setDisable(true);
    }
    
    private void enableDealButton() {
    	//omogoči gumb deal
    	dealButton.setDisable(false);
    }

    private void dealCards(ActionEvent event) {
        startNewGame();
    }

    private void hit(ActionEvent event) {
        playerHand.addCard(deck.dealCard());
        enableDealButton();
        updateView();

        if (playerHand.getValue() > 21) {
            displayMessage("You busted! Dealer wins.");
            disableGameButtons();
        }
    }

    private void stand(ActionEvent event) {
    	enableDealButton();
        while (dealerHand.getValue() < 17) {
            dealerHand.addCard(deck.dealCard());
        }

        updateView();

        if (dealerHand.getValue() > 21) {
            displayMessage("Dealer busted! You win.");
        } else if (dealerHand.getValue() > playerHand.getValue()) {
            displayMessage("Dealer wins!");
        } else if (dealerHand.getValue() < playerHand.getValue()) {
            displayMessage("You win!");
        } else {
            displayMessage("It's a draw!");
        }

        disableGameButtons();
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
        startButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        welcomeLabel.setPadding(new Insets(30, 0, 0, 0));
       
        startButton.setOnAction(event -> {
            Scene gameScene = new Scene(createGameLayout(), 800, 600);
            primaryStage.setScene(gameScene);
        });
        
        return startLayout;
    }
}