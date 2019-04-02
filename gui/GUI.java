package gui;
import java.io.*;
import java.util.ArrayList;

import cards.Card;
import game.Game;
import game.SaveIO;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import players.AI;
import players.Human;
import players.Player;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.shape.Ellipse;
//imports used to play background music
import java.net.URL;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * This method is the entry point for a new, GUI-based game. The menu is 
 * displayed, new Games are created, and the main game loop is run from here.
 * Code for playing a media file is from http://www.java2s.com/Code/Java/JavaFX/Playmp3file.htm
 * 
 * @author Adam Hiles
 * @version 03/28/18
 */
public class GUI extends Application {
	private final double WIN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
	private final double WIN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
	private SaveIO saveLoad = new SaveIO();
    //resources used for playing music
	//private final URL resource = getClass().getResource("/Resources/LOUDER.mp3");
    //private final Media media = new Media(resource.toString());
    //private final MediaPlayer mediaPlayer = new MediaPlayer(media);
	
	/**
	 * On the start of the GUI the main menu will be displayed and an
	 * EventHandler for its start button will be created that starts
	 * the game if pressed.
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
		scene.getStylesheets().add("/gui/tableStyle.css");
		scene.setFill(Color.BLACK);
		//plays a media file indefinitely
		//mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
	    //mediaPlayer.play();
		primaryStage.setScene(scene);
		primaryStage.show();
		
		try { //If a local save file is not available the continue option is greyed out
			(new FileReader("Save.txt")).close();
		}
		catch(FileNotFoundException e) {
			((Button) scene.lookup("#continue")).setDisable(true);
		}
		
		((Button) scene.lookup("#continue")).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				loadFromFile(scene);
			}
		});
		
		((Button) scene.lookup("#startButton")).setOnAction(new EventHandler<ActionEvent>() { //Temporarily, the EventHandler for the main menu's start button is set here.
			@Override
			public void handle(ActionEvent event) {
				int playerNum = (int) ((Slider) scene.lookup("#comSlider")).getValue() + 1;
				int stackSize = Integer.parseInt((((ChoiceBox<String>) scene.lookup("#stackChoice")).getValue()).substring(1).replaceAll(" ", "").replace("$", ""));
				makeNewGame(scene, playerNum, stackSize);
			}
		});
	}
	
	private void makeNewGame(Scene scene, int playerNum, int stackSize) {
		Game game = new Game(); //The new game is created and its parameters are generated
		ArrayList<Player> players = game.generatePlayers(playerNum, stackSize);
		game.setupRound();
		generatePlayArea(scene, game);
	}
	
	private void loadFromFile(Scene scene) {
		Game game = saveLoad.loadState();
		generatePlayArea(scene, game);
	}
	
	/**
	 * Before starting the game the player are will be generated by creating a
	 * new Game object, ActionBar and Table objects based on Game information,
	 * and creating EventHandlers for the user's action buttons. These 
	 * EventHandlers will likely be moved to their own class for future
	 * iterations. The start of a round is called once all this is set up.
	 * 
	 * @param scene the GUI scene
	 */
	private void generatePlayArea(Scene scene, Game game) {
		BorderPane playArea = new BorderPane(); //The GUI play area scene is created
		ActionBar actionBar = new ActionBar(WIN_WIDTH, WIN_HEIGHT, (int) (game.getSmallBlind() / 0.025));
		Table table = new Table(game.getPlayerList(), game.getComm());
		playArea.setBottom(actionBar.getBarPane());
		playArea.setCenter(table.getTablePane());
		scene.setRoot(playArea);
		
		((Button) scene.lookup("#notifCont")).setOnAction(new EventHandler<ActionEvent>() { //Handler for notification acceptance button
			@Override
			public void handle(ActionEvent event) {
				((HBox) scene.lookup("#notif")).setVisible(false); //Certain nodes are made invisible and disabled
				((Button) scene.lookup("#save")).setDisable(true);
				((Button) scene.lookup("#help")).setDisable(true);
				((Button) scene.lookup("#quit")).setDisable(true);
				
				for (Player player : game.getPlayers()) { //Each player not all-in has their action reset
					if (!(player.getStack() == 0)) {
						player.setAction(" ");
						((Label) scene.lookup("#" + player.getName() + "Action")).setText(" ");
					}
				}
				
				if (game.getRound() < 4) { //The sequence of player turns is started for betting rounds
					runTurn(scene, game);
				}
				else if (game.getRound() == 4) { //Special behavior is applied for the showdown
					if (((Label) scene.lookup("#notifLabel")).getText() == "Showdown") //The showdown method is called
						showdown(scene, game);
					else { //The winner display carries out these actions on dismissal
						if (game.isGameOver()) //The game over screen is displayed
							gameOver(scene, game);
						else { //Otherwise the bet (hand) labels are reset and cards are returned to the deck
							returnComm(scene, game);
							for (Player player : game.getPlayers()) {
								((Label) scene.lookup("#" + player.getName() + "Bet")).setText(" ");
								if (player.getStack() == 0)
									((Ellipse) scene.lookup("#" + player.getName() + "Chip")).setVisible(false);
							}
						}
					}
				}
			}
		});
		
		((Button) scene.lookup("#playAgain")).setOnAction(new EventHandler<ActionEvent>() { //Handler for the end game play again button
			@Override
			public void handle(ActionEvent event) {
				((HBox) scene.lookup("#endGameNotif")).setVisible(false);
				((Button) scene.lookup("#quit")).setDisable(true);
				makeNewGame(scene, game.getPlayerList().size(), (int) (game.getSmallBlind() / 0.025));
			}
		});
		
		((Button) scene.lookup("#fold")).setOnAction(new EventHandler<ActionEvent>() { //Handler for the user's fold button
			@Override
			public void handle(ActionEvent event) {
				game.fold();
				finishUserTurn(scene, game);
			}
		});
		
		((Button) scene.lookup("#raiseConfirm")).setOnAction(new EventHandler<ActionEvent>() { //Handler for the confirm button of the user's raise slider
			@Override
			public void handle(ActionEvent event) {
				if (scene.lookup("#raiseConfirm").isDisabled() == true)
					game.bet((game.getCurrentPlayer().getBet() + game.getCurrentPlayer().getStack()) - game.getHighestBet());
				else
					game.bet(((int) ((Slider) scene.lookup("#raiseSlider")).getValue()) - game.getHighestBet());
				finishUserTurn(scene, game);
			}
		});
		
		((Button) scene.lookup("#call")).setOnAction(new EventHandler<ActionEvent>() { //Handler for the user's call button
			@Override
			public void handle(ActionEvent event) {
				game.call();
				finishUserTurn(scene, game);
			}
		});
		
		((Button) scene.lookup("#save")).setOnAction(new EventHandler<ActionEvent>() { //Handler for saving the game
			@Override
			public void handle(ActionEvent event) {
				saveLoad.saveState(game.getPlayerList(), game.getSmallBlind());
			}
		});
		
		startPlayRound(scene, game); //The first round of play is begun
	}
	
	/**
	 * Before starting a round of play the small blind, big blind, and dealer
	 * chips are set in their appropriate spot, players' hole cards are
	 * updated, and the community cards are updated. After these actions are
	 * carried out the first round notification is called.
	 * 
	 * @param scene the GUI scene
	 * @param game the Game object
	 */
	private void startPlayRound(Scene scene, Game game) {
		ArrayList<Player> players = game.getPlayers();
		
		for (Player player : players)
			((Ellipse) scene.lookup("#" + player.getName() + "Chip")).setVisible(false);
		((Ellipse) scene.lookup("#" + players.get(0).getName() + "Chip")).setFill(Color.BLUE); //The blind/dealer chips are displayed as appropriate
		((Ellipse) scene.lookup("#" + players.get(0).getName() + "Chip")).setVisible(true);
		((Ellipse) scene.lookup("#" + players.get(1).getName() + "Chip")).setFill(Color.YELLOW);
		((Ellipse) scene.lookup("#" + players.get(1).getName() + "Chip")).setVisible(true);
		if (players.size() != 2) {
			((Ellipse) scene.lookup("#" + players.get(players.size() - 1).getName() + "Chip")).setFill(Color.WHITE);
			((Ellipse) scene.lookup("#" + players.get(players.size() - 1).getName() + "Chip")).setVisible(true);
		}
		
		for (Player player : players) { //Each player's cards are updated and bets are returned to 0, their action is also reset
			((ImageView) scene.lookup("#" + player.getName() + "Card1")).setImage(new Image("/Images/" + player.getHole().get(0).getSuit() + "/" + player.getHole().get(0).getRank() + ".png")); 
			((ImageView) scene.lookup("#" + player.getName() + "Card2")).setImage(new Image("/Images/" + player.getHole().get(1).getSuit() + "/" + player.getHole().get(1).getRank() + ".png"));
			((Label) scene.lookup("#" + player.getName() + "Bet")).setText("Current Bet: $0");
			((Label) scene.lookup("#" + player.getName() + "Action")).setText(" ");
		}
		
		for(int index = 0; index < 5; index++) { //The community cards are updated
			ArrayList<Card> comm = game.getComm();
			((ImageView) scene.lookup("#commFront" + index)).setImage(new Image("/Images/" + comm.get(index).getSuit() + "/" + comm.get(index).getRank() + ".png"));
		}
		
		shuffleDeck(scene, game); //The deck shuffle animation is played
	}
	
	/**
	 * In between rounds a notification window will appear, prompting the user
	 * to continue. Labels will also be updated as each player's wager is added
	 * to the pot. From here, the game will be fast-tracked to the showdown if
	 * either the user has folded or only one player remains. The community 
	 * cards will also be revealed as appropriate.
	 * 
	 * @param scene the GUI scene
	 * @param game the Game object
	 */
	private void interRound(Scene scene, Game game) {
		Boolean fast = false;
		if (game.isUserFolded())
			fast = true;
		
		if (game.getRound() == 1) //Community cards are dealt and shown as appropriate
			dealFlop(scene, game);
		else if (game.getRound() == 2)
			dealStreet(scene, (ImageView) scene.lookup("#commBack3"), (ImageView) scene.lookup("#commFront3"), fast);
		else if (game.getRound() == 3)
			dealStreet(scene, (ImageView) scene.lookup("#commBack4"), (ImageView) scene.lookup("#commFront4"), fast);
		
		((Button) scene.lookup("#help")).setDisable(false); //Settings buttons are disabled
		((Button) scene.lookup("#quit")).setDisable(false);
		
		((Label) scene.lookup("#pot")).setText("Pot: " + (new MoneyFormatter(game.getPot()).toString())); //The pot is updated and the highest wager is reset
		((Label) scene.lookup("#wager")).setText("Highest Wager: $0");
		for (Player player : game.getPlayers()) //The players' bet is reset
			((Label) scene.lookup("#" + player.getName() + "Bet")).setText("Current Bet: $0");
		
		HBox notif = (HBox) scene.lookup("#notif"); //The notification is set and displayed
		Label notifLabel = (Label) scene.lookup("#notifLabel");
		notifLabel.setText(game.getRoundString());
		
		notif.setVisible(true);
	}
	
	/**
	 * To run a player turn this method retrieves the current player from the
	 * game. If the player is the user the method for setting up a user turn is
	 * called. Else, if they are an AI, the game will process their turn, and,
	 * after a short pause for effect, a method will be called to finish their
	 * turn.
	 * 
	 * @param scene the GUI scene
	 * @param game the Game object
	 */
	private void runTurn(Scene scene, Game game) {
		int allInCounter = 0;
		for (Player player : game.getPlayers()) {
			if (player.getStack() == 0)
				allInCounter++;
		}
		
		Player player = game.getCurrentPlayer();
		
		if ((game.getPlayers().size() == 1) || (allInCounter == game.getPlayers().size())) { //If one player remains or all players are all-in, fast track to showdown
			game.incrementRound();
			interRound(scene, game);
		}
		else if (player.getStack() == 0) { //If a player has gone all-in they cannot do an action
			game.incrementPlayer();
			runTurn(scene, game);
		}
		else if (player instanceof Human) { //The user is directed to an appropriate method
			((Label) scene.lookup("#" + player.getName() + "Name")).setStyle("-fx-text-fill: red;");
			if (game.getRound() == 0) { //For the blind round
				if (game.getPlayerCount() == 0 && game.getHighestBet() == 0) { //If the user is the small blind
					game.bet(game.getSmallBlind());
					
					PauseTransition pause = new PauseTransition(new Duration(1000)); //A pause is added for effect
					pause.setOnFinished(e -> finishUserTurn(scene, game));
					pause.play();
				}
				else if (game.getPlayerCount() == 1 && game.getHighestBet() == game.getSmallBlind()) { //If the user is the big blind
					game.bet(game.getSmallBlind());
					
					PauseTransition pause = new PauseTransition(new Duration(1000));
					pause.setOnFinished(e -> finishUserTurn(scene, game));
					pause.play();
				}
				else
					setupUserTurn(player, scene, game); //Else the action bar is setup for the user's turn in the blind round
			}
			else
				setupUserTurn(player, scene, game); //Else the action bar is setup for the user's turn
		}
		else { //AI players complete their action with a short pause
			((Label) scene.lookup("#" + player.getName() + "Name")).setStyle("-fx-text-fill: red;");
			player = game.processTurn();
			
			PauseTransition pause = new PauseTransition(new Duration(1000));
			
			if (game.isUserFolded())
				pause.setDuration(new Duration(1));
			
			pause.setOnFinished(e -> finishAITurn(scene, game));
			
			pause.play();
		}
	}
	
	/**
	 * After an AI's turn is processed, their on screen information will be
	 * updated, the counter for the next player will be incremented, and either
	 * the program will continue to that next player's turn or a new round
	 * notification will appear, granted the betting round is over.
	 * 
	 * @param scene the GUI scene
	 * @param game the Game object
	 */
	private void finishAITurn(Scene scene, Game game) {
		Player player = game.getLastPlayer(); //The player's information is updated and the player count is incremented
		updatePlayerInfo(player, scene, game);
		game.incrementPlayer();
		((Label) scene.lookup("#" + player.getName() + "Name")).setStyle("-fx-text-fill: black;");
		
		if (player.getAction() == "Folded") { //If the AI folds their cards are returned to the deck
			Boolean fast = false;
			if (game.isUserFolded())
				fast = true;
			
			returnHole(scene, player, false, fast).play();
		}
		
		if (game.isBetRoundRunning()) //Continuing the round calls for the next player's turn
			runTurn(scene, game);
		else //The end of the round calls for the interim round message
			interRound(scene, game);
	}
	
	/**
	 * To setup the user's turn their control buttons are simply enabled and 
	 * their name is colored to mark them as the current player. Additional
	 * scene lookups are included for future iterations where this method
	 * will have to dynamically change call/raise button names and change the
	 * bounds of the raise slide to reflect the user's stack.
	 * 
	 * @param user the human player
	 * @param scene the GUI scene
	 * @param game the Game object
	 */
	private void setupUserTurn(Player user, Scene scene, Game game) {
		Button call = (Button) scene.lookup("#call"); //Needed nodes are found
		Button raise = (Button) scene.lookup("#raise");
		Button raiseConfirm = (Button) scene.lookup("#raiseConfirm");
		HBox controls = (HBox) scene.lookup("#controls");
		Slider raiseSlider = (Slider) scene.lookup("#raiseSlider");
		
		if (user.getBet() == game.getHighestBet()) //The check/call button is set as appropriate
			call.setText("Check");
		else
			call.setText("Call");
		
		if (game.getHighestBet() == 0) //The bet/raise button is set as appropriate
			raise.setText("Bet");
		else
			raise.setText("Raise");
		
		if (user.getStack() <= (game.getHighestBet() - user.getBet())) { //If the user has less money total than the highest bet they can only fold or go all-in
			raise.setDisable(true);
			call.setText("All In");
		}
		else if (((user.getStack() + user.getBet()) > game.getHighestBet()) && ((user.getBet() + user.getStack()) < (game.getHighestBet() + game.getSmallBlind()))) {
			raiseSlider.setDisable(true); //If the user can match the highest bet but not the minimum raise they can fold, call, or go all-in
			raiseConfirm.setText("All In");
		}
		else { //The user can complete the standard actions
			raise.setDisable(false);
			raiseConfirm.setText("Enter");
			raiseSlider.setDisable(false); //The raise slider is setup
			raiseSlider.setMin(game.getHighestBet() + game.getSmallBlind());
			raiseSlider.setMax(user.getStack() + user.getBet());
			raiseSlider.setMajorTickUnit((user.getStack() + user.getBet()) - (game.getSmallBlind() + game.getHighestBet()));
			raiseSlider.setMinorTickCount(((user.getStack() - (game.getSmallBlind() + game.getHighestBet())) / (game.getSmallBlind() / 25)) - 1);
		}
		
		((Button) scene.lookup("#help")).setDisable(false); //Settings buttons are enabled
		((Button) scene.lookup("#quit")).setDisable(false);
		controls.setDisable(false);
	}
	
	/**
	 * At the end of the user's turn their controls are disabled, the raise
	 * menu is closed, button names are defaulted, and the current player is
	 * incremented and the call to run their turn is made. If the betting round
	 * is over the call to setup the next round is made.
	 * 
	 * @param scene the GUI scene
	 * @param game the Game object
	 */
	private void finishUserTurn(Scene scene, Game game) {
		HBox controls = (HBox) scene.lookup("#controls"); //Needed nodes are found
		HBox raiseInput = (HBox) scene.lookup("#raiseInput");
		Button raise = (Button) scene.lookup("#raise");
		Button help = (Button) scene.lookup("#help");
		Button quit = (Button) scene.lookup("#quit");
		Button call = (Button) scene.lookup("#call");
		
		controls.setDisable(true); //Nodes are disabled and labels are reset to default
		help.setDisable(true);
		quit.setDisable(true);
		raiseInput.setVisible(false);
		raise.setVisible(true);
		call.setText("Call");
		
		Player user = game.getLastPlayer(); //The player's information is updated and the player count is incremented
		((Label) scene.lookup("#" + user.getName() + "Name")).setStyle("-fx-text-fill: black;");
		updatePlayerInfo(user, scene, game);
		game.incrementPlayer();
		
		if (user.getAction() == "Folded") { //If the user folds their cards are returned to the deck
			returnHole(scene, user, true, true).play();
		}
		
		if (game.isBetRoundRunning()) //Continuing the round calls for the next player's turn
			runTurn(scene, game);
		else //The end of the round calls for the interim round message
			interRound(scene, game);
	}
	
	/**
	 * After a player has carried out their turn their information as displayed
	 * on the GUI needs to be updated. Currently, only their action is updated
	 * but their stack and bet will also be updated once money systems are
	 * 
	 * 
	 * @param player the player whose information needs to be updated
	 * @param scene the GUI scene
	 */
	private void updatePlayerInfo(Player player, Scene scene, Game game) {
		((Label) scene.lookup("#" + player.getName() + "Action")).setText("Action: " + player.getAction()); //The player's stack and action labels are updated
		((Label) scene.lookup("#" + player.getName() + "Stack")).setText("Stack: " + (new MoneyFormatter(player.getStack())).toString());
		
		MoneyFormatter wagerFormat = new MoneyFormatter(player.getBet()); //The player's bet is formatted
		String wager = wagerFormat.toString();
		((Label) scene.lookup("#" + player.getName() + "Bet")).setText("Current Bet: " + wager); //The player's bet label is updated
		if (player.getBet() > game.getHighestBet()) //If the player has the highest bet their wager becomes the highest in the centre
			((Label) scene.lookup("#wager")).setText("Highest Wager: " + wager);
		
		if (player.getAction() == "Folded") { //If the player folded their bet label is cleared and the centre pot is updated
			((Label) scene.lookup("#" + player.getName() + "Bet")).setText(" ");
			((Label) scene.lookup("#pot")).setText("Pot: " + (new MoneyFormatter(game.getPot())).toString());
		}
	}
	
	/**
	 * For the showdown phase of play each remaining player's bet label is
	 * set to display their highest hand rank. After game is called to 
	 * process the showdown the notification window is shown with a dynamic
	 * string listing the round's victors.
	 * 
	 * @param scene the GUI scene
	 * @param game the Game object
	 */
	private void showdown(Scene scene, Game game) {
		Boolean fast = false;
		if (game.isUserFolded())
			fast = true;
		
		revealAllCards(game.getPlayers(), scene, fast); //All AI hole cards are revealed
		
		ArrayList<Player> winners = game.showdown(); //The game's showdown logic is run
		
		for (Player player : game.getPlayers()) { //Each player has their bet label changed to reflect their highest hand rank
			((Label) scene.lookup("#" + player.getName() + "Bet")).setText("Hand: " + player.getHand().toString());
			if (player.getStack() == 0) //Actions are either cleared or set to "BUSTED OUT" if the player lost all their stack
				((Label) scene.lookup("#" + player.getName() + "Action")).setText("BUSTED OUT");
			else
				((Label) scene.lookup("#" + player.getName() + "Action")).setText(" ");
		}
		
		((Label) scene.lookup("#pot")).setText("Pot: $0"); //The pot is reset and the winners' stacks are updated
		for (Player player : winners) {
			((Label) scene.lookup("#" + player.getName() + "Stack")).setText("Stack: " + (new MoneyFormatter(player.getStack())).toString());
		}
		
		HBox notif = (HBox) scene.lookup("#notif"); //A notification message displaying the winner(s) is constructed
		Label notifLabel = (Label) scene.lookup("#notifLabel");
		StringBuilder winnerString = new StringBuilder();
		if ((winners.size() == game.getPlayers().size()) &&(game.getPlayers().size() != 1))
			winnerString.append("The Pot Will Be Divided Evenly");
		else {
			if (winners.size() == 1)
				winnerString.append(winners.get(0).getName());
			else {
				int index = 0;
				for (Player player : winners) {
					if (index == 0)
						winnerString.append(player.getName());
					else if (index == winners.size() - 1)
						winnerString.append(" and " + player.getName());
					else
						winnerString.append(", " + player.getName());
					index++;
				}
			}
			winnerString.append(" Won the Pot");
		}
		
		((Button) scene.lookup("#help")).setDisable(false); //All settings buttons are enabled and the notification is shown
		((Button) scene.lookup("#save")).setDisable(false);
		((Button) scene.lookup("#quit")).setDisable(false);
		notifLabel.setText(winnerString.toString());
		notif.setVisible(true);
	}
	
	/**
	 * Once a game is finished the end game notification is set to display.
	 * TODO: Make label update with specific end game messages (i.e. You
	 * Busted Out, You Won)
	 * 
	 * @param scene the game node tree
	 * @param game the current Game instance
	 */
	private void gameOver(Scene scene, Game game) {
		HBox endGameNotif = (HBox) scene.lookup("#endGameNotif");
		Label endGameMsg = (Label) scene.lookup("#endGameMsg");
		((Button) scene.lookup("#quit")).setDisable(false);
		endGameNotif.setVisible(true);
	}
	
	/**
	 * To show the shuffling of the cards each image that makes up the deck's
	 * GUI representation has to be moved in a specific way as defined in the
	 * below function, which creates an animation for an image based on the
	 * specific instructions
	 * 
	 * @param scene the master node tree
	 * @param image the image to be moved
	 * @param mirrored whether the animation is a mirrored version or not
	 * @return a full motion animation
	 */
	private SequentialTransition shuffleAnimFactory(Scene scene, ImageView image, Boolean mirrored) {
		TranslateTransition shiftImageHoriz = new TranslateTransition(Duration.millis(250), image); //The image is shifted horizontally 40 pixels
		if (mirrored) //Mirrored images are shifted in the other direction
			shiftImageHoriz.setByX(-40);
		else
			shiftImageHoriz.setByX(40);
		shiftImageHoriz.setAutoReverse(true);
		shiftImageHoriz.setCycleCount(2);
		
		TranslateTransition shiftImageVerti = new TranslateTransition(Duration.millis(250), image); //The image is shifted vertically 56 pixels
		if (mirrored) //Mirrored images are shifted in the other direction
			shiftImageVerti.setByY(-56);
		else
			shiftImageVerti.setByY(56);
		shiftImageVerti.setAutoReverse(true);
		shiftImageVerti.setCycleCount(2);
		
		SequentialTransition shuffleAnim = new SequentialTransition(shiftImageHoriz, shiftImageVerti); //The image is shifted horizontally then vertically
		
		return shuffleAnim;
	}
	
	/**
	 * At the start of each round of play the deck's GUI representation has 
	 * its components shifted to simulate a shuffle as controlled by the below
	 * method. 
	 * 
	 * @param scene the node tree
	 * @param game the current game object
	 */
	private void shuffleDeck(Scene scene, Game game) {
		//The deck components are listed
		ImageView deckA = (ImageView) scene.lookup("#deckA");
		ImageView deckB = (ImageView) scene.lookup("#deckB");
		ImageView drawCard = (ImageView) scene.lookup("#drawCard");
		ImageView returnCard = (ImageView) scene.lookup("#returnCard");
		
		ParallelTransition shuffleAnim = new ParallelTransition();
		
		SequentialTransition deckAAnim = shuffleAnimFactory(scene, deckA, false);
		SequentialTransition deckBAnim = shuffleAnimFactory(scene, deckB, true);
		SequentialTransition drawCardAnim = shuffleAnimFactory(scene, drawCard, true);
		SequentialTransition returnCardAnim = shuffleAnimFactory(scene, returnCard, false);
		
		shuffleAnim.getChildren().addAll(deckAAnim, deckBAnim, drawCardAnim, returnCardAnim); //All components animations are played in unison
		
		shuffleAnim.setOnFinished(e -> dealHoles(scene, game)); //At the end of the animation the hole dealing animation is played
		
		shuffleAnim.play();
	}
	
	/**
	 * For each AI player in the passed list, i.e. those whose cards are
	 * covered, their hole cards are revealed through the card reveal 
	 * animation.
	 * 
	 * @param players the players still in the game
	 * @param scene the game node tree
	 */
	private void revealAllCards(ArrayList<Player> players, Scene scene, Boolean fast) {
		ParallelTransition revealAnim = new ParallelTransition();
		
		for (int card = 1; card <= 2; card++) {
			for (Player player : players) {
				if (player instanceof AI) { 
					ImageView cardBack = (ImageView) scene.lookup("#" + player.getName() + "Card" + card + "Back");
					ImageView cardFront = (ImageView) scene.lookup("#" + player.getName() + "Card" + card);
					
					SequentialTransition revealCard = flipCard(cardBack, cardFront, false, fast);
					revealAnim.getChildren().add(revealCard);
				}
			}
		}
		
		revealAnim.play();
	}
	
	/**
	 * For the second betting round the first three community cards are
	 * revealed sequentially via the below method and the card deal and
	 * reveal animations.
	 * 
	 * @param scene the game node tree
	 */
	private void dealFlop(Scene scene, Game game) {
		SequentialTransition dealingAnim = new SequentialTransition();
		
		ParallelTransition revealCards = new ParallelTransition();
		
		Boolean fast = false;
		if (game.isUserFolded())
			fast = true;
		
		for (int commCard = 0; commCard < 3; commCard++) {
			ImageView cardBack = (ImageView) scene.lookup("#commBack" + commCard);
			ImageView cardFront = (ImageView) scene.lookup("#commFront" + commCard);
			
			SequentialTransition revealCard = flipCard(cardBack, cardFront, false, fast);
			revealCards.getChildren().add(revealCard);
			
			SequentialTransition dealCardAnim = moveCard(scene, cardBack, cardFront, false, fast);
			dealingAnim.getChildren().add(dealCardAnim);
		}
		
		dealingAnim.getChildren().add(revealCards); //After all cards are dealt they are simultaneously revealed
		
		dealingAnim.play();
	}
	
	/**
	 * For the river and turn rounds a single card is dealt and revealed.
	 * 
	 * @param scene the game node tree
	 * @param cardBack the back of the subject card
	 * @param cardFront the front of the subject card
	 */
	private void dealStreet(Scene scene, ImageView cardBack, ImageView cardFront, Boolean fast) {
		SequentialTransition cardAnim = moveCard(scene, cardBack, cardFront, false, fast);
		cardAnim.setOnFinished(e -> flipCard(cardBack, cardFront, false, fast).play());
		
		cardAnim.play();
	}
	
	/**
	 * All of the community cards are simultaneously flipped then returned to
	 * the deck one by one.
	 * 
	 * @param scene the game node tree
	 * @param game the current Game object
	 */
	private void returnComm(Scene scene, Game game) {
		SequentialTransition returnCards = new SequentialTransition();
		ParallelTransition hideCards = new ParallelTransition();
		returnCards.getChildren().add(hideCards); //The parallel hide animation is played before any returns to the deck
		
		Boolean fast = false;
		if (game.isUserFolded())
			fast = true;
		
		for (int commCard = 0; commCard < 5; commCard++) { //Each of the five community cards is iterated through
			ImageView commFront = (ImageView) scene.lookup("#commFront" + commCard);
			ImageView commBack = (ImageView) scene.lookup("#commBack" + commCard);
			hideCards.getChildren().add(flipCard(commBack, commFront, true, fast));
			returnCards.getChildren().add(moveCard(scene, commBack, commFront, true, fast));
		}
		
		returnCards.setOnFinished(e -> returnAllHoles(scene, game)); //At the end of animation the return animation for the hole cards is played
		returnCards.play();
	}
	
	/**
	 * At the beginning of each round of play after the deck is shuffled by its
	 * animation each player is dealt their hole cards, beginning with the
	 * first card clockwise then the second clockwise.
	 * 
	 * @param scene the game node tree
	 * @param game the current Game object
	 */
	private void dealHoles(Scene scene, Game game) {
		SequentialTransition dealAllHoles = new SequentialTransition();
		
		ParallelTransition showUserCards = new ParallelTransition();
		
		for (int card = 1; card <= 2; card++) { //Each of a player's two cards is iterated through
			for (Player player : game.getPlayers()) { //Each player is iterated through
				ImageView cardBack = (ImageView) scene.lookup("#" + player.getName() + "Card" + card + "Back");
				ImageView cardFront = (ImageView) scene.lookup("#" + player.getName() + "Card" + card);
				
				Boolean fast = false;
				if (game.isUserFolded())
					fast = true;
				
				if (player instanceof Human) { //If the player is the user their cards are set to reveal
					SequentialTransition cardFlip = flipCard(cardBack, cardFront, false, fast);
					showUserCards.getChildren().add(cardFlip);
				}
				
				SequentialTransition cardMotion = moveCard(scene, cardBack, cardFront, false, fast);
				dealAllHoles.getChildren().add(cardMotion);
			}
		}
		
		dealAllHoles.setOnFinished(e -> showUserCards.play()); //The user's cards are revealed to them once all are dealt
		
		showUserCards.setOnFinished(e -> interRound(scene, game)); //The net animation of this method is followed by the first round notification
		
		dealAllHoles.play();
	}
	
	/**
	 * To return a player's cards to the deck image they are first
	 * simultaneously flipped (if applicable) then returned to the deck one at
	 * a time.
	 * 
	 * @param scene the game node tree
	 * @param player the player whose hole cards need to be returned
	 * @param flip if the player's cards also need to be flipped, for a folding player and end round returns
	 * @return the return hole animation
	 */
	private SequentialTransition returnHole(Scene scene, Player player, Boolean flip, Boolean fast) {
		ParallelTransition hideCards = new ParallelTransition();
		
		SequentialTransition returnCards = new SequentialTransition();
		returnCards.getChildren().add(hideCards); //The cards are flipped before returning (if applicable)
		
		for (int card = 1; card <= 2; card++) { //Each card is iterated to be returned
			ImageView cardBack = (ImageView) scene.lookup("#" + player.getName() + "Card" + card + "Back");
			ImageView cardFront = (ImageView) scene.lookup("#" + player.getName() + "Card" + card);
			
			if (flip) { //If the card is needed to be flipped it is set to do so
				SequentialTransition hideCard = flipCard(cardBack, cardFront, true, fast);
				hideCards.getChildren().add(hideCard);
			}
			
			SequentialTransition returnCard = moveCard(scene, cardBack, cardFront, true, fast);
			returnCards.getChildren().add(returnCard);
		}
		
		return returnCards;
	}
	
	/**
	 * To return all of the players' hole cards at the end of a round of play
	 * each player is passed to the returnHole method to produce a returning
	 * animation which is added to the whole method's animation. On the end of
	 * the animation the Game object is reset for the next round of play and
	 * a new round of play is started.
	 * 
	 * @param scene the game node tree
	 * @param game the current Game object
	 */
	private void returnAllHoles(Scene scene, Game game) {
		SequentialTransition returnCards = new SequentialTransition();
		
		for (Player player : game.getPlayers()) { //Each player is iterated
			Boolean fast = false;
			if (game.isUserFolded())
				fast = true;
			returnCards.getChildren().add(returnHole(scene, player, true, fast));
		}
		
		returnCards.setOnFinished(new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) { //On the animation finish a new round of play is started
				game.setupRound();
				startPlayRound(scene, game);
			}
		});
		
		returnCards.play();
	}
	
	/**
	 * The following method controls the animation of a card either being drawn
	 * from or returned to the deck. 
	 * 
	 * @param scene the game node tree
	 * @param cardBack the image of the back of the card
	 * @param cardFront the image of the front of the card
	 * @param reversed whether or not the animation should be reversed
	 * @param fast whether or not the animation should move fast
	 * @return
	 */
	private SequentialTransition moveCard(Scene scene, ImageView cardBack, ImageView cardFront, Boolean reversed, Boolean fast) {
		ImageView subCard; //Depending on if the card needs to be drawn or returned the appropriate dummy image is found
		if (reversed)
			subCard = (ImageView) scene.lookup("#returnCard");
		else
			subCard = (ImageView) scene.lookup("#drawCard");
		
		double originX = subCard.localToScene(subCard.getBoundsInLocal()).getMinX(); //The to and from coordinates are calculated
		double originY = subCard.localToScene(subCard.getBoundsInLocal()).getMinY(); 
		double targetX = cardBack.localToScene(cardBack.getBoundsInLocal()).getMinX(); 
		double targetY = cardBack.localToScene(cardBack.getBoundsInLocal()).getMinY(); 
		
		SequentialTransition fullMotion = new SequentialTransition();
		
		TranslateTransition moveCard = new TranslateTransition(); //The deck-to-card motion is set
		moveCard.setNode(subCard);
		if (reversed || fast) //In return motion this animation will be nearly instantaneous, also for fast
			moveCard.setDuration(Duration.millis(1));
		else
			moveCard.setDuration(Duration.millis(250));
		moveCard.setFromX(0.0);
		moveCard.setFromY(0.0);
		moveCard.setByX(targetX - originX);
		moveCard.setByY(targetY - originY);
		
		TranslateTransition returnCard = new TranslateTransition(); //The card-to-deck motion is set
		returnCard.setNode(subCard);
		if (reversed && !fast) //In dealing animation and fast this animation will be nearly instantaneous
			returnCard.setDuration(Duration.millis(250));
		else
			returnCard.setDuration(Duration.millis(1));
		returnCard.setByX(originX - targetX);
		returnCard.setByX(originY - targetY);
		returnCard.setToX(0.0);
		returnCard.setToY(0.0);
		
		fullMotion.getChildren().addAll(moveCard, returnCard);
		
		moveCard.setOnFinished(new EventHandler<ActionEvent>() { //On the end of the deck-to-card motion the target card will either be visible or invisible
			@Override
			public void handle(ActionEvent event) {
				if (reversed) {
					cardBack.setVisible(false);
					cardFront.setVisible(false);
				}
				else {
					cardBack.setVisible(true);
					cardFront.setVisible(true);
				}
			}
		});
		
		return fullMotion; //The full motion is returned for further setOnFinished actions
	}
	
	/**
	 * The set of ImageViews passed into this method will be swapped by an
	 * appropriate flipping animation. The positions of the card back and 
	 * front imageviews are swapped if a hiding animation is required over a
	 * revealing animation.
	 * 
	 * @param cardBack the ImageView of the card's back
	 * @param cardFront the ImageView of the card's face
	 * @param reversed whether or not the animation should be reversed
	 * @param fast whether or not the animation should be fast
	 */
	private SequentialTransition flipCard(ImageView cardBack, ImageView cardFront, Boolean reversed, Boolean fast) {
		SequentialTransition fullMotion = new SequentialTransition();
		
		ScaleTransition hide = new ScaleTransition(); //The back is scaled to a line to be invisible
		hide.setByX(-1);
		if (fast)
			hide.setDuration(Duration.millis(1));
		else
			hide.setDuration(Duration.millis(200));
		if (reversed)
			hide.setNode(cardFront);
		else
			hide.setNode(cardBack);
		
		ScaleTransition show = new ScaleTransition(); //The face is returned from a line to full size
		show.setByX(1);
		if (fast)
			show.setDuration(Duration.millis(1));
		else
			show.setDuration(Duration.millis(200));
		if (reversed)
			show.setNode(cardBack);
		else
			show.setNode(cardFront);
		
		fullMotion.getChildren().addAll(hide, show);
		
		return fullMotion;
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
