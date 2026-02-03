package model;

import java.awt.Point;
import java.util.ArrayList;
//import java.util.HashSet;
import java.util.List;
//import java.util.Set;

public class Grid {
	
	public static final int SIZE = 10;
	
	private Cell[][] cells;
	private List<Ship> ships = new ArrayList<>();
	// private List<Ship> sunkenShips = new ArrayList<>();
	
	public Grid() {
        cells = new Cell[SIZE][SIZE];
        ships = new ArrayList<>();
        initCells();
    }

	private void initCells() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                cells[x][y] = new Cell(new Point(x, y));
            }
        }
    }
	
	public boolean isInside(int x, int y) {
		return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
	}

	public boolean isCellUntouched(int x, int y) {
		return isInside(x, y) && cells[x][y].getState() == CellState.NOTFIRED;
	}

	public boolean isCellHit(int x, int y) {
		return isInside(x, y) && cells[x][y].getState() == CellState.HIT;
	}

	public List<Ship> getShips() {
		return this.ships;
	}

	/*public Cell getCell(int row, int column) {
		return this.cells[row][column];
	}*/

	public Cell getCell(int x, int y) {
        if (!isInside(x, y)) {
            throw new IndexOutOfBoundsException("Cell outside grid");
        }
        return cells[x][y];
    }
	
	public void addShip(Ship ship) {
        ships.add(ship);
    }
	
	/**
     * Applica un colpo alla griglia.
     */
    public MoveResult fireAt(int x, int y) {
        if (!isInside(x, y)) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        
        Cell cell = cells[x][y];
        if (cell.isFired()) {
            return MoveResult.ALREADY_FIRED;
        }

        for (Ship ship : ships) {
            if (ship.occupies(x, y)) {
                ship.hit();
                cell.fire(CellState.HIT);
                return ship.isSunk() ? MoveResult.SUNK : MoveResult.HIT;
            }
        }

        cell.fire(CellState.MISS);
        return MoveResult.MISS;
    }
        
    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }    
        
        
        
        
        
        
        
}
