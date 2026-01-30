package model;

public class Cell {
	
	private CellState state = CellState.EMPTY;

	public CellState getState() {
		return state;
	}

	public void setState(CellState state) {
		this.state = state;
	}
	
}
