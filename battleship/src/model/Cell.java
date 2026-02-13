package model;

import java.awt.Point;

/**
 * Represents a single square (cell) on the battlefield.
 * It manages its own state and acts as a link to a Ship object if one is present.
 */
public class Cell {
	
    private CellState state = CellState.NOTFIRED;
    private final Point coordinates; 				// Final: a cell's position is immutable
    private Ship ship; 								// Reference to the ship occupying this cell (null if empty)
	
    /**
     * Initializes a cell at the specified coordinates.
     * @param coordinates The (x, y) position on the grid.
     */
    public Cell(Point coordinates) {
        this.coordinates = coordinates;
    }

    // --- GETTERS ---

    public CellState getState() {
        return this.state;
    }
	
    public Point getCoordinates() {
        return this.coordinates;
    }
	   
    public Ship getShip() {
        return this.ship;
    }
	
    /**
     * @return true if there is a ship assigned to this cell.
     */
    public boolean hasShip() {
        return this.ship != null;
    }
    
    // --- SHIP PLACEMENT ---

    /**
     * Associates a ship with this cell.
     * @param ship The ship to be placed.
     * @throws IllegalStateException if the cell is already occupied.
     */
    public void placeShip(Ship ship) {
        if (this.hasShip()) {
            throw new IllegalStateException("Cell already has a ship at " + coordinates);
        }
        this.ship = ship;
    }
    
    // --- COMBAT LOGIC ---

    /**
     * Processes a shot fired at this cell.
     * Transitions the state to HIT if a ship is present, or MISS otherwise.
     * @return A MoveResult indicating the outcome (MISS, HIT, SUNK, or ALREADY_FIRED).
     */
    public MoveResult fire() {

        // 1. Invalid move: Cell was already targeted
        if (this.state != CellState.NOTFIRED) {
            return MoveResult.ALREADY_FIRED;
        }

        // 2. Shot missed: No ship at these coordinates
        if (ship == null) {
            state = CellState.MISS;
            return MoveResult.MISS;
        }

        // 3. Shot hit: Update cell state and notify the ship
        state = CellState.HIT;
        ship.hit();

        // Check if this hit was the one that destroyed the ship
        if (ship.isSunk()) {
            return MoveResult.SUNK;
        }

        return MoveResult.HIT;
    }
    
    /**
     * @return true if the cell has been shot at (HIT or MISS).
     */
    public boolean isFired() {
        return state != CellState.NOTFIRED;
    }
    
    /**
     * @return true if the cell has not been targeted yet.
     */
    public boolean isNotFired() {
    	return this.state == CellState.NOTFIRED;
    }

    /**
     * Resets the cell to its initial state (empty and not fired).
     */
    public void reset() {
        this.state = CellState.NOTFIRED;
        this.ship = null;
    }
    
    @Override
    public String toString() {
        return "Cell(" + coordinates.x + "," + coordinates.y + ") - " + state;
    }
    
    /**
     * Provides a visual symbol for console debugging:
     * "." = Not fired
     * "o" = Miss
     * "X" = Hit
     */
    public String toSymbol() {
        switch (state) {
            case NOTFIRED: return ".";
            case MISS: return "o";
            case HIT: return "X";
            default: return "?";
        }
    }

    // --- UTILITY METHODS ---

    /**
     * Cells are considered equal if they share the same coordinates.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cell)) return false;
        Cell other = (Cell) obj;
        return coordinates.equals(other.coordinates);
    }

    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }
}
