import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;

public class Main extends Application{

    BlackjackGame game;
    Button startBtn, rulesBtn;
    BackgroundFill bgFill = new BackgroundFill(Color.rgb(20, 174, 92), null, null);
    BackgroundFill btnFill = new BackgroundFill(Color.rgb(69, 194, 128), new CornerRadii(20), null);
    Background btnBg = new Background(btnFill);

    BorderStroke btnStroke = new BorderStroke(Color.rgb(0, 121, 76), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(5));
    Border btnBorder = new Border(btnStroke);
    Background bg = new Background(bgFill);

    EventHandler<ActionEvent> returnToHome, startRound;

    HashMap<String, Scene> sceneMap = new HashMap<>();

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {

        game = new BlackjackGame();

        sceneMap.put("start", createStartScene());

        // need code for startBtn

        rulesBtn.setOnAction(e -> {
                mainStage.setScene(sceneMap.get("rules"));
        });

        returnToHome = e -> {
            mainStage.setScene(sceneMap.get("start"));
        };

        startRound = e->{
            mainStage.setScene(createPlayScene());
        };

        sceneMap.put("rules", createRulesScene());

        mainStage.setTitle("Blackjack");

//        mainStage.setScene(sceneMap.get("start"));
        mainStage.setScene(createSetupScene());
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
                                    "• Card Values: Number cards are worth their face value, face cards are worth 10, and Aces can be worth 1 or 11,\ndepending on what is most helpful to the player\n\n"+
                                    "• Player's Turn: Players can choose to hit (take another card) or stand (keep their current hand).\n\n" +
                                    "• Dealer's Turn: The dealer reveals their face-down card after the player have finished their turn.\nThey must hit until they reach 17 or higher. On hands 17 or higher, the dealer will stay\n\n" +
                                    "• Players win if their hand is closer to 21 than the dealer's without going over. If the player or dealer busts (exceeds 21), they lose\n\n" +
                                    "• If both sides have the same value hand, it is a draw and the round is over without any money changing hands.\n\n");
        ruleText.setStyle("-fx-font-size: 25;");
        ruleText.setWrappingWidth(800);

        Button returnBtn = new Button("Return");
        returnBtn.setOnAction(returnToHome);
        returnBtn.setMinSize(200, 50);
        returnBtn.setBackground(btnBg);
        returnBtn.setBorder(btnBorder);
        returnBtn.setStyle("-fx-font-size: 20;");

        VBox box = new VBox(50, ruleTitle, ruleText, returnBtn);
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

        TextField balanceInput = new TextField("Enter a starting amount");
        TextField betInput = new TextField("Enter a bet");

        VBox v1 = new VBox(20, balanceText, balanceInput);
        VBox v2 = new VBox(20, betText, betInput);
        VBox v3 = new VBox(50, v1, v2);
        v1.setAlignment(Pos.valueOf("CENTER"));
        v2.setAlignment(Pos.valueOf("CENTER"));
        v3.setAlignment(Pos.valueOf("CENTER"));

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

        balanceText.setStyle("-fx-font-size: 40");
        betText.setStyle("-fx-font-size: 40");

        balanceInput.setStyle("-fx-font-size: 30");
        betInput.setStyle("-fx-font-size: 30");

        balanceInput.setAlignment(Pos.valueOf("CENTER"));
        betInput.setAlignment(Pos.valueOf("CENTER"));

        balanceInput.setBorder(inputBorder);
        betInput.setBorder(inputBorder);

        BorderPane pane = new BorderPane();
        pane.setCenter(v3);
        pane.setPadding(new Insets(200, 200, 600, 200));
        pane.setBackground(bg);
        pane.setStyle("-fx-font-family: 'Times New Roman'");

        return new Scene(pane, 1500, 1200);
    }

    private Scene createPlayScene(){
        return null;
    }

}
