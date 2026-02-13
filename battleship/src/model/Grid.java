package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid {
    
    private final int width;
    private final int height;
    private final Cell[][] cells;
    private final List<Ship> ships = new ArrayList<>();
    
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];

        // Inizializza tutte le celle
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(new Point(x, y));
            }
        }
    }
    
    // --- GETTER ---
    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height; // CORRETTO: prima restituiva width
    }

    public List<Ship> getShips() {
        return List.copyOf(this.ships);
    }
    
    public Cell getCell(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            throw new IndexOutOfBoundsException("Invalid coordinates: " + x + "," + y);
        }
        return cells[x][y];
    }
    
    // --- CHECK ---
    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    // --- POSIZIONAMENTO NAVI ---

    /**
     * Tenta di posizionare una nave nella griglia rispettando i bordi e la distanza
     */
    public boolean placeShip(Ship ship, int startX, int startY, boolean horizontal) {
        // Usiamo il controllo di fattibilità prima di procedere
        if (!canPlaceShipAt(ship, startX, startY, horizontal)) {
            return false;
        }

        List<Cell> shipCells = new ArrayList<>();
        for (int i = 0; i < ship.getSize(); i++) {
            int x = horizontal ? startX + i : startX;
            int y = horizontal ? startY : startY + i;

            Cell cell = getCell(x, y);
            cell.placeShip(ship);
            shipCells.add(cell);
        }

        ship.setCells(shipCells);
        ships.add(ship);
        return true;
    }

    /**
     * Versione interna di canPlaceShip per supportare startX/startY/horizontal
     */
    private boolean canPlaceShipAt(Ship ship, int startX, int startY, boolean horizontal) {
        for (int i = 0; i < ship.getSize(); i++) {
            int x = horizontal ? startX + i : startX;
            int y = horizontal ? startY : startY + i;

            if (!isValidCoordinate(x, y)) {
            	return false;
            }
            
            // Controllo 8 celle circostanti + cella stessa
            if (!isAreaClear(x, y)) {
            	return false;
            }
        }
        return true;
    }

    /**
     * Helper per verificare se una cella e i suoi vicini sono privi di navi
     */
    private boolean isAreaClear(int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (isValidCoordinate(nx, ny) && getCell(nx, ny).hasShip()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // --- COLPI ---
    public MoveResult fireAt(int x, int y) {
        Cell cell = getCell(x, y);
        return cell.fire();
    }

    public boolean allShipsSunk() {
        if (ships.isEmpty()) return false;
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

    // --- METODI AGGIUNTI DA EDDY (Revisionati) ---
    
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
    
    /**
     * Controlla se una nave (già configurata con posizioni) può stare in griglia.
     * Utile se la nave ha già calcolato i suoi Point internamente.
     */
    public boolean canPlaceShip(Ship ship) {
        for (Point p : ship.getPositions()) {
            if (!isValidCoordinate(p.x, p.y)) return false;

            // Il controllo deve essere fatto su .hasShip(), non sullo stato del colpo!
            // Perché in fase di piazzamento sono tutte NOTFIRED.
            if (!isAreaClear(p.x, p.y)) {
                return false;
            }
        }
        return true;
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
