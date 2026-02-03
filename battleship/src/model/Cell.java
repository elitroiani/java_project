package model;

import java.awt.Point;

public class Cell {
	
	private CellState state = CellState.NOTFIRED;
	private final Point coordinates; //final perché la cella non cambia posizione
	private Ship ship; // riferimento alla nave presente, null se non c'è
	
	public Cell(Point coordinates) {
        this.coordinates = coordinates;
    }

	public CellState getState() {
		return this.state;
	}

	public void setState(CellState state) {
		this.state = state;
	}

    public Ship getShip() {
        return this.ship;
    }
	
    public boolean hasShip() {
        return this.ship != null;
    }
    
    
 // --- POSIZIONAMENTO DELLA NAVE ---
    public void placeShip(Ship ship) {
        if (this.hasShip()) {
            throw new IllegalStateException("Cell already has a ship at " + coordinates);
        }
        this.ship = ship;
    }
    
    
 // --- COLPO ---
    /**
     * Applica un colpo alla cella.
     * Se c'è una nave, diventa HIT; altrimenti MISS.
     * @return lo stato aggiornato della cella
     */
    public CellState fire() {
        if (isFired()) {
            throw new IllegalStateException("Cell already fired at " + coordinates);
        }
        if (hasShip()) {
            state = CellState.HIT;
            ship.hit(); // aggiorna lo stato della nave
        } else {
            state = CellState.MISS;
        }
        return state;
    }
 
    public boolean isFired() {
        return state != CellState.NOTFIRED;
    }

    
    public void reset() {
        this.state = CellState.NOTFIRED;
        this.ship = null;
    }
    
    
    @Override
    public String toString() {
        return "Cell(" + coordinates.x + "," + coordinates.y + ") - " + state;
    }
    
    
    /**
     * Simbolo per la console:
     * "." = non colpita
     * "o" = colpo a vuoto
     * "X" = colpo a segno
     */
    public String toSymbol() {
        switch (state) {
            case NOTFIRED: return ".";
            case MISS: return "o";
            case HIT: return "X";
            default: return "?";
        }
    }
    

    // --- UTILITY ---
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
    
    

	public Point getCoordinates() {
		return coordinates;
	}
    
}
