package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.HashSet;
import java.util.List;
//import java.util.Set;

public class Grid {
	
	//public static final int SIZE = 10;
	private final int width;
    private final int height;
	private final Cell[][] cells;
	private final List<Ship> ships = new ArrayList<>();
	// private List<Ship> sunkenShips = new ArrayList<>();
	
	/*public Grid() {
        cells = new Cell[SIZE][SIZE];
        ships = new ArrayList<>();
        initCells();
    }*/
	
	public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];

        // inizializza tutte le celle
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(new Point(x, y));
            }
        }
    }
	
	public Cell getCell(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            throw new IndexOutOfBoundsException("Invalid coordinates: " + x + "," + y);
        }
        return cells[x][y];
    }
	
	
	public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
	
	
	// --- POSIZIONAMENTO NAVI ---
    /**
     * Tenta di posizionare una nave nella griglia
     * @param ship nave da posizionare
     * @param startX coordinata iniziale X
     * @param startY coordinata iniziale Y
     * @param horizontal orientamento
     * @return true se posizionamento riuscito
     */
    public boolean placeShip(Ship ship, int startX, int startY, boolean horizontal) {
        List<Cell> shipCells = new ArrayList<>();

        for (int i = 0; i < ship.getSize(); i++) {
            int x = horizontal ? startX + i : startX;
            int y = horizontal ? startY : startY + i;

            if (!isValidCoordinate(x, y)) return false; // fuori griglia
            Cell cell = getCell(x, y);
            if (cell.hasShip()) return false; 			// cella occupata
            shipCells.add(cell);
        }

        // assegna la nave alle celle
        for (Cell cell : shipCells) {
            cell.placeShip(ship);
        }
        ship.setCells(shipCells);
        ships.add(ship);
        return true;
    }
	
	
 // --- COLPI ---
    /**
     * Applica un colpo e ritorna il risultato
     */
    public CellState fireAt(int x, int y) {
        Cell cell = getCell(x, y);
        return cell.fire();
    }

    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) return false;
        }
        return true;
    }

    // --- RESET ---
    public void reset() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].reset();
            }
        }
        ships.clear();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Ship> getShips() {
        return ships;
    }
    
    
    // METODI AGGIUNTI DA EDDY 
    // Controllali plss
    
    public CellState getCellState(int x, int y) {
    	return this.getCell(x, y).getState();
    }
    
    public List<Cell> getUntouchedCells(){
    	return Arrays.stream(cells)
    				 .flatMap(Arrays::stream)
    				 .filter(s -> s.getState() == CellState.NOTFIRED)
    				 .toList();
    }
    
    public List<Ship> shipsRemaining(){
		return this.ships.stream()
						 .filter(s -> !s.isSunk())
						 .toList();
    }
 
	
/*
 * 
	public boolean isCellUntouched(int x, int y) {
		return this.isValidCoordinate(x, y) && !this.getCell(x, y).isFired();
	}
    
    public boolean isCellHit(int x, int y) {
		return this.isValidCoordinate(x, y) && this.getCell(x, y).isHit();
	}

	public Cell getCell(int row, int column) {
		return this.cells[row][column];
	}
	
	public void addShip(Ship ship) {
        ships.add(ship);
    }*/
	
	/**
     * Applica un colpo alla griglia.
     */
    /*public MoveResult fireAt(int x, int y) {
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
    }*/
           
        
}
