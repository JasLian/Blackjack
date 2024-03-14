// BlackJack Game
// Written by Jason Liang
// Main.java:
// Contains all code used to make the GUI for playing a game of blackjack
// Image files of cards sourced from Google Code Archive: https://code.google.com/archive/p/vector-playing-cards/

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application{

    private BlackjackGame game;

    // javafx layout components
    HBox dealerSide, playerSide;

    Button startBtn, rulesBtn, startRoundBtn1, startRoundBtn2, hitBtn, stayBtn;
    Text errorMsg, endErrorMsg, balanceAndBet, result, newBalance, playMsg;
    TextField balanceInput, betInput, newBetInput;

    // Styling components for buttons
    BackgroundFill bgFill = new BackgroundFill(Color.rgb(20, 174, 92), null, null);
    BackgroundFill btnFill = new BackgroundFill(Color.rgb(69, 194, 128), new CornerRadii(20), null);
    Background btnBg = new Background(btnFill);

    BorderStroke btnStroke = new BorderStroke(Color.rgb(0, 121, 76), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(5));
    Border btnBorder = new Border(btnStroke);
    Background bg = new Background(bgFill);

    EventHandler<ActionEvent> returnToHome;

    PauseTransition roundOver = new PauseTransition(Duration.seconds(2));
    PauseTransition blackjack = new PauseTransition(Duration.millis(1250));

    ScrollPane scroll;

    // Hashmaps used to store scenes and certain messages
    HashMap<String, Scene> sceneMap = new HashMap<>();
    HashMap<String, String> resultMsg = new HashMap<>();

    // private variables to be used as constants
    private static double windowHeight;
    private static double windowWidth;

    private static int picHeight;
    private static int picWidth;

    public static void main(String[] args){
        launch(args);
    }

    // start function
    @Override
    public void start(Stage mainStage) throws Exception {

        // retrieve the monitor size and set constants
        Rectangle2D mainScreen = Screen.getPrimary().getVisualBounds();
        windowHeight = mainScreen.getHeight() * 0.9;
        windowWidth = mainScreen.getWidth() * 0.8;

        mainStage.setMinHeight(windowWidth);
        mainStage.setMinHeight(windowHeight);

        picWidth = (int) (windowWidth / 8.0);
        picHeight = (int) (windowHeight / 4.0);

        game = new BlackjackGame();

        // end messages after a round is finished
        resultMsg.put("player", "You Won!");
        resultMsg.put("dealer", "You Lost...");
        resultMsg.put("push", "Drawn");

        // PauseTransition when a round is over
        roundOver.setOnFinished(e->{
            if (game.totalWinnings <= 0.0){
                mainStage.setScene(sceneMap.get("lose"));
            }
            else{
                mainStage.setScene(sceneMap.get("end"));
            }
        });

        // PauseTransition when the player hits "blackjack"
        blackjack.setOnFinished(e->{

            stayBtn.fire();
        });

        // defines the EventHandler that returns to the start screen
        returnToHome = e -> {
            mainStage.setScene(sceneMap.get("start"));
        };


        // creates all scenes and stores them into the hashmap
        sceneMap.put("start", createStartScene());
        sceneMap.put("setup", createSetupScene());
        sceneMap.put("rules", createRulesScene());
        sceneMap.put("play", createPlayScene());
        sceneMap.put("end", createEndScene());
        sceneMap.put("lose", createLoseScene());

        //
        // Button events
        //

        startBtn.setOnAction(e ->{
            mainStage.setScene(sceneMap.get("setup"));
        });

        rulesBtn.setOnAction(e -> {
            mainStage.setScene(sceneMap.get("rules"));
            scroll.setVvalue(scroll.getVmin());
        });

        startRoundBtn1.setOnAction(e->{

            try{

                // balance and bet is read through parsing a TextField
                game.totalWinnings = Double.parseDouble(balanceInput.getText());

                String inputtedBet = betInput.getText();
                if (inputtedBet.equalsIgnoreCase("ALL")){
                    game.currentBet = game.totalWinnings;
                }
                else{
                    game.currentBet = Double.parseDouble(inputtedBet);
                }

                // if the bet cannot be used
                if (game.currentBet < 0.0 || game.totalWinnings < 0.0 || game.totalWinnings - game.currentBet < 0.0){
                    errorMsg.setText("The bet amount cannot be used, as it is either negative, OR your balance is insufficient");
                    return;
                }

                // recalculate the totalWinnings, and update the visual elements with the new amounts
                game.currentBet = Math.round(game.currentBet * 100) / 100.0;
                game.totalWinnings = Math.round(game.totalWinnings * 100) / 100.0;

                game.totalWinnings -= game.currentBet;

                balanceInput.setText("Enter a starting balance");
                betInput.setText("Enter a bet or \"All\" for all in");

                balanceAndBet.setText(String.format("Balance: $%,.2f\nBet: $%,.2f", game.totalWinnings, game.currentBet));
                newBetInput.setText(String.format("%.2f", game.currentBet));

                mainStage.setScene(sceneMap.get("play"));
                beginGame(); // setups the round

                checkBlackJack(); // checks for blackjack
            }
            catch (NumberFormatException ignored){ // catches all invalid text inputs for the balance and bet
                errorMsg.setText("Invalid value entered for balance and/or bet. Please try again.");
            }

        });

        balanceInput.setOnMouseClicked(e->{
            if (e.getButton().equals(MouseButton.PRIMARY)){
                balanceInput.setText("");
            }
        });

        betInput.setOnMouseClicked(e->{
            if (e.getButton().equals(MouseButton.PRIMARY)){
                betInput.setText("");
            }
        });

        hitBtn.setOnAction(e->{

            Card newCard = game.theDealer.drawOne();

            game.playerHand.add(newCard);
            addNewCardImage(newCard, playerSide);

            if (determineBust(game.playerHand)){ // everytime the player hits, check if they bust

                hitBtn.setDisable(true);
                stayBtn.setDisable(true);

                endRound(); // end the round
                roundOver.play(); // scene switch pause
            }

        });

        stayBtn.setOnAction(e->{

            hitBtn.setDisable(true);
            stayBtn.setDisable(true);

            dealerSide.getChildren().remove(1);
            addNewCardImage(game.bankerHand.get(1), dealerSide);

            // the dealer repeatedly draws, until bust or >= 17
            boolean bust = determineBust(game.bankerHand);
            while (game.gameLogic.evaluateBankerDraw(game.bankerHand) && !bust){
                Card newCard = game.theDealer.drawOne();
                game.bankerHand.add(newCard);
                addNewCardImage(newCard, dealerSide);
            }

            endRound(); // end the round
            roundOver.play(); // scene switch pause
        });

        startRoundBtn2.setOnAction(e->{

            // same method of reading in a new bet for the next round
            try{

                String inputtedBet = newBetInput.getText();
                if (inputtedBet.equalsIgnoreCase("ALL")){
                    game.currentBet = game.totalWinnings;
                }
                else{
                    game.currentBet = Double.parseDouble(inputtedBet);
                }

                if (game.currentBet < 0.0 || game.totalWinnings - game.currentBet < 0.0){
                    endErrorMsg.setText("The bet amount cannot be used, as it is either negative, OR your balance is not sufficient");
                    return;
                }

                game.currentBet = Math.round(game.currentBet * 100) / 100.0;

                game.totalWinnings -= game.currentBet;

                balanceAndBet.setText(String.format("Balance: $%,.2f\nBet: $%,.2f", game.totalWinnings, game.currentBet));
                newBetInput.setText(String.format("%.2f", game.currentBet));

                beginGame();
                mainStage.setScene(sceneMap.get("play"));

                checkBlackJack();

            }
            catch (NumberFormatException ignored){
                endErrorMsg.setText("Invalid value entered for balance and/or bet. Please try again.");
            }

        });

        newBetInput.setOnMouseClicked(e->{
            if (e.getButton().equals(MouseButton.PRIMARY)){
                newBetInput.setText("");
            }
        });


        // show the stage
        mainStage.setTitle("Blackjack");
        mainStage.setScene(sceneMap.get("start"));
        mainStage.setResizable(false);
        mainStage.show();
    }

    // function to create the start scene
    private Scene createStartScene(){

        BorderPane pane = new BorderPane();

        Text titleText = new Text("BLACKJACK");
        titleText.setStyle("-fx-font-size: 50;");

        startBtn = new Button("Start");
        styleButton(startBtn);

        rulesBtn = new Button("Rules");
        styleButton(rulesBtn);

        VBox v = new VBox(windowHeight / 24, titleText, startBtn, rulesBtn);
        v.setAlignment(Pos.CENTER);
        v.setStyle("-fx-font-size: 25;");

        pane.setCenter(v);
        pane.setPadding(new Insets(windowHeight / 12, windowWidth / 7.5, windowHeight / 2.4, windowWidth / 7.5));

        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, windowWidth, windowHeight);

    }

    // function to create the rules scene
    private Scene createRulesScene(){

        Text ruleTitle = new Text("How to Play");
        ruleTitle.setStyle("-fx-font-size: 40;");

        Text ruleText = new Text("• Objective: Beat the dealer's hand without going over 21.\n\n" +
                                    "• Dealing: Both the player and dealer are dealt two cards. The player will only know one of the dealer’s card\n\n" +
                                    "• Card Values: Number cards are worth their face value, face cards are worth 10, and Aces can be worth 1 or 11,depending on what is most helpful to the player\n\n"+
                                    "• Player's Turn: Players can choose to hit (take another card) or stand (keep their current hand).\n\n" +
                                    "• Dealer's Turn: The dealer reveals their face-down card after the player have finished their turn. They must hit until they reach 17 or higher. On hands 17 or higher, the dealer will stay\n\n" +
                                    "• Players win if their hand is closer to 21 than the dealer's without going over. If the player or dealer busts (exceeds 21), they lose\n\n" +
                                    "• If both sides have the same value hand, it is a draw and the round is over without any money changing hands.\n\n");
        ruleText.setStyle("-fx-font-size: 30;");
        ruleText.setWrappingWidth(900);

        Button rulesReturnBtn = new Button("Return");
        rulesReturnBtn.setOnAction(returnToHome);
        styleButton(rulesReturnBtn);

        VBox box = new VBox(windowHeight / 24, ruleTitle, ruleText, rulesReturnBtn);
        box.setAlignment(Pos.CENTER);

        BorderPane pane = new BorderPane();
        pane.setCenter(box);
        pane.setPadding(new Insets(windowHeight / 12, windowWidth / 7.5, windowHeight / 12, windowWidth / 7.5));
        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        scroll = new ScrollPane();
        scroll.setContent(pane);
        scroll.setFitToWidth(true);

        return new Scene(scroll, windowWidth, windowHeight);
    }

    // function to create the setup scene
    private Scene createSetupScene(){

        Text balanceText = new Text("Set Your Starting Amount");
        Text betText = new Text("Set Your Starting Bet");
        balanceText.setStyle("-fx-font-size: 40");
        betText.setStyle("-fx-font-size: 40");

        errorMsg = new Text();
        errorMsg.setStyle("-fx-font-size: 20");

        balanceInput = new TextField("Enter a starting balance");
        balanceInput.setMinSize(windowWidth / 4.5, windowHeight / 22);
        balanceInput.setMaxSize(windowWidth / 4.5, windowHeight / 22);
        balanceInput.setAlignment(Pos.CENTER);
        balanceInput.setStyle("-fx-font-size: 25");

        betInput = new TextField("Enter a bet or \"All\" for all in");
        betInput.setMinSize(windowWidth / 4, windowHeight / 22);
        betInput.setMaxSize(windowWidth / 4, windowHeight / 22);
        betInput.setAlignment(Pos.CENTER);
        betInput.setStyle("-fx-font-size: 25");

        BackgroundFill inputFill = new BackgroundFill(Color.rgb(175, 244, 198), new CornerRadii(5), null);
        Background inputBg = new Background(inputFill);
        balanceInput.setBackground(inputBg);
        betInput.setBackground(inputBg);

        BorderStroke inputStroke = new BorderStroke(Color.rgb(101, 191, 140), BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2));
        Border inputBorder = new Border(inputStroke);
        balanceInput.setBorder(inputBorder);
        betInput.setBorder(inputBorder);

        startRoundBtn1 = new Button("Start Game");
        styleButton(startRoundBtn1);

        Button setUpReturnBtn = new Button("Return");
        setUpReturnBtn.setOnAction(returnToHome);
        styleButton(setUpReturnBtn);

        VBox v1 = new VBox(windowHeight / 40, balanceText, balanceInput, betText, betInput);
        VBox v2 = new VBox(windowHeight / 24, startRoundBtn1, setUpReturnBtn, errorMsg);

        VBox v3 = new VBox(windowHeight / 20, v1, v2);
        v1.setAlignment(Pos.CENTER);
        v2.setAlignment(Pos.CENTER);
        v3.setAlignment(Pos.CENTER);


        BorderPane pane = new BorderPane();
        pane.setCenter(v3);
        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, windowWidth, windowHeight);
    }

    // function to create the play scene
    private Scene createPlayScene(){

        HBox upperRail = new HBox();
        upperRail.setMinHeight(windowHeight / 12);
        upperRail.setMaxHeight(windowHeight / 12);

        hitBtn = new Button("Hit");
        stayBtn = new Button("Stay");

        hitBtn.setMinSize(windowWidth / 15, windowHeight / 24);
        stayBtn.setMinSize(windowWidth / 15, windowHeight / 24);

        BackgroundFill controlFill = new BackgroundFill(Color.rgb(230, 230, 230), new CornerRadii(10), null);
        Background controlBg = new Background(controlFill);
        hitBtn.setBackground(controlBg);
        stayBtn.setBackground(controlBg);

        HBox buttonBox = new HBox(150, hitBtn, stayBtn);
        buttonBox.setAlignment(Pos.CENTER);

        balanceAndBet = new Text();
        balanceAndBet.setFill(Color.WHITE);
        balanceAndBet.setStyle("-fx-font-size: 25");
        balanceAndBet.setTextAlignment(TextAlignment.LEFT);

        HBox textBox = new HBox(balanceAndBet);
        textBox.setAlignment(Pos.CENTER);

        BorderPane lowerRail = new BorderPane();
        lowerRail.setCenter(buttonBox);
        lowerRail.setPadding(new Insets(0, windowWidth / 6.5, 0, 5));
        lowerRail.setLeft(textBox);
        lowerRail.setMinHeight(windowHeight / 12);
        lowerRail.setMaxHeight(windowHeight / 12);

        dealerSide = new HBox(windowWidth / 75);
        playerSide = new HBox(windowWidth / 75);
        dealerSide.setAlignment(Pos.CENTER);
        playerSide.setAlignment(Pos.CENTER);

        playMsg = new Text();
        playMsg.setStyle("-fx-font-size: 30;");
        playMsg.setTextAlignment(TextAlignment.CENTER);

        VBox table = new VBox(windowHeight / 12, dealerSide, playMsg, playerSide);
        table.setAlignment(Pos.CENTER);

        BorderPane centerPane = new BorderPane();
        centerPane.setCenter(table);
        centerPane.setPadding(new Insets(windowHeight / 20, 0, windowHeight / 24, 0));

        BackgroundFill tableRailFill = new BackgroundFill(Color.rgb(125,73, 35), null, null);
        Background tableRailBg = new Background(tableRailFill);
        upperRail.setBackground(tableRailBg);
        lowerRail.setBackground(tableRailBg);

        BorderPane pane = new BorderPane();
        pane.setTop(upperRail);
        pane.setCenter(centerPane);
        pane.setBottom(lowerRail);

        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman';" + "-fx-font-size: 20;");

        return new Scene(pane, windowWidth, windowHeight);
    }

    // function to create the round end scene
    private Scene createEndScene(){

        result = new Text();
        result.setStyle("-fx-font-size: 40;");

        endErrorMsg = new Text();
        endErrorMsg.setStyle("-fx-font-size: 20");

        Text continueMsg1 = new Text("Keep Playing?");
        Text continueMsg2 = new Text("Change your bet, or continue with the same bet");

        newBalance = new Text();

        newBetInput = new TextField();
        newBetInput.setMinSize(windowWidth / 4.5, windowHeight / 22);
        newBetInput.setMaxSize(windowWidth / 4.5, windowHeight / 22);
        newBetInput.setAlignment(Pos.CENTER);

        BackgroundFill inputFill = new BackgroundFill(Color.rgb(175, 244, 198), new CornerRadii(5), null);
        Background inputBg = new Background(inputFill);
        newBetInput.setBackground(inputBg);

        startRoundBtn2 = new Button("Continue");
        styleButton(startRoundBtn2);

        Button endGame = new Button("Home");
        endGame.setOnAction(returnToHome);
        styleButton(endGame);

        VBox textBox = new VBox(windowHeight / 60, continueMsg1, newBalance, continueMsg2, newBetInput);
        textBox.setAlignment(Pos.CENTER);
        textBox.setStyle("-fx-font-size: 25;");

        VBox container = new VBox(windowHeight / 24, result, textBox, startRoundBtn2, endGame, endErrorMsg);
        container.setAlignment(Pos.CENTER);

        BorderPane pane = new BorderPane();
        pane.setCenter(container);
        pane.setPadding(new Insets(0, windowWidth / 7.5, 0, windowWidth / 7.5));
        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman';" + "-fx-font-size: 25;");

        return new Scene(pane, windowWidth, windowHeight);
    }

    // method to create the lose screen
    private Scene createLoseScene(){

        Text loseTitle = new Text("YOU'VE LOST YOUR ENTIRE BALANCE");
        loseTitle.setStyle("-fx-font-size: 40");

        Text msg = new Text("To keep playing, return to the start");
        msg.setStyle("-fx-font-size: 30");

        Button endGame = new Button("Home");
        endGame.setOnAction(returnToHome);
        styleButton(endGame);

        VBox v1 = new VBox(windowHeight / 50, msg, endGame);
        v1.setAlignment(Pos.CENTER);

        VBox v2 = new VBox(windowHeight / 10, loseTitle, v1);
        v2.setAlignment(Pos.CENTER);

        BorderPane pane = new BorderPane();
        pane.setCenter(v2);
        pane.setPadding(new Insets(0, 0, 0, 0));
        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman';" + "-fx-font-size: 25;");

        return new Scene(pane, windowWidth, windowHeight);
    }

    // method that adds a new card to the player's or dealer's side
    // takes a Card object and a HBox as parameters
    private void addNewCardImage(Card newCard, HBox side){

        // create an Image obj from the card
        String imageFile = newCard.value + "_" + newCard.suit + ".png";
        Image image = new Image(imageFile);

        // create an ImageView obj and adds it to either side
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(picHeight);
        imageView.setFitWidth(picWidth);

        side.getChildren().add(imageView);

    }

    // method clears all cards from the table and deals 2 hands to the player and dealer
    private void beginGame(){

        // reenable the hit and stay buttons
        hitBtn.setDisable(false);
        stayBtn.setDisable(false);

        // clear the table
        playMsg.setText("");
        playerSide.getChildren().clear();
        dealerSide.getChildren().clear();

        game.theDealer.generateDeck();

        // deals both sides their hands and displays the cards
        game.playerHand = game.theDealer.dealHand();
        game.bankerHand = game.theDealer.dealHand();

        addNewCardImage(game.bankerHand.get(0), dealerSide);

        Image backImage = new Image("back.png");
        ImageView viewBackImage = new ImageView(backImage);
        viewBackImage.setFitHeight(picHeight);
        viewBackImage.setFitWidth(picWidth);

        dealerSide.getChildren().add(viewBackImage);

        for (Card card : game.playerHand){
            addNewCardImage(card, playerSide);
        }

    }

    // method determines if a hand busts
    private boolean determineBust(ArrayList<Card> hand){
        return game.gameLogic.handTotal(hand) > 21;
    }

    // method ends the round by calculating the winnings, and set the corresponding end messages
    private void endRound(){

        double winnings = game.evaluateWinnings();

        if (winnings >= 0.0){
            game.totalWinnings += winnings + game.currentBet;
        }

        newBalance.setText(String.format("Current Balance: $%,.2f", game.totalWinnings));

        String winner = game.gameLogic.whoWon(game.playerHand, game.bankerHand);
        result.setText(resultMsg.get(winner));

        String totalCmp = "(Player) " + game.gameLogic.handTotal(game.playerHand) + " -- " + game.gameLogic.handTotal(game.bankerHand) + " (Dealer)";
        if (winner.equals("player")){
            playMsg.setText("Player Wins\n" + totalCmp);
        }
        else if (winner.equals("push")){
            playMsg.setText("Push\n" + totalCmp);
        }
        else{
            playMsg.setText("House Wins\n" + totalCmp);
        }

    }

    // method styles a button
    private void styleButton(Button btn){
        btn.setMinSize(windowWidth / 10, windowHeight / 20);
        btn.setMaxSize(windowWidth / 8, windowHeight / 10);
        btn.setBackground(btnBg);
        btn.setBorder(btnBorder);
        btn.setStyle("-fx-font-size: 25");
    }

    // method checks if the player has hit blackjack
    private void checkBlackJack(){
        if (game.gameLogic.handTotal(game.playerHand) == 21){
            playMsg.setText("BLACKJACK!");
            blackjack.play(); // immediately ends the player's turn and switch to the dealer
        }
    }
}
