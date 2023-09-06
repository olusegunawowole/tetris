package model;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Game {
	private Cell[][] board;
	private int nRow = 20;
	private int nCol = 10;
	private int level = 1;
	private int highLevel = 1;
	private int score;
	private int highScore;
	private int lineCount;
	private int highLineCount;
	private int distance;
	private Tetromino tetromino;
	private Tetromino nextTetromino;
	private ArrayList<Character> letters;

	public Game() {
		board = new Cell[nRow][nCol];
		for (int row = 0; row < nRow; row++) {
			for (int col = 0; col < nCol; col++) {
				board[row][col] = new Cell();
			}
		}
		letters = new ArrayList<>();
		Tetromino.setBoardState(board);
		spawn();
	}

	public int getHighLineCount() {
		return highLineCount;
	}

	public void setHighLineCount(int highLineCount) {
		this.highLineCount = highLineCount;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getHighLevel() {
		return highLevel;
	}

	public void setHighLevel(int highLevel) {
		this.highLevel = highLevel;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public boolean inbounds(int row, int col) {
		return row >= 0 && col >= 0 && row < nRow && col < nCol;
	}

	public Cell getCell(int row, int col) {
		if (inbounds(row, col)) {
			return board[row][col];
		}
		return null;
	}

	public Tetromino getTetromino() {
		return tetromino;
	}

	public Tetromino getNextTetromino() {
		return nextTetromino;
	}

	private char getLetter() {
		if (letters.isEmpty()) {
			letters.add('I');
			letters.add('J');
			letters.add('L');
			letters.add('O');
			letters.add('S');
			letters.add('T');
			letters.add('Z');
		}
		int rand = (int) (Math.random() * letters.size());
		return letters.remove(rand);
	}

	public void spawn() {
		if (nextTetromino != null) {
			tetromino = nextTetromino;
		} else {
			tetromino = new Tetromino(getLetter());
		}
		tetromino.set();
		nextTetromino = new Tetromino(getLetter());
	}

	public boolean isRowBlank(int row) {
		if (row < 0 || row >= nRow) {
			return false;
		}
		int counter = 0;
		for (int col = 0; col < nCol; col++) {
			if (getCell(row, col).isEmpty()) {
				counter++;
			}
		}
		return counter == 10;
	}

	public boolean isRowFilled(int row) {
		if (row < 0 || row >= nRow) {
			return false;
		}
		int counter = 0;
		for (int col = 0; col < nCol; col++) {
			if (!getCell(row, col).isEmpty()) {
				counter++;
			}
		}
		return counter == 10;
	}

	public ArrayList<Integer> getFilledRows() {
		ArrayList<Integer> list = new ArrayList<>();
		for (int row = nRow - 1; row > -1; row--) {
			if (isRowFilled(row)) {
				list.add(row);
			}
		}
		return list;
	}

	public void setFix() {
		for (int row = nRow - 1; row > -1; row--) {
			if (isRowBlank(row))
				return;
			for (int col = 0; col < nCol; col++) {
				if (!board[row][col].isEmpty())
					board[row][col].setFixed(true);
			}
		}
	}

	public int getNRowOccupied() {
		int count = 0;
		for (int row = nRow - 1; row > -1; row--) {
			for (int col = 0; col < nCol; col++) {
				Cell cell = getCell(row, col);
				if (!cell.isEmpty() && cell.isFixed()) {
					count++;
					break;
				}
			}
		}
		return count;
	}

	public void clearRow(int row) {
		if (row < 0 || row >= nRow)
			return;
		for (int col = 0; col < nCol; col++) {
			board[row][col].clear();
		}
	}

	public void clearFilledRows() {
		ArrayList<Integer> rowList = getFilledRows();
		for (int row : rowList) {
			clearRow(row);
		}
		if (rowList.isEmpty())
			return;
		for (int row = nRow - 2; row > -1; row--) {
			moveRowFrom(row);
		}
	}

	public void clearCol(int col) {
		if (col < 0 || col >= nCol)
			return;
		for (int row = 0; row < nRow; row++) {
			board[row][col].clear();
		}
	}

	public void clearCell(int row, int col) {
		if (row < 0 || col < 0 || row >= nRow || col >= nCol)
			return;
		board[row][col].clear();
	}

	public void fillRow(char value, int row) {
		for (int col = 0; col < nCol; col++) {
			fillCell(value, row, col);
		}
	}

	public void moveRowFrom(int row) {
		for (int emptyRow = nRow - 1; emptyRow > row; emptyRow--) {
			if (isRowBlank(emptyRow)) {
				copyRow(row, emptyRow);
				clearRow(row);
			}
		}
	}

	public void copyRow(int rowFrom, int rowTo) {
		for (int col = 0; col < nCol; col++) {
			char val = getCell(rowFrom, col).getValue();
			fillCell(val, rowTo, col);
		}
	}

	public void fillCol(char value, int col) {
		for (int row = 0; row < nRow; row++) {
			fillCell(value, row, col);
		}
	}

	public void fillCell(char value, int row, int col) {
		if (row < 0 || col < 0 || row >= nRow || col >= nCol)
			return;
		board[row][col].setValue(Character.toUpperCase(value));
		boolean fixed = Character.toUpperCase(value) != '*';
		board[row][col].setFixed(fixed);
	}

	public void hardDrop() {
		distance = 0;
		while (true) {
			if (!tetromino.moveDown())
				break;
			distance++;
		}
	}

	public boolean isGameOver() {
		return getNRowOccupied() == nRow && !tetromino.canMoveDown();
	}

	public int calculatePoint() {
		int rowFilled = 0;
		for (int row = nRow - 1; row > -1; row--) {
			if (isRowFilled(row)) {
				rowFilled++;
			}
		}
		if (rowFilled == 0) {
			distance = 0;
			return 0;
		}
		int point;
		switch (rowFilled) {
		case 1:
			point = (40 * level) + (2 * distance);
			break;
		case 2:
			point = (100 * level) + (2 * distance);
			break;
		case 3:
			point = (300 * level) + (2 * distance);
			break;
		case 4:
			point = (1200 * level) + (2 * distance);
			break;
		default:
			point = 0;
			break;
		}
		score += point;
		if (score > highScore)
			highScore = score;
		lineCount += rowFilled;
		if (lineCount > highLineCount)
			highLineCount = lineCount;

		int lines = lineCount >= 100 ? 100 - lineCount : lineCount;
		if (lines >= 90)
			level = 10;
		else if (lines >= 80 && lines < 90)
			level = 9;
		else if (lines >= 70 && lines < 80)
			level = 8;
		else if (lines >= 60 && lines < 70)
			level = 7;
		else if (lines >= 50 && lines < 60)
			level = 6;
		else if (lines >= 40 && lines < 50)
			level = 5;
		else if (lines >= 30 && lines < 40)
			level = 4;
		else if (lines >= 20 && lines < 30)
			level = 3;
		else if (lines >= 10 && lines < 20)
			level = 2;
		else
			level = 1;

		if (level > highLevel)
			highLevel = level;
		return point;
	}

	public void createNewGame() {
		lineCount = 0;
		score = 0;
		level = 1;
		for (int row = 0; row < nRow; row++) {
			clearRow(row);
		}
		tetromino = null;
		nextTetromino = null;
		letters.clear();
		spawn();
	}

	public void read(File file) {
		if (!file.exists())
			return;
		try {
			Document xmldoc;
			DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmldoc = docReader.parse(file);
			Element rootElement = xmldoc.getDocumentElement();
			if (!rootElement.getNodeName().equals("tetris"))
				throw new Exception("Data file is invalid.");
			String version = rootElement.getAttribute("version");
			double versionNumber = Double.parseDouble(version);
			if (versionNumber > 1.0)
				throw new Exception("Data file requires a newer version of tetris.");

			NodeList nodes = rootElement.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i) instanceof Element) {
					Element element = (Element) nodes.item(i);
					if (element.getTagName().equals("level")) {
						level = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("highlevel")) {
						highLevel = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("score")) {
						score = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("highscore")) {
						highScore = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("linecount")) {
						lineCount = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("highlinecount")) {
						highLineCount = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("tetromino")) {
						String str = element.getAttribute("value");
						if (str.length() > 0) {
							char value = str.charAt(0);
							int rotation = Integer.valueOf(element.getAttribute("rotation"));
							int row = Integer.valueOf(element.getAttribute("row"));
							int col = Integer.valueOf(element.getAttribute("col"));
							tetromino = new Tetromino(value, rotation, row, col);
						}
					} else if (element.getTagName().equals("ghostpiecepositions")) {
						Position[] pos = new Position[4];
						int row = Integer.valueOf(element.getAttribute("row0"));
						int col = Integer.valueOf(element.getAttribute("col0"));
						if (row > -1) {
							pos[0] = new Position(row, col);
						}
						row = Integer.valueOf(element.getAttribute("row1"));
						col = Integer.valueOf(element.getAttribute("col1"));
						if (row > -1) {
							pos[1] = new Position(row, col);
						}
						row = Integer.valueOf(element.getAttribute("row2"));
						col = Integer.valueOf(element.getAttribute("col2"));
						if (row > -1) {
							pos[2] = new Position(row, col);
						}
						row = Integer.valueOf(element.getAttribute("row3"));
						col = Integer.valueOf(element.getAttribute("col3"));
						if (row > -1) {
							pos[3] = new Position(row, col);
						}
						tetromino.setGhostPiecePositions(pos);
					} else if (element.getTagName().equals("nexttetromino")) {
						String value = element.getAttribute("value");
						if (value.length() > 0) {
							nextTetromino = new Tetromino(value.charAt(0));
						}
					} else if (element.getTagName().equals("letters")) {
						letters.clear();
						String str = element.getAttribute("value");
						for (char ch : str.toCharArray()) {
							letters.add(ch);
						}
					} else if (element.getTagName().equals("board")) {
						NodeList cellNodeList = element.getChildNodes();
						for (int j = 0; j < cellNodeList.getLength(); j++) {
							if (cellNodeList.item(j) instanceof Element) {
								Element cellElement = (Element) cellNodeList.item(j);
								int row = Integer.valueOf(cellElement.getAttribute("row"));
								int col = Integer.valueOf(cellElement.getAttribute("col"));
								char value = cellElement.getAttribute("value").charAt(0);
								boolean fixed = Boolean.valueOf(cellElement.getAttribute("fixed"));
								board[row][col].setValue(value);
								board[row][col].setFixed(fixed);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save(File file) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(file));
			out.println("<?xml version=\"1.0\"?>");
			out.println("<tetris version=\"1.0\">");
			out.println("    <level value='" + level + "' />");
			out.println("    <highlevel value='" + highLevel + "' />");
			out.println("    <score value='" + score + "' />");
			out.println("    <highscore value='" + highScore + "' />");
			out.println("    <linecount value='" + lineCount + "' />");
			out.println("    <highlinecount value='" + highLineCount + "' />");
			out.println("    <tetromino value='" + tetromino.getValue() + "' rotation='" + tetromino.getRotation()
					+ "' row='" + tetromino.getCenterPosition().getRow() + "' col='"
					+ tetromino.getCenterPosition().getCol() + "' />");
			Position[] gPosition = tetromino.getGhostPiecePositions();
			out.print("    <ghostpiecepositions ");
			for (int index = 0; index < gPosition.length; index++) {
				Position pos = gPosition[index];
				if (pos != null) {
					out.print("row" + index + "='" + pos.getRow() + "' ");
					out.print("col" + index + "='" + pos.getCol() + "' ");
				} else {
					out.print("row" + index + "='" + -1 + "' ");
					out.print("col" + index + "='" + -1 + "' ");
				}

			}
			out.println(" />");
			out.println("    <nexttetromino value='" + nextTetromino.getValue() + "' />");
			StringBuilder sb = new StringBuilder();
			for (char ch : letters) {
				sb.append(ch);
			}
			out.println("    <letters value='" + sb.toString() + "' />");
			out.println("    <board>");
			for (int row = 0; row < nRow; row++) {
				for (int col = 0; col < nCol; col++) {
					out.print("        <cell row='" + row + "' ");
					out.print("col='" + col + "' ");
					char ch = board[row][col].getValue();
					out.print("value='" + ch + "' ");
					out.println("fixed='" + board[row][col].isFixed() + "' />");
				}
			}
			out.println("    </board>");
			out.println("</tetris>");
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < nRow; row++) {
			for (int col = 0; col < nCol; col++) {
				Cell cell = getCell(row, col);
				if (cell.isFixed())
					sb.append('F');
				else
					sb.append(cell.getValue());
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
