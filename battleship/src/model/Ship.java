package model;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Represents an individual ship on the battlefield.
 * Tracks the ship's integrity (hits), the cells it occupies, 
 * and its status (alive or sunk).
 */
public class Ship { 

    private final ShipConfig config;
    private final List<Cell> positions = new ArrayList<>();
    private int hits = 0;
    
    /**
     * Creates a ship based on a specific configuration.
     * @param config The template containing name and size.
     * @throws IllegalArgumentException if the configuration is null.
     */
    public Ship(ShipConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("ShipConfig cannot be null");
        }
        this.config = config;
    }
    
    // --- GETTERS ---

    public ShipConfig getConfig() {
        return this.config;
    }
    
    public int getSize() {
        return this.config.getSize();
    }
    
    public int getHits() {
        return this.hits;
    }
    
    /**
     * Returns an immutable copy of the cells occupied by this ship.
     * This ensures the internal list cannot be modified from outside.
     */
    public List<Cell> getCells() {
        return List.copyOf(this.positions);
    }
    
    /**
     * Extracts the coordinates (Points) from the occupied cells.
     * @return A list of Point objects representing the ship's location.
     */
    public List<Point> getPositions(){
        return this.positions.stream()
                             .map(Cell::getCoordinates)
                             .toList();
    }
    
    // --- PLACEMENT ---

    /**
     * Assigns the specific cells that this ship occupies on the grid.
     * This method must be called exactly once during ship placement.
     * @param shipCells The list of Cells provided by the Grid.
     * @throws IllegalStateException if the ship has already been placed.
     * @throws IllegalArgumentException if the number of cells doesn't match the ship's size.
     */
    public void setCells(List<Cell> shipCells) {
        if (!this.positions.isEmpty()) {
            throw new IllegalStateException("Ship already placed");
        }
        if (shipCells.size() != getSize()) {
            throw new IllegalArgumentException("Invalid number of cells for ship");
        }
        this.positions.addAll(shipCells);
    }
    
    // --- COMBAT LOGIC ---

    /**
     * Registers a hit on the ship.
     * If the ship is already sunk, the call is ignored to prevent inconsistent states.
     */
    public void hit() {
        if (isSunk()) {
            return;
        }
        hits++;
    }

    /**
     * Determines if the ship has been destroyed.
     * @return true if the number of hits equals the ship's size.
     */
    public boolean isSunk() {
        return this.hits == getSize();
    }
    
    @Override
    public String toString() {
        return config.getName() + " (" + hits + "/" + getSize() + ")";
    }
}
