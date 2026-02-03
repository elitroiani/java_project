package model;

import java.util.List;
import java.util.ArrayList;

public class Ship { // IMPLEMENTS SHIPPLACER

	private final ShipConfig config;
	private final List<Cell> positions = new ArrayList<>();
	private int hits = 0;
	

	public Ship(ShipConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("ShipConfig cannot be null");
        }
        this.config = config;
    }
	
    // --- GETTER ---
    public ShipConfig getConfig() {
        return this.config;
    }
    
    public int getSize() {
        return this.config.getSize();
    }
    
    public int getHits() {
        return this.hits;
    }
    
	public List<Cell> getPositions() {
		return List.copyOf(this.positions);    // protegge la lista
	}
    
    
    
	// --- POSIZIONAMENTO ---
    /**
     * Assegna le celle occupate dalla nave.
     * Deve essere chiamato una sola volta.
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
	
	
    // --- COLPI ---
    public void hit() {
        this.hits++;
    }
	

    public boolean isSunk() {
        return this.hits >= getSize();
    }
	
	
    @Override
    public String toString() {
        return config.getName() + " (" + hits + "/" + getSize() + ")";
    }

}
