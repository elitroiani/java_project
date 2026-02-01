package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grid {
	
	private Cell[][] cells;
	private List<Ship> ships = new ArrayList<>();

	public boolean isInside(int nx, int ny) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCellUntouched(int nx, int ny) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCellHit(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Ship> getShips() {
		return ships;
	}

	public Cell getCell(int row, int column) {
		return this.cells[row][column];
	}

}
