package model;

import java.awt.Point;

public class Cell {
	
	private CellState state = CellState.NOTFIRED;
	private Point coordinates;

	public CellState getState() {
		return state;
	}

	public void setState(CellState state) {
		this.state = state;
	}
	
	
	
}
