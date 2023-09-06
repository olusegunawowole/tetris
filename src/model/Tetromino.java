package model;

import java.util.ArrayList;

import model.Position.Neighbour;

public class Tetromino {
	private char value;
	private Position centerPosition;
	private int rotation;
	private Position[] positions, ghostPiecePositions;
	private static Cell[][] boardState;
	private int nRow;
	private int nCol;
	
	public Tetromino(char value) {
		this(value, 0, -1, 4);
	}

	public Tetromino(char value, int rotation, int centerRow, int centerCol) {
		if (Character.toUpperCase(value) == 'I' || Character.toUpperCase(value) == 'J'
				|| Character.toUpperCase(value) == 'L' || Character.toUpperCase(value) == 'O'
				|| Character.toUpperCase(value) == 'S' || Character.toUpperCase(value) == 'T'
				|| Character.toUpperCase(value) == 'Z') {
			this.value = Character.toUpperCase(value);
		} else {
			throw new IllegalArgumentException(value + " is an invalid tetromino letter");
		}
		centerPosition = new Position(centerRow, centerCol);
		this.rotation =rotation;
		nRow = boardState.length;
		nCol = boardState[0].length;
		positions = new Position[4];
		ghostPiecePositions = new Position[4];
		switch (this.value) {
		case 'I':
			positions = setI(rotation);
			break;
		case 'J':
			positions = setJ(rotation);
			break;
		case 'L':
			positions = setL(rotation);
			break;
		case 'S':
			positions = setS(rotation);
			break;
		case 'T':
			positions = setT(rotation);
			break;
		case 'Z':
			positions = setZ(rotation);
			break;
		default:
			positions = setO();
			break;
		}
	}

	private Position[] setI(int rotation) {
		Position[] positions = new Position[4];
		switch (rotation) {
		case 1:
			positions[0] = centerPosition.getNeighbourPosition(Neighbour.TOP_RIGHT);
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_RIGHT)
					.getNeighbourPosition(Neighbour.BOTTOM);
			break;
		case 2:
			positions[0] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_RIGHT)
					.getNeighbourPosition(Neighbour.RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_LEFT);
			break;
		case 3:
			positions[0] = centerPosition.getNeighbourPosition(Neighbour.TOP_LEFT);
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_LEFT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_LEFT)
					.getNeighbourPosition(Neighbour.BOTTOM);
			break;
		default:
			positions[0] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP_RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.TOP_RIGHT)
					.getNeighbourPosition(Neighbour.RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.TOP_LEFT);
			break;
		}
		return positions;
	}

	private Position[] setJ(int rotation) {
		Position[] positions = new Position[4];
		switch (rotation) {
		case 1:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.TOP_RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			break;
		case 2:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_RIGHT);
			break;
		case 3:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_LEFT);
			break;
		default:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.TOP_LEFT);
			break;
		}
		return positions;
	}

	private Position[] setL(int rotation) {
		Position[] positions = new Position[4];
		switch (rotation) {
		case 1:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			break;
		case 2:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_LEFT);
			break;
		case 3:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.TOP_LEFT);
			break;
		default:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.TOP_RIGHT);
			break;
		}
		return positions;
	}

	private Position[] setO() {
		Position[] positions = new Position[4];
		positions[0] = centerPosition.copy();
		positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
		positions[2] = centerPosition.getNeighbourPosition(Neighbour.TOP_RIGHT);
		positions[3] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
		return positions;
	}

	public void set() {
		for (Position p : positions) {
			int row = p.getRow();
			int col = p.getCol();
			if (row > -1 && col > -1 && row < nRow && col < nCol) {
				boardState[row][col].setValue(value);
			}
		}
	}

	private Position[] setS(int rotation) {
		Position[] positions = new Position[4];
		switch (rotation) {
		case 1:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_RIGHT);
			break;
		case 2:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_LEFT);
			break;
		case 3:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.TOP_LEFT);
			break;
		default:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.TOP_RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			break;
		}
		return positions;
	}

	private Position[] setT(int rotation) {
		Position[] positions = new Position[4];
		switch (rotation) {
		case 1:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			break;
		case 2:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			break;
		case 3:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			break;
		default:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			break;
		}
		return positions;
	}

	private Position[] setZ(int rotation) {
		Position[] positions = new Position[4];
		switch (rotation) {
		case 1:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP_RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			break;
		case 2:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_RIGHT);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			break;
		case 3:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.BOTTOM_LEFT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.LEFT);
			break;
		default:
			positions[0] = centerPosition.copy();
			positions[1] = centerPosition.getNeighbourPosition(Neighbour.TOP);
			positions[2] = centerPosition.getNeighbourPosition(Neighbour.RIGHT);
			positions[3] = centerPosition.getNeighbourPosition(Neighbour.TOP_LEFT);
			break;
		}
		return positions;
	}

	private boolean canRotate(boolean clockwise) {
		int newRotation = setAndGetRotation(clockwise);
		if (value == 'I') {
			Position[] newPositions = setI(newRotation);
			return inbounds(newPositions);
		} else if (value == 'J') {
			Position[] newPositions = setJ(newRotation);
			return inbounds(newPositions);
		} else if (value == 'L') {
			Position[] newPositions = setL(newRotation);
			return inbounds(newPositions);
		} else if (value == 'S') {
			Position[] newPositions = setS(newRotation);
			return inbounds(newPositions);
		} else if (value == 'T') {
			Position[] newPositions = setT(newRotation);
			return inbounds(newPositions);
		} else if (value == 'Z') {
			Position[] newPositions = setZ(newRotation);
			return inbounds(newPositions);
		} else {
			return true;
		}
	}

	public char getValue() {
		return value;
	}

	public int getRotation() {
		return rotation;
	}
	
	public Position getCenterPosition() {
		return centerPosition;
	}

	public void setCenterPosition(Position centerPosition) {
		this.centerPosition = centerPosition;
	}

	public boolean canRotateClockwise() {
		return canRotate(true);

	}

	public boolean canRotateAntiClockwise() {
		return canRotate(false);
	}

	private boolean rotate(boolean clockwise) {
		int newRotation = setAndGetRotation(clockwise);
		Position[] newPositions;
		if (value == 'I') {
			newPositions = setI(newRotation);
		} else if (value == 'J') {
			newPositions = setJ(newRotation);
		} else if (value == 'L') {
			newPositions = setL(newRotation);
		} else if (value == 'S') {
			newPositions = setS(newRotation);
		} else if (value == 'T') {
			newPositions = setT(newRotation);
		} else if (value == 'Z') {
			newPositions = setZ(newRotation);
		} else {
			rotation = newRotation;
			setGhostPiecePositions();
			return true;
		}
		boolean inbounds = inbounds(newPositions);
		if (inbounds) {
			// Clear cells currently occupied by this object
			for (Position p : positions) {
				int row = p.getRow();
				int col = p.getCol();
				if (row > -1) {
					boardState[row][col].resetValue();
				}
			}
			// Set value of new cells
			for (Position p : newPositions) {
				int row = p.getRow();
				int col = p.getCol();
				if (row > -1) {
					boardState[row][col].setValue(value);
				}
			}
			rotation = newRotation;
			positions = newPositions;
			setGhostPiecePositions();
		}
		return inbounds;
	}

	public boolean rotateClockwise() {
		return rotate(true);
	}

	public boolean rotateAntiClockwise() {
		return rotate(false);
	}

	private int setAndGetRotation(boolean clockwise) {
		if (clockwise) {
			return (rotation + 1 > 3) ? 0 : rotation + 1;
		} else {
			return (rotation - 1 < 0) ? 3 : rotation - 1;
		}
	}

	public static void test() {
		char[] arr = { 'I', 'J', 'L', 'O', 'S', 'T', 'Z' };
		for (char c : arr) {
			for (int index = 0; index < 4; index++) {
				Tetromino t = new Tetromino(c);
				System.out.println(t.toString());
				System.out.println("======================================================");
			}

		}
	}

	/**
	 * Moves tetromino down by one step (row) at time. It performs a check to see if
	 * tetromino can be moved down without kicking a wall.
	 * 
	 * @return true if tretromino moves down or false if otherwise.
	 */
	public boolean moveDown() {
		for (Position p : positions) {
			int nextRow = p.getRow() + 1;
			int col = p.getCol();
			boolean inbound = nextRow < nRow; // remain inside board
			if (!inbound) {
				return false;
			}
			if (nextRow > -1 && boardState[nextRow][col].isFixed()) {
				return false;
			}
		}
		ArrayList<Position> visitedPositions = new ArrayList<>();
		for (Position p : positions) {
			int row = p.getRow();
			int col = p.getCol();
			int nextRow = p.getRow() + 1;
			if (nextRow > -1) {
				boardState[nextRow][col].setValue(value);
			}
			visitedPositions.add(new Position(nextRow, col));
			if (!visitedPositions.contains(p) && row > -1)
				boardState[row][col].resetValue();
			p.setRow(nextRow);
		}
		centerPosition.setRow(centerPosition.getRow() + 1);
		setGhostPiecePositions();
		return true;
	}

	/**
	 * Moves tetromino right by one step (column) at time. It performs a check to
	 * see if tetromino can be moved right without kicking a wall.
	 * 
	 * @return true if tretromino moves right or false if otherwise.
	 */
	public boolean moveRight() {
		for (Position p : positions) {
			int row = p.getRow();
			int nextCol = p.getCol() + 1;
			boolean inbound = row < nRow && nextCol >= 0 && nextCol < nCol;
			if (!inbound) {
				return false;
			}
			if (row > -1 && boardState[row][nextCol].isFixed()) {
				return false;
			}
		}
		ArrayList<Position> visitedPositions = new ArrayList<>();
		for (Position p : positions) {
			int row = p.getRow();
			int col = p.getCol();
			int nextCol = p.getCol() + 1;
			if (row > -1)
				boardState[row][nextCol].setValue(value);
			visitedPositions.add(new Position(row, nextCol));
			if (row > -1 && !visitedPositions.contains(p))
				boardState[row][col].resetValue();
			p.setCol(nextCol);
		}
		centerPosition.setCol(centerPosition.getCol() + 1);
		setGhostPiecePositions();
		return true;
	}

	/**
	 * Moves tetromino left by one step (column) at time. It perform a check to see
	 * if tetromino can be moved left without kicking a wall.
	 * 
	 * @return true if tretromino moves left or false if otherwise.
	 */
	public boolean moveLeft() {
		for (Position p : positions) {
			int row = p.getRow();
			int prevCol = p.getCol() - 1;

			boolean inbound = row < nRow && prevCol >= 0 && prevCol < nCol;
			if (!inbound) {
				return false;
			}
			if (row > -1 && boardState[row][prevCol].isFixed()) {
				return false;
			}
		}
		ArrayList<Position> visitedPositions = new ArrayList<>();
		for (Position p : positions) {
			int row = p.getRow();
			int col = p.getCol();
			int prevCol = p.getCol() - 1;
			if (row > -1)
				boardState[row][prevCol].setValue(value);
			visitedPositions.add(new Position(row, prevCol));
			if (row > -1 && !visitedPositions.contains(p))
				boardState[row][col].resetValue();
			p.setCol(prevCol);
		}
		centerPosition.setCol(centerPosition.getCol() - 1);
		setGhostPiecePositions();
		return true;
	}
	
	private boolean inbounds(Position[] positions) {
		for (Position p : positions) {
			int row = p.getRow();
			int col = p.getCol();
			boolean inbound = row < nRow && col >= 0 && col < nCol;
			boolean fixed = inbound && row > -1 && boardState[row][col].isFixed();
			if (!inbound || fixed)
				return false;
		}
		return true;
	}

	public static Cell[][] getBoardState() {
		return boardState;
	}

	public static void setBoardState(Cell[][] boardState) {
		Tetromino.boardState = boardState;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Value: " + value + "\n");
		sb.append("Rotation: " + rotation + "\n");
		sb.append("Coordinates (row,col): ");
		int[][] board = new int[nRow][nCol];
		for (Position p : positions) {
			int row = p.getRow();
			int col = p.getCol();
			if (row > -1 && col > -1) {
				board[row][col] = 1;
			}
			String str = "(" + row + "," + col + ") ";
			sb.append(str);
		}
		sb.append("\n");
		for (int row = 0; row < nRow; row++) {
			for (int col = 0; col < nCol; col++) {
				if (board[row][col] > 0) {
					sb.append('@');
				} else {
					sb.append('.');
				}
			}
			sb.append('\n');
		}
		return sb.toString().trim();
	}

	public boolean canMoveDown() {
		for (Position p : positions) {
			int nextRow = p.getRow() + 1;
			if (nextRow >= nRow)
				return false;
			if (nextRow < 0 && boardState[0][p.getCol()].isFixed()) {
				return false;
			}
			if (nextRow > -1 && boardState[nextRow][p.getCol()].isFixed()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean canMoveDown(Position[] pos) {
		for (Position p : pos) {
			int nextRow = p.getRow() + 1;
			if (nextRow >= nRow)
				return false;
			if (nextRow > -1 && boardState[nextRow][p.getCol()].isFixed()) {
				return false;
			}
		}
		return true;
	}

	public void setGhostPiecePositions() {
		Position[] tempPositions = new Position[positions.length]; // to store positions
		//Copy positions
		for (int index = 0; index < positions.length; index++) {
			tempPositions[index] = positions[index].copy();
		}
		
		while (canMoveDown(tempPositions)) {
			for (Position p : tempPositions) {
				p.setRow(p.getRow() + 1);
			}
		}
				ghostPiecePositions = tempPositions;
	}
	
	public void setGhostPiecePositions(Position[] pos) {
		ghostPiecePositions = pos;
	}
	
	public Position[] getGhostPiecePositions() {
		return ghostPiecePositions;
	}

	public static Position[] setAndGetPositions(Tetromino t, int startRow, int startCol) {
		Tetromino temp = new Tetromino(t.value);
		temp.centerPosition.setRow(startRow);
		temp.centerPosition.setCol(startCol);
		switch (temp.value) {
		case 'I':
			return temp.setI(temp.rotation);
		case 'J':
			return temp.setJ(temp.rotation);
		case 'L':
			return temp.setL(temp.rotation);
		case 'S':
			return temp.setS(temp.rotation);
		case 'T':
			return temp.setT(temp.rotation);
		case 'Z':
			return temp.setZ(temp.rotation);
		default:
			return temp.setO();
		}
	}
}