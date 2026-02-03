package model;

import java.awt.Point;

public class Cell {
	
	private CellState state = CellState.NOTFIRED;
	private final Point coordinates; //final perché la cella non cambia posizione
	
	public Cell(Point coordinates) {
        this.coordinates = coordinates;
        this.state = CellState.NOTFIRED;
    }

	public CellState getState() {
		return state;
	}

	public void setState(CellState state) {
		this.state = state;
	}

	public Point getCoordinates() {
		return coordinates;
	}
	
    public boolean isFired() {
        return state != CellState.NOTFIRED;
    }
    
    /**
     * Applica un colpo alla cella.
     * Il risultato deve essere coerente (HIT o MISS).
     */
    public void fire(CellState result) {
        if (isFired()) {
            throw new IllegalStateException("Cell already fired at " + coordinates);
        }
        if (result == CellState.NOTFIRED) {
            throw new IllegalArgumentException("Invalid fire result");
        }
        this.state = result;
    }

    public void reset() {
        this.state = CellState.NOTFIRED;
    }

    @Override
    public String toString() {
        return "Cell(" + coordinates.x + "," + coordinates.y + ") - " + state;
    }
    
    
}
