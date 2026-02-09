package model;

import java.awt.Point;

public class Cell {
	
	private CellState state = CellState.NOTFIRED;
	private final Point coordinates; 				//final perché la cella non cambia posizione
	private Ship ship; 								// riferimento alla nave presente, null se non c'è
	
	public Cell(Point coordinates) {
        this.coordinates = coordinates;
    }

	public CellState getState() {
		return this.state;
	}
	
	public Point getCoordinates() {
		return this.coordinates;
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
    public MoveResult fire() {

        // 1️ mossa non valida → NON cambia stato
        if (this.state != CellState.NOTFIRED) {
            return MoveResult.ALREADY_FIRED;
        }

        // 2️ colpo valido
        if (ship == null) {
            state = CellState.MISS;
            return MoveResult.MISS;
        }

        // 3️ colpita una nave
        state = CellState.HIT;
        ship.hit();

        if (ship.isSunk()) {
            return MoveResult.SUNK;
        }

        return MoveResult.HIT;
    }
 
    public boolean isFired() {
        return state != CellState.NOTFIRED;
    }
    
    public boolean isNotFired() {
    	return this.state == CellState.NOTFIRED;
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
    
}
