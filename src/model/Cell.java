package model;

public class Cell {
	private char value;
	private boolean fixed;
	
	public Cell() {
		value = '*';
	}

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public void clear() {
		fixed = false;
		value = '*';
	}

	public void resetValue() {
		value = '*';
	}
	
	public boolean isEmpty() {
		return value == '*';
	}
}
