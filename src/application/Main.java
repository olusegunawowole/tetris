package application;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Game;
import model.Position;
import model.Tetromino;
import reusable.Alert;
import reusable.AnimatedLabel;
import reusable.Control;
import reusable.ModalDialog;
import reusable.MyHyperlink;
import reusable.ToggleSwitch;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * 
 * @author Olusegun This class contains the game GUI
 *
 */
public class Main extends Application {
	// Set it to file path of the directory containing the project folder
	public static final String PROJECT_PATH = "C:\\Users\\peace\\eclipse-workspace\\";

	private double height = 800;
	private double width = height / 2;
	private Canvas board, previewCanvas;
	private GraphicsContext g;
	private Game game;

	private int nRow = 20;
	private int nCol = 10;
	private int squareSize = 40;
	private int speed = 60;

	// Panes
	private StackPane root, leftPane;
	private VBox rightPane, controlPane, infoPane;
	private HBox container;
	private ModalDialog dialog;
	private GameOverPane gameOverPane;

	// Labels
	private Label currentLevel, highLevel, currentLeveLabel, highLevelLabel, highScorelLabel, currentScorelLabel,
			themeLabel, soundLabel, infoLabel, linesCleared, linesClearedLabel, highLinesCleared, highLinesClearedLabel,
			highHeaderLabel, currentHeaderLabel;
	private AnimatedLabel highScore, currentScore;

	// Switches and buttons
	private ToggleSwitch themeSwitch;
	private ToggleSwitch soundSwitch;
	private Button newGameBtn;
	private Button pauseBtn;
	private Button exitBtn;
	private Button infoBtn;
	private Control control;

	public static final String BTN_DARK_STYLE = "-fx-background-color: transparent; -fx-color: rgb(0, 0, 0); -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: darkgray; -fx-cursor: hand";
	public static final String BTN_DARK_HOVER_STYLE = "-fx-background-color: white; -fx-color: rgb(255, 255, 255); -fx-border-width: 1px; -fx-border-color: white;";
	public static final String BTN_LIGHT_STYLE = "-fx-background-color: transparent; -fx-color: rgb(255, 255, 255); -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: darkgray; -fx-cursor: hand";
	public static final String BTN_LIGHT_HOVER_STYLE = "-fx-background-color: black; -fx-color: rgb(0, 0, 0); -fx-border-width: 1px; -fx-border-color: black;";

	private String fontColor = "black";
	private String backgroundColor = "white";
	private String btnStyle = BTN_LIGHT_STYLE;
	private String btnStyleHover = BTN_LIGHT_HOVER_STYLE;

	private AnimationTimer timer;

	private boolean paused, pausedBeforeAction, gameOver, softDrop;

	private File gameDataFile, configFile;
	private DecimalFormat format;

	@Override
	public void start(Stage stage) {
		try {
			// Contains state of game before the last closure
			gameDataFile = new File(PROJECT_PATH, "tetris\\src\\resource\\gameData.xml");
			// Contains settings of the game
			configFile = new File(PROJECT_PATH, "tetris\\src\\resource\\config.xml");
			// Setup left pane
			board = new Canvas(width, height);
			g = board.getGraphicsContext2D();
			game = new Game();
			game.read(gameDataFile);

			draw();
			leftPane = new StackPane(board);
			leftPane.setPadding(new Insets(0, 0, 0, 8));
			leftPane.setAlignment(Pos.TOP_LEFT);

			// Setup right pane
			rightPane = new VBox(3);
			rightPane.setAlignment(Pos.TOP_CENTER);
			rightPane.setId("rightPane");
			rightPane.setPadding(new Insets(7, 5, 0, 5));
			rightPane.setPrefWidth(300);
			rightPane.setMaxHeight(800);
			controlPane = new VBox(5);
			controlPane.setAlignment(Pos.TOP_CENTER);

			container = new HBox(5, leftPane, rightPane);
			container.setMaxSize(690, 670);
			container.setStyle("-fx-border-color: darkgray; -fx-border-width: 2px; -fx-border-style: solid");
			container.setPadding(new Insets(7.5, 7.5, 7.5, 0));
			displaySetup();
			controlSetup();

			Alert.setFontColor(fontColor);
			Alert.setBorderColor(fontColor);
			Alert.setBackgroundColor(backgroundColor);

			rightPane.getChildren().addAll(previewCanvas, controlPane);
			dialog = new ModalDialog();
			gameOverPane = new GameOverPane();
			root = new StackPane(container, dialog);
			root.setStyle("-fx-background-color: " + backgroundColor);
			root.setPadding(new Insets(10));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.sizeToScene();
			String imageUrl = "file:///" + PROJECT_PATH + "tetris\\src\\resource\\logo.png";
			Image icon = new Image(imageUrl);
			stage.getIcons().add(icon);
			stage.show();
			stage.setOnCloseRequest(e -> {
				e.consume();
				exit(stage);
			});
			readConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes display objects e.g. labels
	 */
	private void displaySetup() {
		GridPane pane = new GridPane();
		pane.setPadding(new Insets(0, 5, 25, 5));

		format = new DecimalFormat("#.##");
		format.setGroupingUsed(true);
		format.setGroupingSize(3);

		highHeaderLabel = createLabel("All Time High");
		pane.add(highHeaderLabel, 0, 0, 2, 1);
		GridPane.setHalignment(highHeaderLabel, HPos.CENTER);

		highScorelLabel = createLabel("Score");
		highScore = new AnimatedLabel(0);
		highScore.setFontColor(fontColor);
		addToGridPane(pane, highScorelLabel, highScore, 1);

		highLevelLabel = createLabel("Level");
		highLevel = createLabel("0");
		addToGridPane(pane, highLevelLabel, highLevel, 2);

		highLinesClearedLabel = createLabel("Line");
		highLinesCleared = createLabel("0");
		addToGridPane(pane, highLinesClearedLabel, highLinesCleared, 3);

		currentHeaderLabel = createLabel("Current Game");
		pane.add(currentHeaderLabel, 0, 4, 2, 1);
		GridPane.setHalignment(currentHeaderLabel, HPos.CENTER);

		currentScorelLabel = createLabel("Score");
		currentScore = new AnimatedLabel(0);
		currentScore.setFontColor(fontColor);
		addToGridPane(pane, currentScorelLabel, currentScore, 5);

		currentLeveLabel = createLabel("Level");
		currentLevel = createLabel("0");
		addToGridPane(pane, currentLeveLabel, currentLevel, 6);

		linesClearedLabel = createLabel("Line");
		linesCleared = createLabel("0");
		addToGridPane(pane, linesClearedLabel, linesCleared, 7);

		rightPane.getChildren().addAll(pane);
		previewCanvas = new Canvas(152, 152);
		drawPreview();
		updateDisplay();

		// Info pane setup
		String imageUrl = "file:///" + PROJECT_PATH + "tetris\\src\\resource\\keyboard.png";
		Image img = new Image(imageUrl);
		ImageView iv = new ImageView(img);

		Label refLabel = createLabel("References");
		MyHyperlink refLink = new MyHyperlink("Tetris N-Blox", "https://www.freetetris.org/game.php");
		MyHyperlink refLink2 = new MyHyperlink("Tetris Guideline", "https://tetris.fandom.com/wiki/Tetris_Guideline");

		String style = "-fx-background-color: transparent; -fx-color: rgb(255, 255, 255); -fx-border-width: 1.5px; -fx-border-style: solid; -fx-border-color: black; -fx-cursor: hand";
		Button closeBtn = createButton("Close");

		closeBtn.setStyle(style);
		closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
				"-fx-background-color: black; -fx-color: rgb(0, 0, 0); -fx-border-width: 1.5px; -fx-border-style: solid; -fx-border-color: black; -fx-cursor: hand"));
		closeBtn.setOnMouseExited(e -> closeBtn.setStyle(style));
		closeBtn.setOnAction(e -> {
			if (!pausedBeforeAction)
				pause();
			dialog.close();
		});
		infoPane = new VBox(15, iv, refLabel, refLink, refLink2, new StackPane(closeBtn));
		infoPane.setStyle(
				"-fx-background-color: #DDEEFF; -fx-border-color:black; -fx-border-width: 2px; -fx-border-style: solid");
		infoPane.setPadding(new Insets(25));
		infoPane.setMaxSize(500, 400);
	}

	/**
	 * This function initializes buttons and other controls. It also maps functions
	 * to keys
	 */
	private void controlSetup() {
		newGameBtn = createButton("New Game");
		newGameBtn.setOnAction(e -> {
			if (!paused) {
				pause(); // pause game
				pausedBeforeAction = false;
			}
			String msg = "";
			if (gameOver) {
				msg = "Proceed with new game?";

			} else {
				msg = "There is an existing game.\nProceed with new game anyway?";
			}
			Alert alert = new Alert(msg, "Yes", "No");
			alert.setButtonOnAction(e1 -> {
				newGame();
				dialog.close();
			}, 0);
			alert.setButtonOnAction(e1 -> {
				if (!pausedBeforeAction)
					pause(); // unpause game
				dialog.close();
			}, 1);
			dialog.open(alert);
		});

		pauseBtn = createButton("Pause");
		pauseBtn.setOnAction(e -> {
			pause();
			pausedBeforeAction = paused;
		});

		infoBtn = createButton("Info");
		infoBtn.setOnAction(e -> {
			dialog.open(infoPane);
			if (!paused) {
				pausedBeforeAction = false;
				pause();
			}
		});
		exitBtn = createButton("Exit");
		exitBtn.setOnAction(e -> {
			Stage stage = (Stage) root.getScene().getWindow();
			exit(stage);
		});

		GridPane switchPane = new GridPane();
		ColumnConstraints constraints = new ColumnConstraints(70);
		constraints.setPercentWidth(25);
		switchPane.getColumnConstraints().add(constraints);
		constraints.setPercentWidth(75);
		switchPane.getColumnConstraints().add(constraints);
		switchPane.setStyle("-fx-border-width: 1px; -fx-border-color: darkgrey");
		switchPane.setPadding(new Insets(10, 0, 10, 15));
		switchPane.setVgap(15);
		switchPane.setMaxWidth(200);

		themeLabel = createLabel("Dark Theme");
		switchPane.add(themeLabel, 0, 0);
		themeSwitch = new ToggleSwitch();
		themeSwitch.setTranslateX(15);
		switchPane.add(themeSwitch, 1, 0);

		soundLabel = createLabel("Sound");
		switchPane.add(soundLabel, 0, 1);
		soundSwitch = new ToggleSwitch();
		soundSwitch.setTranslateX(15);
		switchPane.add(soundSwitch, 1, 1);

		control = new Control();
		control.getRightButton().setOnMouseClicked(e -> moveRight());
		control.getLeftButton().setOnMouseClicked(e -> moveLeft());
		control.getUpButton().setOnMouseClicked(e -> rotateRight());
		control.getCenterButton().setOnMouseClicked(e -> hardDrop());
		control.getCenterButton().setDisable(true);
		control.getDownButton().setOnMousePressed(e -> {
			softDrop(true);
			control.getDownButton().setEffect(true);
		});
		control.getDownButton().setOnMouseReleased(e -> {
			softDrop(false);
			control.getDownButton().setEffect(false);
		});

		infoLabel = createLabel("Developed by Olusegun Awowole");
		controlPane.getChildren().addAll(newGameBtn, pauseBtn, infoBtn, exitBtn, switchPane, control, infoLabel);
		controlPane.setAlignment(Pos.TOP_CENTER);

		themeSwitch.stateProperty().addListener((obs, oldState, newState) -> {
			if (newState) {
				btnStyle = BTN_DARK_STYLE;
				btnStyleHover = BTN_DARK_HOVER_STYLE;
				control.setHoverColor(Color.WHITE);
				fontColor = "white";
				backgroundColor = "black";
			} else {
				btnStyle = BTN_LIGHT_STYLE;
				btnStyleHover = BTN_LIGHT_HOVER_STYLE;
				control.setHoverColor(Color.BLACK);
				fontColor = "black";
				backgroundColor = "white";
			}
			newGameBtn.setStyle(btnStyle);
			pauseBtn.setStyle(btnStyle);
			exitBtn.setStyle(btnStyle);
			infoBtn.setStyle(btnStyle);
			highScore.setFontColor(fontColor);
			currentScore.setFontColor(fontColor);
			highHeaderLabel.setStyle("-fx-text-fill: " + fontColor);
			currentHeaderLabel.setStyle("-fx-text-fill: " + fontColor);
			highLinesClearedLabel.setStyle("-fx-text-fill: " + fontColor);
			highLinesCleared.setStyle("-fx-text-fill: " + fontColor);
			linesCleared.setStyle("-fx-text-fill: " + fontColor);
			linesClearedLabel.setStyle("-fx-text-fill: " + fontColor);
			highLevel.setStyle("-fx-text-fill: " + fontColor);
			currentLevel.setStyle("-fx-text-fill: " + fontColor);
			currentLeveLabel.setStyle("-fx-text-fill: " + fontColor);
			highLevelLabel.setStyle("-fx-text-fill: " + fontColor);
			highScorelLabel.setStyle("-fx-text-fill: " + fontColor);
			currentScorelLabel.setStyle("-fx-text-fill: " + fontColor);
			themeLabel.setStyle("-fx-text-fill: " + fontColor);
			soundLabel.setStyle("-fx-text-fill: " + fontColor);
			infoLabel.setStyle("-fx-text-fill: " + fontColor);
			root.setStyle("-fx-background-color: " + backgroundColor);
			gameOverPane.setTheme();
			Alert.setFontColor(fontColor);
			Alert.setBorderColor(fontColor);
			Alert.setBackgroundColor(backgroundColor);
		});

		container.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
			if (key.getCode() == KeyCode.SPACE) {
				hardDrop();
				key.consume();
			} else if (key.getCode() == KeyCode.ESCAPE) {
				pause();
			} else if (key.getCode() == KeyCode.UP) {
				rotateRight();
			} else if (key.getCode() == KeyCode.LEFT) {
				moveLeft();
			} else if (key.getCode() == KeyCode.DOWN) {
				softDrop(true);
			} else if (key.getCode() == KeyCode.RIGHT) {
				moveRight();
			} else if (key.getCode() == KeyCode.Z) {
				rotateLeft();
			}
		});

		container.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.DOWN) {
				softDrop(false);
			}
		});

		timer = new AnimationTimer() {
			int frame;

			@Override
			public void handle(long now) {
				if (softDrop)
					speed = 4;
				else
					speed = (60 - 6 * (game.getLevel() - 1));
				if (frame % speed == 0) {
					play();
					if (gameOver) {
						gameOver();
					}
					draw();
				}
				frame++;
			}
		};
		timer.start();
	}

	private void updateDisplay() {
		highScore.setValue(game.getHighScore());
		currentScore.setValue(game.getScore());
		highLevel.setText(format.format(game.getHighLevel()));
		currentLevel.setText(format.format(game.getLevel()));
		linesCleared.setText(format.format(game.getLineCount()));
		highLinesCleared.setText(format.format(game.getHighLineCount()));
	}

	private void checkGame() {
		timer.stop();
		game.setFix();
		int point = game.calculatePoint();
		if (point > 0) {
			clearFilledRows();
			return;
		}
		updateDisplay();

		game.spawn();
		drawPreview();
		draw();
		gameOver = game.isGameOver();

		timer.start();
	}

	private void play() {
		if (paused || gameOver)
			return;
		boolean down = game.getTetromino().moveDown();
		if (down && game.getTetromino().getCenterPosition().getRow() > 0)
			playSound();
		if (!down) {
			checkGame();
		}
	}

	private void hardDrop() {
		if (paused || gameOver)
			return;
		game.hardDrop();
		playSound();
		checkGame();
	}

	private void gameOver() {
		timer.stop();
		pauseBtn.setDisable(true);
		control.setDisable(true);
		dialog.open(gameOverPane);
	}

	private void pause() {
		if (gameOver)
			return;
		paused = !paused;
		if (paused) {
			timer.stop();
			pauseBtn.setText("Unpause");
		} else {
			timer.start();
			pauseBtn.setText("Pause");
		}
		control.setDisable(paused);
	}

	private void newGame() {
		timer.stop();
		game.createNewGame();
		updateDisplay();
		gameOver = false;
		paused = false;
		control.setDisable(paused);
		pauseBtn.setText("Pause");
		pauseBtn.setDisable(false);
		draw();
		drawPreview();
		timer.start();
	}

	private void moveLeft() {
		if (paused || gameOver)
			return;
		game.getTetromino().moveLeft();
		playSound();
		draw();
	}

	private void moveRight() {
		if (paused || gameOver)
			return;
		game.getTetromino().moveRight();
		playSound();
		draw();
	}

	private void softDrop(boolean softDrop) {
		if (paused || gameOver)
			return;
		this.softDrop = softDrop;
	}

	private void rotateLeft() {
		if (paused || gameOver)
			return;
		game.getTetromino().rotateAntiClockwise();
		playSound();
		draw();
	}

	private void rotateRight() {
		if (paused || gameOver)
			return;
		game.getTetromino().rotateClockwise();
		playSound();
		draw();
	}

	private void drawPreview() {
		GraphicsContext g = previewCanvas.getGraphicsContext2D();
		double w = previewCanvas.getWidth();
		double h = previewCanvas.getHeight();
		g.clearRect(0, 0, w, h);
		g.setStroke(Color.GRAY);
		g.setLineWidth(.5);
		int squareSize = 25;
		Tetromino tetromino = game.getNextTetromino();
		Position[] positions = Tetromino.setAndGetPositions(tetromino, 3, 2);
		g.setFill(getTetrominoColor(tetromino.getValue()));
		for (Position p : positions) {
			double x = squareSize * p.getCol();
			double y = squareSize * p.getRow();
			g.fillRect(x, y, squareSize, squareSize);
			g.save();
			g.setFill(Color.rgb(255, 255, 255, .4));

			g.setEffect(new DropShadow(3, 0, 2, Color.BLACK));
			g.fillRoundRect(x + 7.5, y + 7.5, squareSize - 15, squareSize - 15, 30.5, 30.5);
			g.strokeRoundRect(x + 5, y + 4.5, squareSize - 10, squareSize - 10, 30.5, 30.5);

			g.restore();
		}

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 7; col++) {
				double x = squareSize * col;
				double y = squareSize * row;
				g.strokeRect(x, y, squareSize, squareSize);

			}
		}
		g.setLineWidth(2);
		g.strokeRect(1, 1, w - 2, h - 2);
	}

	private void draw() {
		g.clearRect(0, 0, width, height); // clear canvas
		g.setLineWidth(.5);
		g.setStroke(Color.GRAY);
		for (Position p : game.getTetromino().getGhostPiecePositions()) {
			if (p == null)
				continue;
			double x = squareSize * p.getCol();
			double y = squareSize * p.getRow();
			drawGhostPiece(game.getTetromino().getValue(), x, y);
		}
		for (int row = 0; row < nRow; row++) {
			for (int col = 0; col < nCol; col++) {
				double x = squareSize * col;
				double y = squareSize * row;
				char ch = game.getCell(row, col).getValue();
				if (ch != '*') {
					g.setFill(getTetrominoColor(ch));
					g.fillRect(x, y, squareSize, squareSize);
					g.save();
					g.setFill(Color.rgb(255, 255, 255, .4));
					// g.setEffect(new DropShadow(3, 0, 2, Color.BLACK));
					g.fillRoundRect(x + 7.5, y + 7.5, squareSize - 15, squareSize - 15, 30.5, 30.5);
					g.strokeRoundRect(x + 5, y + 4.5, squareSize - 10, squareSize - 10, 30.5, 30.5);
					g.restore();
				}
				g.strokeRect(x, y, squareSize, squareSize);
			}
		}
		g.setStroke(Color.DARKGRAY);
		g.setLineWidth(4);
		g.strokeRect(0, 0, width, height);
	}

	private void drawGhostPiece(char ch, double x, double y) {
		g.save();
		g.setLineWidth(2);
		g.setStroke(getTetrominoColor(ch));
		g.strokeRect(x + 3, y + 3, squareSize - 6, squareSize - 6);

		g.setEffect(new DropShadow(3, 0, 2, getTetrominoColor(ch)));
		g.strokeRoundRect(x + 8, y + 7.5, squareSize - 16, squareSize - 16, 30.5, 30.5);
		g.restore();
	}

	private Color getTetrominoColor(char ch) {
		switch (ch) {
		case 'I':
			return Color.AQUA;
		case 'J':
			return Color.BLUE;
		case 'L':
			return Color.ORANGE;
		case 'O':
			return Color.YELLOW;
		case 'S':
			return Color.LIGHTGREEN;
		case 'T':
			return Color.PURPLE;
		case 'Z':
			return Color.RED;
		default:
			return null;
		}
	}

	private void clearFilledRows() {
		ArrayList<Integer> rowList = game.getFilledRows();
		if (rowList.isEmpty()) {
			return;
		}
		IntegerProperty colProperty = new SimpleIntegerProperty(-1);
		colProperty.addListener(e -> {
			int col = colProperty.get();
			for (int row : rowList) {
				game.clearCell(row, col);
			}
			draw();
		});

		KeyValue keyValue = new KeyValue(colProperty, 9);
		KeyFrame keyFrame = new KeyFrame(Duration.millis(250), keyValue);
		Timeline timeline = new Timeline(keyFrame);
		timeline.play();
		timeline.setOnFinished(e -> {
			for (int row : rowList) { // to ensure all columns in the filled rows are cleared
				game.clearRow(row);
			}
			for (int row = nRow - 2; row > -1; row--) {
				game.moveRowFrom(row);
			}
			updateDisplay();
			game.spawn();
			drawPreview();
			draw();
			gameOver = game.isGameOver();
			timer.start();
			draw();
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void playSound() {
		if (!soundSwitch.getStatus())
			return;
		String audioFilePath = PROJECT_PATH + "tetris\\src\\resource\\sound.mp3";
		String str = new File(audioFilePath).toURI().toString();
		Media soundMedia = new Media(str);
		MediaPlayer soundPlayer = new MediaPlayer(soundMedia);
		soundPlayer.setCycleCount(1);
		soundPlayer.play();
	}

	/**
	 * This method is called when a user makes a request to close the window.
	 * 
	 * @param stage
	 */
	private void exit(Stage stage) {
		if (gameOver) { // Close without warning
			gameOver = true;
			game.save(gameDataFile);
			saveConfig();
			stage.close();
		} else {
			if (paused) {
				pausedBeforeAction = true;
			} else {
				pause();
				pausedBeforeAction = false;
			}
			String msg = "There is an ongoing game.\nDo you want to save it before exiting?";
			Alert alert = new Alert(msg, "Save", "Don't Save", "Cancel");
			alert.setButtonOnAction(e -> { // User selects save
				game.save(gameDataFile);
				saveConfig();
				stage.close();
			}, 0);
			alert.setButtonOnAction(e -> {
				// User selects Don't Save - game will be saved but new game will be created at
				// the next start of Application.
				gameOver = true; // No existing game
				game.save(gameDataFile);
				saveConfig();
				stage.close();
			}, 1);
			alert.setButtonOnAction(e -> { // User selects cancel - return to game.
				dialog.close();
				if (!pausedBeforeAction) {
					pause(); // unpause game
				}
			}, 2);
			if (dialog.isOpen())
				dialog.set(alert);
			else
				dialog.open(alert);
		}
	}

	private void saveConfig() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(configFile));
			out.println("<?xml version=\"1.0\"?>");
			out.println("<config version=\"1.0\">");
			out.println("    <theme value='" + themeSwitch.getStatus() + "' />");
			out.println("    <sound value='" + soundSwitch.getStatus() + "' />");
			out.println("    <paused value='" + paused + "' />");
			out.println("    <gameover value='" + gameOver + "' />");
			out.println("</config>");
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readConfig() {
		if (!configFile.exists())
			return;
		try {
			Document xmldoc;
			DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmldoc = docReader.parse(configFile);
			Element rootElement = xmldoc.getDocumentElement();
			if (!rootElement.getNodeName().equals("config"))
				throw new Exception("config file is invalid.");
			String version = rootElement.getAttribute("version");
			double versionNumber = Double.parseDouble(version);
			if (versionNumber > 1.0)
				throw new Exception("Data file requires a newer version of tetris.");

			NodeList nodes = rootElement.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i) instanceof Element) {
					Element element = (Element) nodes.item(i);
					if (element.getTagName().equals("theme")) {
						boolean state = Boolean.valueOf(element.getAttribute("value"));
						themeSwitch.switchTo(state);
					} else if (element.getTagName().equals("sound")) {
						boolean state = Boolean.valueOf(element.getAttribute("value"));
						soundSwitch.switchTo(state);
					} else if (element.getTagName().equals("gameover")) {
						boolean state = Boolean.valueOf(element.getAttribute("value"));
						if (state) {
							newGame();
						}
					} else if (element.getTagName().equals("paused")) {
						boolean state = Boolean.valueOf(element.getAttribute("value"));
						if (state) {
							pause();
							pausedBeforeAction = true;
						}
						paused = state;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Label createLabel(String text) {
		Label label = new Label(text);
		label.setStyle("-fx-text-fill: " + fontColor);
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		return label;
	}

	private Button createButton(String text) {
		Button btn = new Button(text);
		btn.setPrefWidth(150);
		btn.setStyle(btnStyle);
		btn.setOnMouseEntered(e -> btn.setStyle(btnStyleHover));
		btn.setOnMouseExited(e -> btn.setStyle(btnStyle));
		return btn;
	}

	private void addToGridPane(GridPane pane, Label title, Label info, int row) {
		pane.add(title, 0, row);
		pane.add(info, 1, row);
		GridPane.setHgrow(info, Priority.ALWAYS);
		GridPane.setHalignment(info, HPos.RIGHT);
	}

	private class GameOverPane extends VBox {
		private Label label;
		private Button replayBtn;
		private Button cancelBtn;

		public GameOverPane() {
			replayBtn = createButton("Replay");
			replayBtn.setOnAction(e -> {
				newGame();
				dialog.close();
			});
			cancelBtn = createButton("Cancel");
			cancelBtn.setOnAction(e -> {
				dialog.close();
			});
			label = new Label("Game Over");
			label.setStyle("-fx-text-fill: " + fontColor);
			label.setFont(Font.font("Arial", FontWeight.NORMAL, 100));
			setAlignment(Pos.BASELINE_CENTER);
			setStyle("-fx-opacity: .5; -fx-border-color: " + fontColor + "; -fx-border-width: 2px; -fx-background-color: "
					+ backgroundColor);
			setSpacing(50);
			setMaxHeight(250);
			setMaxWidth(600);
			setPadding(new Insets(0, 0, 30, 0));
			HBox btnBox = new HBox(20, replayBtn, cancelBtn);
			btnBox.setAlignment(Pos.BASELINE_CENTER);
			getChildren().addAll(label, btnBox);
		}

		private void setTheme() {
			label.setStyle("-fx-text-fill: " + fontColor);
			setStyle("-fx-border-color: " + fontColor + "; -fx-border-width: 2px; -fx-background-color: "
					+ backgroundColor);
			replayBtn.setStyle(btnStyle);
			cancelBtn.setStyle(btnStyle);

		}

	}
}