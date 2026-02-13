package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * @param width Grid width (columns).
     * @param height Grid height (rows).
     */
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];

        // Initialize all cells within the matrix
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(new Point(x, y));
            }
        }
    }
    
    // --- GETTERS ---

    public int getWidth() {
        return this.width;
    }

    /**
     * @return The height of the grid. 
     * Note: Previously corrected a bug where it returned width.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @return An immutable copy of the ships currently on the grid.
     */
    public List<Ship> getShips() {
        return List.copyOf(this.ships);
    }
    
    /**
     * Retrieves a specific cell.
     * @throws IndexOutOfBoundsException if coordinates are outside grid boundaries.
     */
    public Cell getCell(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            throw new IndexOutOfBoundsException("Invalid coordinates: " + x + "," + y);
        }
        return cells[x][y];
    }
    
    // --- VALIDATION ---

    /**
     * Checks if a coordinate is within the grid limits.
     */
    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    // --- SHIP PLACEMENT LOGIC ---

    /**
     * Attempts to place a ship on the grid.
     * This follows a transactional approach: it verifies all constraints (boundaries 
     * and distance from other ships) before modifying any cell data.
     * @return true if placement succeeded, false otherwise.
     */
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

    /**
     * Internal check for ship placement feasibility at a specific location.
     */
    private boolean canPlaceShipAt(Ship ship, int startX, int startY, boolean horizontal) {
        for (int i = 0; i < ship.getSize(); i++) {
            int x = horizontal ? startX + i : startX;
            int y = horizontal ? startY : startY + i;

            if (!isValidCoordinate(x, y)) {
                return false;
            }
            
            // Check the 8 surrounding cells + the cell itself
            if (!isAreaClear(x, y)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifies if a cell and its immediate neighbors are free of ships.
     * This implements the required "buffer zone" between ships.
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
    
    // --- GAMEPLAY ACTIONS ---

    /**
     * Targets a cell and processes the result of the shot.
     * @return The result of the move (HIT, MISS, SUNK, etc.)
     */
    public MoveResult fireAt(int x, int y) {
        Cell cell = getCell(x, y);
        return cell.fire();
    }

    /**
     * @return true if all ships on this grid have been sunk.
     */
    public boolean allShipsSunk() {
        if (ships.isEmpty()) return false;
        for (Ship ship : ships) {
            if (!ship.isSunk()) return false;
        }
        return true;
    }

    /**
     * Resets the grid to its initial state, clearing all cells and removing ships.
     */
    public void reset() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].reset();
            }
        }
        ships.clear();
    }

    // --- UTILITY METHODS (Revised) ---
    
    public CellState getCellState(int x, int y) {
        return this.getCell(x, y).getState();
    }
    
    /**
     * Uses Java Streams to find all cells that haven't been targeted yet.
     */
    public List<Cell> getUntouchedCells(){
        return Arrays.stream(cells)
                     .flatMap(Arrays::stream)
                     .filter(s -> s.getState() == CellState.NOTFIRED)
                     .toList();
    }
    
    /**
     * @return A list of ships that are currently still afloat.
     */
    public List<Ship> shipsRemaining(){
        return this.ships.stream()
                         .filter(s -> !s.isSunk())
                         .toList();
    }
    
    /**
     * Validates if a ship can be placed based on its pre-calculated coordinate points.
     */
    public boolean canPlaceShip(Ship ship) {
        for (Point p : ship.getPositions()) {
            if (!isValidCoordinate(p.x, p.y)) return false;

            // Proximity check ensures ships don't touch even during initial placement
            if (!isAreaClear(p.x, p.y)) {
                return false;
            }
        }
        return true;
    }
}
