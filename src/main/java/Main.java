import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application{

    Scene startScene, setupScene, rulesScene, gameScene, endScene, loseScene;

    BlackjackGame game;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        game = new BlackjackGame();

        BorderPane pane = new BorderPane();


    }
}
