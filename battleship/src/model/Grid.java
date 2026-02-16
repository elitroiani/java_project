package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Represents the game board.
 * Responsible for managing the cell matrix, validating ship placement rules 
 * (including the 3x3 proximity rule), and processing shots.
 */
public class Grid {
    
    private final int width;
    private final int height;
    private final Cell[][] cells;
    private final List<Ship> ships = new ArrayList<>();
    
    /**
     * Initializes the grid with the specified dimensions and populates it with Cell objects.
     */
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(new Point(x, y));
            }
        }
    }
    
    // --- GETTERS ---

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public List<Ship> getShips() {
        return List.copyOf(this.ships);
    }
    
    public Cell getCell(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            throw new IndexOutOfBoundsException("Invalid coordinates: " + x + "," + y);
        }
        return cells[x][y];
    }
    
    // --- VALIDATION & PROXIMITY LOGIC ---

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Verifies if a cell and its immediate neighbors are free of ships.
     * Used primarily during the placement phase.
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

    /**
     * NEW: Checks if the 3x3 area around a coordinate contains a sunken ship.
     * If a sunken ship is found, the current cell cannot contain another ship 
     * according to the proximity rules.
     * * @return true if NO sunken ships are in the immediate vicinity.
     */
    public boolean isAreaClearOfSunkenShips(int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (isValidCoordinate(nx, ny)) {
                    Cell cell = getCell(nx, ny);
                    // If a neighbor is a HIT and the ship it belongs to is SUNK
                    if (cell.getState() == CellState.HIT && cell.hasShip() && cell.getShip().get().isSunk()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * NEW: Determines if a cell is a viable target for an AI.
     * A cell is a potential target only if it hasn't been fired at AND
     * it is not adjacent to a ship that has already been sunk.
     */
    public boolean isPotentialTarget(int x, int y) {
        return getCellState(x, y) == CellState.NOTFIRED && isAreaClearOfSunkenShips(x, y);
    }
    
    // --- SHIP PLACEMENT LOGIC ---

    public boolean placeShip(Ship ship, int startX, int startY, boolean horizontal) {
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

    private boolean canPlaceShipAt(Ship ship, int startX, int startY, boolean horizontal) {
        for (int i = 0; i < ship.getSize(); i++) {
            int x = horizontal ? startX + i : startX;
            int y = horizontal ? startY : startY + i;

            if (!isValidCoordinate(x, y) || !isAreaClear(x, y)) {
                return false;
            }
        }
        return true;
    }
    
    // --- GAMEPLAY ACTIONS ---

    public MoveResult fireAt(int x, int y) {
        return getCell(x, y).fire();
    }

    public boolean allShipsSunk() {
        if (ships.isEmpty()) {
        	return false;
        }
        return ships.stream().allMatch(Ship::isSunk);
    }

    public void reset() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].reset();
            }
        }
        ships.clear();
    }

    // --- UTILITY METHODS ---
    
    public CellState getCellState(int x, int y) {
        return this.getCell(x, y).getState();
    }
    
    /**
     * Returns all cells that are NOTFIRED.
     */
    public List<Cell> getUntouchedCells(){
        return Arrays.stream(cells)
                     .flatMap(Arrays::stream)
                     .filter(s -> s.getState() == CellState.NOTFIRED)
                     .toList();
    }

    /**
     * NEW: Returns only the cells that are actually worth firing at 
     * (ignoring zones around sunken ships).
     */
    public List<Cell> getSmartUntouchedCells() {
        return Arrays.stream(cells)
                     .flatMap(Arrays::stream)
                     .filter(s -> isPotentialTarget(s.getCoordinates().x, s.getCoordinates().y))
                     .toList();
    }
    
    public List<Ship> shipsRemaining(){
        return this.ships.stream().filter(s -> !s.isSunk()).toList();
    }
    
    public Optional<Ship> getShipAt(int x, int y) {
        // Supponendo che le celle abbiano un riferimento alla nave
        Optional<Ship> ship = cells[x][y].getShip(); 
        return ship;
    }
}