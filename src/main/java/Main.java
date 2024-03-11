import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application{

    private BlackjackGame game;

    HBox dealerSide, playerSide;

    Button startBtn, rulesBtn, startRoundBtn1, startRoundBtn2, hitBtn, stayBtn;
    Text errorMsg, balanceAndBet;
    TextField balanceInput, betInput, newBetInput;
    BackgroundFill bgFill = new BackgroundFill(Color.rgb(20, 174, 92), null, null);
    BackgroundFill btnFill = new BackgroundFill(Color.rgb(69, 194, 128), new CornerRadii(20), null);
    Background btnBg = new Background(btnFill);

    BorderStroke btnStroke = new BorderStroke(Color.rgb(0, 121, 76), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(5));
    Border btnBorder = new Border(btnStroke);
    Background bg = new Background(bgFill);

    EventHandler<ActionEvent> returnToHome;

    HashMap<String, Scene> sceneMap = new HashMap<>();
    HashMap<String, String> resultMsg = new HashMap<>();

    static final int picHeight = 246;
    static final int picWidth = 180;

    String winner;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {

        game = new BlackjackGame();

        resultMsg.put("player", "You Won!");
        resultMsg.put("dealer", "You Lost...");
        resultMsg.put("push", "Drawn");

        returnToHome = e -> {
            mainStage.setScene(sceneMap.get("start"));
        };

        sceneMap.put("start", createStartScene());
        sceneMap.put("setup", createSetupScene());
        sceneMap.put("rules", createRulesScene());
        sceneMap.put("play", createPlayScene());

        startBtn.setOnAction(e ->{
            mainStage.setScene(sceneMap.get("setup"));
        });

        rulesBtn.setOnAction(e -> {
            mainStage.setScene(sceneMap.get("rules"));
        });

        startRoundBtn1.setOnAction(e->{
            try{

                errorMsg.setText("");
                game.totalWinnings = Double.parseDouble(balanceInput.getText());

                game.currentBet = Double.parseDouble(betInput.getText());

                balanceInput.setText("Enter a starting balance");
                betInput.setText("Enter a bet");

                balanceAndBet.setText("Balance: $" + game.totalWinnings + "\n" +
                                      "Bet: $" + game.currentBet);

//                newBetInput.setText(String.valueOf(game.currentBet));

                mainStage.setScene(sceneMap.get("play"));
                beginGame();

            }
            catch (NumberFormatException ignored){
                errorMsg.setText("Invalid value entered for balance and/or bet. Please try again.");
            }

        });

        hitBtn.setOnAction(e->{

            Card newCard = game.theDealer.drawOne();
            String cardName = newCard.value + "_" + newCard.suit + ".png";

            Image newImage = new Image(cardName);
            ImageView viewImage = new ImageView(newImage);
            viewImage.setFitHeight(picHeight);
            viewImage.setFitWidth(picWidth);

            game.playerHand.add(newCard);
            playerSide.getChildren().add(viewImage);

            if (determinePlayerBust()){
                winner = endRound();

                if (game.totalWinnings < 0.0){
                    mainStage.setScene(sceneMap.get("lose"));
                }
                else{
                    mainStage.setScene(sceneMap.get("end"));
                }

            }

        });

        mainStage.setTitle("Blackjack");
        mainStage.setScene(sceneMap.get("start"));
        mainStage.show();

    }

    private Scene createStartScene(){

        BorderPane pane = new BorderPane();

        Text titleText = new Text("BLACKJACK");

        startBtn = new Button("Start");
        startBtn.setMinSize(200, 50);
        startBtn.setBackground(btnBg);
        startBtn.setBorder(btnBorder);

        rulesBtn = new Button("Rules");
        rulesBtn.setMinSize(200, 50);
        rulesBtn.setBackground(btnBg);
        rulesBtn.setBorder(btnBorder);

        VBox v = new VBox(50, titleText, startBtn, rulesBtn);
        v.setAlignment(Pos.valueOf("BASELINE_CENTER"));
        v.setStyle("-fx-font-size: 20;");

        titleText.setStyle("-fx-font-size: 50;");

        pane.setCenter(v);
        pane.setPadding(new Insets(100, 200, 500, 200));

        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, 1500, 1200);

    }

    private Scene createRulesScene(){

        Text ruleTitle = new Text("How to Play");
        ruleTitle.setStyle("-fx-font-size: 30;");

        Text ruleText = new Text("• Objective: Beat the dealer's hand without going over 21.\n\n" +
                                    "• Dealing: Both the player and dealer are dealt two cards. The player will only know one of the dealer’s card\n\n" +
                                    "• Card Values: Number cards are worth their face value, face cards are worth 10, and Aces can be worth 1 or 11,depending on what is most helpful to the player\n\n"+
                                    "• Player's Turn: Players can choose to hit (take another card) or stand (keep their current hand).\n\n" +
                                    "• Dealer's Turn: The dealer reveals their face-down card after the player have finished their turn. They must hit until they reach 17 or higher. On hands 17 or higher, the dealer will stay\n\n" +
                                    "• Players win if their hand is closer to 21 than the dealer's without going over. If the player or dealer busts (exceeds 21), they lose\n\n" +
                                    "• If both sides have the same value hand, it is a draw and the round is over without any money changing hands.\n\n");
        ruleText.setStyle("-fx-font-size: 25;");
        ruleText.setWrappingWidth(900);

        Button rulesReturn = new Button("Return");
        rulesReturn.setOnAction(returnToHome);
        rulesReturn.setMinSize(200, 50);
        rulesReturn.setBackground(btnBg);
        rulesReturn.setBorder(btnBorder);
        rulesReturn.setStyle("-fx-font-size: 20;");

        VBox box = new VBox(50, ruleTitle, ruleText, rulesReturn);
        box.maxWidth(50);

        BorderPane pane = new BorderPane();
        pane.setCenter(box);
        pane.setPadding(new Insets(100, 200, 500, 200));

        box.setAlignment(Pos.valueOf("CENTER"));

        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, 1500, 1200);
    }

    private Scene createSetupScene(){

        Text balanceText = new Text("Set Your Starting Amount");
        Text betText = new Text("Set Your Starting Bet");
        balanceText.setStyle("-fx-font-size: 40");
        betText.setStyle("-fx-font-size: 40");

        errorMsg = new Text();
        errorMsg.setStyle("-fx-font-size: 20");

        balanceInput = new TextField("Enter a starting balance");
        betInput = new TextField("Enter a bet");
        balanceInput.setStyle("-fx-font-size: 30");
        betInput.setStyle("-fx-font-size: 30");
        balanceInput.setAlignment(Pos.valueOf("CENTER"));
        betInput.setAlignment(Pos.valueOf("CENTER"));
        balanceInput.setMinSize(400, 50);
        betInput.setMinSize(400, 50);
        balanceInput.setMaxSize(400, 100);
        betInput.setMaxSize(400, 100);

        BackgroundFill inputFill = new BackgroundFill(Color.rgb(175, 244, 198), new CornerRadii(5), null);
        Background inputBg = new Background(inputFill);
        balanceInput.setBackground(inputBg);
        betInput.setBackground(inputBg);

        BorderStroke inputStroke = new BorderStroke(Color.rgb(101, 191, 140), BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2));
        Border inputBorder = new Border(inputStroke);
        balanceInput.setBorder(inputBorder);
        betInput.setBorder(inputBorder);

        startRoundBtn1 = new Button("Start Game");
        startRoundBtn1.setMinSize(200, 50);
        startRoundBtn1.setBackground(btnBg);
        startRoundBtn1.setBorder(btnBorder);
        startRoundBtn1.setStyle("-fx-font-size: 20;");

        Button setUpReturn = new Button("Return");
        setUpReturn.setOnAction(returnToHome);
        setUpReturn.setMinSize(200, 50);
        setUpReturn.setBackground(btnBg);
        setUpReturn.setBorder(btnBorder);
        setUpReturn.setStyle("-fx-font-size: 20;");

        VBox v1 = new VBox(20, balanceText, balanceInput);
        VBox v2 = new VBox(20, betText, betInput);
        VBox v3 = new VBox(50, v1, v2, startRoundBtn1, setUpReturn, errorMsg);
        v1.setAlignment(Pos.valueOf("CENTER"));
        v2.setAlignment(Pos.valueOf("CENTER"));
        v3.setAlignment(Pos.valueOf("CENTER"));

        BorderPane pane = new BorderPane();
        pane.setCenter(v3);
        pane.setPadding(new Insets(200, 200, 600, 200));
        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, 1500, 1200);
    }

    private Scene createPlayScene(){

        HBox upperRail = new HBox();
        upperRail.setMinHeight(100);
        upperRail.setMaxHeight(100);

        hitBtn = new Button("Hit");
        stayBtn = new Button("Stay");

        hitBtn.setMinSize(100, 50);
        stayBtn.setMinSize(100, 50);

        BackgroundFill controlFill = new BackgroundFill(Color.rgb(230, 230, 230), new CornerRadii(10), null);
        Background controlBg = new Background(controlFill);
        hitBtn.setBackground(controlBg);
        stayBtn.setBackground(controlBg);

        HBox buttonBox = new HBox(150, hitBtn, stayBtn);
        buttonBox.setAlignment(Pos.CENTER);

        balanceAndBet = new Text();
        balanceAndBet.setFill(Color.WHITE);
        balanceAndBet.setStyle("-fx-font-size: 25");

        HBox lowerUI = new HBox(450, balanceAndBet, buttonBox);
        lowerUI.setAlignment(Pos.CENTER);
        HBox lowerRail = new HBox(lowerUI);
        lowerRail.setMinHeight(100);
        lowerRail.setMaxHeight(100);

        dealerSide = new HBox(20);
        playerSide = new HBox(20);

        dealerSide.setAlignment(Pos.CENTER);
        playerSide.setAlignment(Pos.CENTER);

        VBox table = new VBox(300, dealerSide, playerSide);

        BorderPane centerPane = new BorderPane();
        centerPane.setCenter(table);
        centerPane.setPadding(new Insets(100, 0, 0, 0));

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

        return new Scene(pane, 1500, 1200);
    }

    private Scene createEndScene(){

        Text result = new Text(resultMsg.get(winner));

        Text continueMsg1 = new Text("Keep Playing?");
        Text continueMsg2 = new Text("Change your bet, or continue with the same bet");
        newBetInput = new TextField();
        newBetInput.setAlignment(Pos.CENTER);

        startRoundBtn2 = new Button("Continue");

        return null;
    }

    private void beginGame(){

        playerSide.getChildren().clear();
        dealerSide.getChildren().clear();

        game.theDealer.generateDeck();

        game.playerHand = game.theDealer.dealHand();
        game.bankerHand = game.theDealer.dealHand();

        String cardName = game.bankerHand.get(0).value + "_" + game.bankerHand.get(0).suit + ".png";

        Image newCard = new Image(cardName);
        ImageView viewImage = new ImageView(newCard);
        viewImage.setFitHeight(picHeight);
        viewImage.setFitWidth(picWidth);

        dealerSide.getChildren().add(viewImage);

        newCard = new Image("back.png");
        viewImage = new ImageView(newCard);
        viewImage.setFitHeight(picHeight);
        viewImage.setFitWidth(picWidth);

        dealerSide.getChildren().add(viewImage);

        for (Card card : game.playerHand){
            cardName = card.value + "_" + card.suit + ".png";
            newCard = new Image(cardName);
            viewImage = new ImageView(newCard);
            viewImage.setFitHeight(picHeight);
            viewImage.setFitWidth(picWidth);

            playerSide.getChildren().add(viewImage);
        }

    }

    private boolean determinePlayerBust(){
        return game.gameLogic.handTotal(game.playerHand) > 21;
    }

    private String endRound(){

        double winnings = game.evaluateWinnings();
        game.totalWinnings += winnings;

        return game.gameLogic.whoWon(game.playerHand, game.bankerHand);
    }
}
