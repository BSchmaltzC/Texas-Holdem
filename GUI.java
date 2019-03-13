import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.shape.Ellipse;

/**
 * This method is the entry point for a new, GUI-based game. The menu is 
 * displayed, new Games are created, and the main game loop is run from here.
 * 
 * @author Adam Hiles
 * @version 03/03/18
 */
public class GUI extends Application {
	private final double WIN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
	private final double WIN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
	
	
	/**
	 * 
	 * 
	 * @param the stage of the GUI
	 */
	@Override
	public void start(Stage primaryStage) throws Exception{
		primaryStage.setTitle("Texas Hold\'em");
		primaryStage.setFullScreen(true);
		primaryStage.setResizable(false);
		primaryStage.setFullScreenExitHint("");
		
		MainMenu menu = new MainMenu();
		
		Scene scene = new Scene(menu.getMenu(), WIN_WIDTH, WIN_HEIGHT);
		scene.getStylesheets().add("tableStyle.css");
		scene.setFill(Color.BLACK);

		primaryStage.setScene(scene);
		primaryStage.show();
		
		((Button) scene.lookup("#startButton")).setOnAction(new EventHandler<ActionEvent>() { //Temporarily, the EventHandler for the main menu's start button is set here.
			@Override
			public void handle(ActionEvent event) {
				generatePlayArea(scene);
			}
		});
	}
	
	private void generatePlayArea(Scene scene) {
		GUITest.testDeck.shuffle();
		ArrayList<Card> comm = GUITest.generateComm();
		int playerNum = (int) ((Slider) scene.lookup("#comSlider")).getValue() + 1;
		ArrayList<Player> players = GUITest.generatePlayers(playerNum);
		
		BorderPane playArea = new BorderPane();
		ActionBar actionBar = new ActionBar(WIN_WIDTH, WIN_HEIGHT);
		Table table = new Table(players, comm);
		playArea.setBottom(actionBar.getBarPane());
		playArea.setCenter(table.getTablePane());
		scene.setRoot(playArea);
		
		((Button) scene.lookup("#fold")).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				revealAllCards(players, scene);
			}
		});
		
		((Button) scene.lookup("#notifCont")).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				((HBox) scene.lookup("#notif")).setVisible(false);
				/*
				 if (game.getRound < 4)
				 	runTurn(scene, game);
				 else if (game.getRound == 4) {
				 	if (((Label) scene.lookup("#notifLabel")).getText() == "Showdown")
				 		showdown(scene, game);
				 	else
				 	runPlayRound(scene, game);
				 	}
				 */
			}
		});
		
		/*
		((Button) scene.lookup("#raiseConfirm")).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				game.raise(((Slider) scene.lookup("#raiseSlider")).getValue());
				finishUserTurn(scene, game);
			}
		});
		
		((Button) scene.lookup("#call")).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				game.call();
				finishUserTurn(scene, game);
			}
		});
		*/
	}
	
	private void runGame(Scene scene, Game game) {
		runPlayRound(scene, game);
	}
	
	private void runPlayRound(Scene scene, Game game) {
		ArrayList<Player> players = game.getPlayers();
		((Ellipse) scene.lookup("#" + players.get(0).getName() + "Chip")).setFill(Color.BLUE);
		((Ellipse) scene.lookup("#" + players.get(0).getName() + "Chip")).setVisible(true);
		((Ellipse) scene.lookup("#" + players.get(1).getName() + "Chip")).setFill(Color.YELLOW);
		((Ellipse) scene.lookup("#" + players.get(1).getName() + "Chip")).setVisible(true);
		if (players.size() != 2) {
			((Ellipse) scene.lookup("#" + players.get(players.size() - 1).getName() + "Chip")).setFill(Color.WHITE);
			((Ellipse) scene.lookup("#" + players.get(players.size() - 1).getName() + "Chip")).setVisible(true);
		}
		
		for (Player player : players) {
			((ImageView) scene.lookup("#" + player.getName() + "Card1Front")).setImage(new Image("/Images/" + player.getHole().get(0).getSuit() + "/" + player.getHole().get(0).getRank() + ".png")); 
			((ImageView) scene.lookup("#" + player.getName() + "Card2Front")).setImage(new Image("/Images/" + player.getHole().get(1).getSuit() + "/" + player.getHole().get(1).getRank() + ".png"));
		}
		
		for(int index = 0; index < 5; index++) {
			ArrayList<Card> comm = game.getComm();
			((ImageView) scene.lookup("#commFront" + index)).setImage(new Image("/Images/" + comm.get(index).getSuit() + "/" + comm.get(index).getRank() + ".png"));
		}
		
		notifyRound(scene, game);
	}
	
	private void notifyRound(Scene scene, Game game) {
		if (game.getRound() == 1)
			revealFlop(scene);
		else if (game.getRound() == 2)
			revealCard((ImageView) scene.lookup("#commBack3"), (ImageView) scene.lookup("#commFront3"));
		else if (game.getRound() == 3)
			revealCard((ImageView) scene.lookup("#commBack4"), (ImageView) scene.lookup("#commFront4"));
		HBox notif = (HBox) scene.lookup("#notif");
		Label notifLabel = (Label) scene.lookup("#notifLabel");
		notifLabel.setText(game.getRoundString());
		notif.setVisible(true);
	}
	
	private void runTurn(Scene scene, Game game) {
		Player player = game.getCurrentPlayer();
		if (player instanceof Human) {
			setupUserTurn(player, scene, game);
		}
		else {
			((Label) scene.lookup("#" + player.getName() + "Name")).setStyle("-fx-border-color: lime;");
			player = game.processTurn();
			TimeUnit.SECONDS.sleep(3);
			updatePlayerInfo(player, scene);
			((Label) scene.lookup("#" + player.getName() + "Name")).setStyle("-fx-border-color: lightblue;");
			if (game.isBetRoundRunning())
				runTurn(scene, game);
			else
				notifyRound(scene, game);
		}
	}
	
	private void setupUserTurn(Player user, Scene scene, Game game) {
		Button call = (Button) scene.lookup("#call");
		Button raise = (Button) scene.lookup("#raise");
		HBox controls = (HBox) scene.lookup("#controls");
		HBox raiseInput = (HBox) scene.lookup("#raiseInput");
		Slider raiseSlider = (Slider) scene.lookup("#raiseSlider");
		
		((Label) scene.lookup("#" + user.getName() + "Name")).setStyle("-fx-border-color: lime;");
		
		/*
		if (user.getBet() == game.getBet())
			call.setText("Check");
		else
			call.setText("Call");
		
		raiseSlider.setMax(user.getStack());
		*/
		
		controls.setDisable(false);
	}
	
	private void finishUserTurn(Scene scene, Game game) {
		HBox controls = (HBox) scene.lookup("#controls");
		HBox raiseInput = (HBox) scene.lookup("#raiseInput");
		Slider raiseSlider = (Slider) scene.lookup("#raiseSlider");
		Button raise = (Button) scene.lookup("#raise");
		
		Player user = game.getCurrentPlayer();
		controls.setDisable(true);
		raiseInput.setVisible(false);
		raise.setText("Bet");
		updatePlayerInfo(user, scene);
		((Label) scene.lookup("#" + user.getName() + "Name")).setStyle("-fx-border-color: lightblue;");
		game.incrementPlayer();
		
		if (game.isBetRoundRunning())
			runTurn(scene, game);
		else
			notifyRound(scene, game);
	}
	
	private void updatePlayerInfo(Player player, Scene scene) {
		//((Label) scene.lookup("#" + player.getName() + "Stack")).setText("Stack: " + player.getStack());
		((Label) scene.lookup("#" + player.getName() + "Action")).setText("Action: " + player.getAction());
		//((Label) scene.lookup("#" + player.getName() + "Bet")).setText("Current Bet: " + player.getBet());
	}
	
	private void showdown(Scene scene, Game game) {
		revealAllCards(game.getPlayers, scene);
		for (Player player : game.getPlayers())
			((Label) scene.lookup("#" + player.getName() + "Bet")).setText("Hand: " + player.getHand().toString());
		
		ArrayList<Player> winners = game.showdown();
		HBox notif = (HBox) scene.lookup("#notif");
		Label notifLabel = (Label) scene.lookup("#notifLabel");
		if (winners.size() == 1)
			notifLabel.setText(winners.get(0).getName() + " Wins the Pot");
		else if (winners.size() == 2)
			notifLabel.setText(winners.get(0).getName() + " and " + winners.get(1).getName() + " Wins the Pot");
		else if (winners.size() == game.getPlayers().size())
			notifLabel.setText("The Pot Will Be Divided Evenly");
		notif.setVisible(true);
	}
	
	private void cleanup(Scene scene, Game game) {
		ScaleTransition showBack = new ScaleTransition();
		showBack.setByX(1);
		showBack.setDuration(Duration.seconds(0.001));
		for (Player player : game.getPlayers) {
			if (player instanceof AI) {
				showBack.setNode((ImageView) scene.lookup("#" + player.getName() + "Card1Back"));
				showBack.play();
				showBack.setNode((ImageView) scene.lookup("#" + player.getName() + "Card2Back"));
				showBack.play();
			}
		((Label) scene.lookup("#" + player.getName() + "Action")).setText(" ");
		}
		for (int index = 0; index < 5; index++) {
			showBack.setNode((ImageView) scene.lookup("#commBack" + index));
			showBack.play();
		}
		runPlayRound(scene, game);
	}
	
	/**
	 * For each AI player in the passed list, i.e. those whose cards are
	 * covered, their hole cards are revealed through the card reveal 
	 * animation.
	 * 
	 * @param players the players still in the game
	 * @param scene the game node tree
	 */
	private void revealAllCards(ArrayList<Player> players, Scene scene) {
		for (Player player : players) {
			if (player instanceof AI) {
				ScaleTransition showFrontA = revealCard((ImageView) scene.lookup("#" + player.getName() + "Card1Back"), (ImageView) scene.lookup("#" + player.getName() + "Card1"));
				showFrontA.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						revealCard((ImageView) scene.lookup("#" + player.getName() + "Card2Back"), (ImageView) scene.lookup("#" + player.getName() + "Card2"));
					}
				});	
			}
		}
	}
	
	/**
	 * For the second betting round the first three community cards are
	 * revealed sequentially via the below method and the card reveal
	 * animation.
	 * 
	 * @param scene the game node tree
	 */
	private void revealFlop(Scene scene) {
		ScaleTransition showFrontA = revealCard((ImageView) scene.lookup("#commBack0"), (ImageView) scene.lookup("#commFront0"));
		showFrontA.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ScaleTransition showFrontB = revealCard((ImageView) scene.lookup("#commBack1"), (ImageView) scene.lookup("#commFront1"));
				showFrontB.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						revealCard((ImageView) scene.lookup("#commBack2"), (ImageView) scene.lookup("#commFront2"));
					}
				});	
			}
		});	
	}
	
	/**
	 * The set of ImageViews passed into this method will be swapped by an
	 * appropriate flipping animation.
	 * 
	 * @param cardBack the ImageView of the card's back
	 * @param cardFront the ImageView of the card's face
	 */
	private ScaleTransition revealCard(ImageView cardBack, ImageView cardFront) {
		ScaleTransition hideFront = new ScaleTransition(); //The face is initially hidden from view
		hideFront.setByX(-1);
		hideFront.setDuration(Duration.seconds(0.001));
		hideFront.setNode(cardFront);
		
		ScaleTransition hideBack = new ScaleTransition(); //The back is scaled to a line to be invisible
		hideBack.setByX(-1);
		hideBack.setDuration(Duration.seconds(0.25));
		hideBack.setNode(cardBack);
		
		ScaleTransition showFront = new ScaleTransition(); //The face is returned from a line to full size
		showFront.setByX(1);
		showFront.setDuration(Duration.seconds(0.25));
		showFront.setNode(cardFront);
		
		hideFront.setOnFinished(new EventHandler<ActionEvent>() { //Once the face is hidden the back is flipped
			@Override
			public void handle(ActionEvent event) {
				hideBack.play();
			}
		});
		
		hideBack.setOnFinished(new EventHandler<ActionEvent>() { //Once the back is flipped the front is flipped
			@Override
			public void handle(ActionEvent event) {
				showFront.play();
			}
		});
		
		hideFront.play();
		
		return showFront;
	}
	
	/**
	 * On launch the extended Application handles the launch of the GUI,
	 * eventually leading to the start method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
