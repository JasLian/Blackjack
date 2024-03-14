import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application{

    private BlackjackGame game;

    HBox dealerSide, playerSide;

    Button startBtn, rulesBtn, startRoundBtn1, startRoundBtn2, hitBtn, stayBtn;
    Text errorMsg, endErrorMsg, balanceAndBet, result, newBalance;
    TextField balanceInput, betInput, newBetInput;
    BackgroundFill bgFill = new BackgroundFill(Color.rgb(20, 174, 92), null, null);
    BackgroundFill btnFill = new BackgroundFill(Color.rgb(69, 194, 128), new CornerRadii(20), null);
    Background btnBg = new Background(btnFill);

    BorderStroke btnStroke = new BorderStroke(Color.rgb(0, 121, 76), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(5));
    Border btnBorder = new Border(btnStroke);
    Background bg = new Background(bgFill);

    EventHandler<ActionEvent> returnToHome;

    PauseTransition roundOverPause = new PauseTransition(Duration.seconds(5));

    HashMap<String, Scene> sceneMap = new HashMap<>();
    HashMap<String, String> resultMsg = new HashMap<>();

    private static double windowHeight;
    private static double windowWidth;

    private static int picHeight;
    private static int picWidth;

    String winner;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {

        Rectangle2D mainScreen = Screen.getPrimary().getVisualBounds();
        windowHeight = mainScreen.getHeight() * 0.9;
        windowWidth = mainScreen.getWidth() * 0.8;

        mainStage.setMinHeight(windowWidth);
        mainStage.setMinHeight(windowHeight);

        picWidth = (int) (windowWidth / 8.0);
        picHeight = (int) (windowHeight / 4.0);

        game = new BlackjackGame();

        resultMsg.put("player", "You Won!");
        resultMsg.put("dealer", "You Lost...");
        resultMsg.put("push", "Drawn");

        roundOverPause.setOnFinished(e->{
            System.out.println("round");
            if (game.totalWinnings <= 0.0){
                mainStage.setScene(sceneMap.get("lose"));
            }
            else{
                mainStage.setScene(sceneMap.get("end"));
            }
        });

        returnToHome = e -> {
            mainStage.setScene(sceneMap.get("start"));
        };


        sceneMap.put("start", createStartScene());
        sceneMap.put("setup", createSetupScene());
        sceneMap.put("rules", createRulesScene());
        sceneMap.put("play", createPlayScene());
        sceneMap.put("end", createEndScene());
        sceneMap.put("lose", createLoseScene());

        startBtn.setOnAction(e ->{
            mainStage.setScene(sceneMap.get("setup"));
        });

        rulesBtn.setOnAction(e -> {
            mainStage.setScene(sceneMap.get("rules"));
        });

        startRoundBtn1.setOnAction(e->{
            try{

                game.totalWinnings = Double.parseDouble(balanceInput.getText());
                game.currentBet = Double.parseDouble(betInput.getText());

                if (game.currentBet < 0.0 || game.totalWinnings < 0.0 || game.totalWinnings - game.currentBet < 0.0){
                    errorMsg.setText("The bet amount cannot be used, as it is either negative, OR your balance is insufficient");
                    return;
                }

                game.currentBet = Math.round(game.currentBet * 100) / 100.0;
                game.totalWinnings = Math.round(game.totalWinnings * 100) / 100.0;

                game.totalWinnings -= game.currentBet;

                balanceInput.setText("Enter a starting balance");
                betInput.setText("Enter a bet");

                balanceAndBet.setText(String.format("Balance: $%.2f\nBet: $%.2f", game.totalWinnings, game.currentBet));

                newBetInput.setText(String.format("%.2f", game.currentBet));

                mainStage.setScene(sceneMap.get("play"));
                beginGame();

            }
            catch (NumberFormatException ignored){
                errorMsg.setText("Invalid value entered for balance and/or bet. Please try again.");
            }

        });

        hitBtn.setOnAction(e->{

            Card newCard = game.theDealer.drawOne();

            game.playerHand.add(newCard);
            playerSide.getChildren().add(makeNewCardImage(newCard));

            if (determineBust(game.playerHand)){
                endRound();

                if (game.totalWinnings <= 0.0){
                    mainStage.setScene(sceneMap.get("lose"));
                }
                else{
                    mainStage.setScene(sceneMap.get("end"));
                }

            }

        });

        stayBtn.setOnAction(e->{

            dealerSide.getChildren().remove(1);
            dealerSide.getChildren().add(makeNewCardImage(game.bankerHand.get(1)));

            boolean bust = determineBust(game.bankerHand);
            while (game.gameLogic.evaluateBankerDraw(game.bankerHand) && !bust){

                Card newCard = game.theDealer.drawOne();
                game.bankerHand.add(newCard);
                dealerSide.getChildren().add(makeNewCardImage(newCard));
            }

            endRound();
            roundOverPause.play();
        });

        startRoundBtn2.setOnAction(e->{
            try{
                game.currentBet = Double.parseDouble(newBetInput.getText());

                if (game.currentBet < 0.0 || game.totalWinnings - game.currentBet < 0.0){
                    endErrorMsg.setText("The bet amount cannot be used, as it is either negative, OR your balance is not sufficient");
                    return;
                }

                game.currentBet = Math.round(game.currentBet * 100) / 100.0;

                game.totalWinnings -= game.currentBet;

                balanceAndBet.setText(String.format("Balance: $%.2f\nBet: $%.2f", game.totalWinnings, game.currentBet));
                newBetInput.setText(String.format("%.2f", game.currentBet));

                beginGame();
                mainStage.setScene(sceneMap.get("play"));

            }
            catch (NumberFormatException ignored){
                endErrorMsg.setText("Invalid value entered for balance and/or bet. Please try again.");
            }

        });

        mainStage.setTitle("Blackjack");
        mainStage.setScene(sceneMap.get("start"));
        mainStage.show();

    }

    private Scene createStartScene(){

        BorderPane pane = new BorderPane();

        Text titleText = new Text("BLACKJACK");
        titleText.setStyle("-fx-font-size: 50;");

        startBtn = new Button("Start");
        startBtn.setMinSize(windowWidth / 10, windowHeight / 20);
        startBtn.setBackground(btnBg);
        startBtn.setBorder(btnBorder);

        rulesBtn = new Button("Rules");
        rulesBtn.setMinSize(windowWidth / 10, windowHeight / 20);
        rulesBtn.setBackground(btnBg);
        rulesBtn.setBorder(btnBorder);

        VBox v = new VBox(windowHeight / 24, titleText, startBtn, rulesBtn);
        v.setAlignment(Pos.CENTER);
        v.setStyle("-fx-font-size: 25;");

        pane.setCenter(v);
        pane.setPadding(new Insets(windowHeight / 12, windowWidth / 7.5, windowHeight / 2.4, windowWidth / 7.5));

        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, windowWidth, windowHeight);

    }

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

        Button rulesReturn = new Button("Return");
        rulesReturn.setOnAction(returnToHome);
        rulesReturn.setMinSize(windowWidth / 10, windowHeight / 20);
        rulesReturn.setBackground(btnBg);
        rulesReturn.setBorder(btnBorder);
        rulesReturn.setStyle("-fx-font-size: 25;");

        VBox box = new VBox(windowHeight / 24, ruleTitle, ruleText, rulesReturn);
        box.maxWidth(50);

        BorderPane pane = new BorderPane();
        pane.setCenter(box);
        pane.setPadding(new Insets(windowHeight / 12, windowWidth / 7.5, windowHeight / 2.4, windowWidth / 7.5));

        box.setAlignment(Pos.CENTER);

        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, windowWidth, windowHeight);
    }

    private Scene createSetupScene(){

        Text balanceText = new Text("Set Your Starting Amount");
        Text betText = new Text("Set Your Starting Bet");
        balanceText.setStyle("-fx-font-size: 40");
        betText.setStyle("-fx-font-size: 40");

        errorMsg = new Text();
        errorMsg.setStyle("-fx-font-size: 20");

        balanceInput = new TextField("Enter a starting balance");
        balanceInput.setMinSize(windowWidth / 4.5, windowHeight / 24);
        balanceInput.setMaxSize(windowWidth / 4.5, windowHeight / 24);
        balanceInput.setAlignment(Pos.CENTER);
        balanceInput.setStyle("-fx-font-size: 25");

        betInput = new TextField("Enter a bet");
        betInput.setMinSize(windowWidth / 4.5, windowHeight / 24);
        betInput.setMaxSize(windowWidth / 4.5, windowHeight / 24);
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
        startRoundBtn1.setMinSize(windowWidth / 10, windowHeight / 20);
        startRoundBtn1.setBackground(btnBg);
        startRoundBtn1.setBorder(btnBorder);
        startRoundBtn1.setStyle("-fx-font-size: 25;");

        Button setUpReturn = new Button("Return");
        setUpReturn.setOnAction(returnToHome);
        setUpReturn.setMinSize(windowWidth / 10, windowHeight / 20);
        setUpReturn.setBackground(btnBg);
        setUpReturn.setBorder(btnBorder);
        setUpReturn.setStyle("-fx-font-size: 25;");

        VBox v1 = new VBox(windowHeight / 60, balanceText, balanceInput);
        VBox v2 = new VBox(windowHeight / 60, betText, betInput);
        VBox v3 = new VBox(windowHeight / 24, v1, v2, startRoundBtn1, setUpReturn, errorMsg);
        v1.setAlignment(Pos.valueOf("CENTER"));
        v2.setAlignment(Pos.valueOf("CENTER"));
        v3.setAlignment(Pos.valueOf("CENTER"));

        BorderPane pane = new BorderPane();
        pane.setCenter(v3);
        pane.setPadding(new Insets(windowHeight / 6, windowWidth / 7.5, windowHeight / 2, windowWidth / 7.5));
        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, windowWidth, windowHeight);
    }

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
        balanceInput.setMaxWidth(windowWidth / 10);
        balanceAndBet.setFill(Color.WHITE);
        balanceAndBet.setStyle("-fx-font-size: 25");
        balanceInput.setAlignment(Pos.CENTER);

        HBox textBox = new HBox(balanceAndBet);
        textBox.setAlignment(Pos.CENTER);

        BorderPane lowerRail = new BorderPane();
        lowerRail.setCenter(buttonBox);
        lowerRail.setPadding(new Insets(0, windowWidth / 12, 0, 5));
        lowerRail.setLeft(textBox);
        lowerRail.setMinHeight(windowHeight / 12);
        lowerRail.setMaxHeight(windowHeight / 12);

        dealerSide = new HBox(windowWidth / 75);
        playerSide = new HBox(windowWidth / 75);

        dealerSide.setAlignment(Pos.CENTER);
        playerSide.setAlignment(Pos.CENTER);

        VBox table = new VBox(windowHeight / 4, dealerSide, playerSide);

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

    private Scene createEndScene(){

        result = new Text();
        result.setStyle("-fx-font-size: 40;");

        endErrorMsg = new Text();
        endErrorMsg.setStyle("-fx-font-size: 20");

        Text continueMsg1 = new Text("Keep Playing?");
        Text continueMsg2 = new Text("Change your bet, or continue with the same bet");

        newBalance = new Text();

        newBetInput = new TextField();
        newBetInput.setMinSize(windowWidth / 3.75, windowHeight / 24);
        newBetInput.setMaxSize(windowWidth / 3.75, windowHeight / 24);
        newBetInput.setAlignment(Pos.CENTER);

        startRoundBtn2 = new Button("Continue");
        startRoundBtn2.setMinSize(windowWidth / 10, windowHeight / 20);
        startRoundBtn2.setMaxSize(windowWidth / 10, windowHeight / 20);
        startRoundBtn2.setBackground(btnBg);
        startRoundBtn2.setBorder(btnBorder);

        Button endGame = new Button("Home");
        endGame.setOnAction(returnToHome);
        endGame.setMinSize(windowWidth / 10, windowHeight / 20);
        endGame.setMaxSize(windowWidth / 10, windowHeight / 20);
        endGame.setBackground(btnBg);
        endGame.setBorder(btnBorder);

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

    private Scene createLoseScene(){

        Text loseTitle = new Text("YOU'VE LOST YOUR ENTIRE BALANCE");
        loseTitle.setStyle("-fx-font-size: 40");

        Text msg = new Text("To keep playing, return to the start");
        msg.setStyle("-fx-font-size: 30");

        Button endGame = new Button("Home");
        endGame.setOnAction(returnToHome);
        endGame.setMinSize(windowWidth / 10, windowHeight / 20);
        endGame.setMaxSize(windowWidth / 10, windowHeight / 20);
        endGame.setBackground(btnBg);
        endGame.setBorder(btnBorder);

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

    private ImageView makeNewCardImage(Card newCard){
        String imageFile = newCard.value + "_" + newCard.suit + ".png";
        Image image = new Image(imageFile);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(picHeight);
        imageView.setFitWidth(picWidth);

        return imageView;
    }

    private void beginGame(){

        playerSide.getChildren().clear();
        dealerSide.getChildren().clear();

        game.theDealer.generateDeck();

        game.playerHand = game.theDealer.dealHand();
        game.bankerHand = game.theDealer.dealHand();

        dealerSide.getChildren().add(makeNewCardImage(game.bankerHand.get(0)));

        Image backImage = new Image("back.png");
        ImageView viewBackImage = new ImageView(backImage);
        viewBackImage.setFitHeight(picHeight);
        viewBackImage.setFitWidth(picWidth);

        dealerSide.getChildren().add(viewBackImage);

        for (Card card : game.playerHand){
            playerSide.getChildren().add(makeNewCardImage(card));
        }

    }

    private boolean determineBust(ArrayList<Card> hand){
        return game.gameLogic.handTotal(hand) > 21;
    }

    private void endRound(){

        double winnings = game.evaluateWinnings();

        if (winnings > 0.0){
            game.totalWinnings += winnings + game.currentBet;
        }

        newBalance.setText("Current Balance: " + game.totalWinnings);

        winner = game.gameLogic.whoWon(game.playerHand, game.bankerHand);
        result.setText(resultMsg.get(winner));

    }
}
