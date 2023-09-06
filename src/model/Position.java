package model;

import java.util.Objects;

public class Position {
	public static enum Neighbour {
		TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, LEFT, TOP_LEFT,
	};

	private int row;
	private int col;

	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public Position() {
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public Position copy() {
		return new Position(row, col);
	}

	public Position getNeighbourPosition(Neighbour neighbour) {
		switch (neighbour) {
		case TOP:
			return new Position(row - 1, col);
		case TOP_RIGHT:
			return new Position(row - 1, col + 1);
		case RIGHT:
			return new Position(row, col + 1);
		case BOTTOM_RIGHT:
			return new Position(row + 1, col + 1);
		case BOTTOM:
			return new Position(row + 1, col);
		case BOTTOM_LEFT:
			return new Position(row + 1, col - 1);
		case LEFT:
			return new Position(row, col - 1);
		case TOP_LEFT:
			return new Position(row - 1, col - 1);
		default:
			return null;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(col, row);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Position)) {
			return false;
		}
		Position other = (Position) obj;
		return col == other.col && row == other.row;
	}

	@Override
	public String toString() {
		return "Row: " + row + " Col: " + col;
	}
}
