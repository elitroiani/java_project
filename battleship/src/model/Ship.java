package model;

import java.util.List;
import java.util.ArrayList;

public class Ship { // IMPLEMENTS SHIPPLACER

	private final ShipConfig shipConfig;
	private final List<Cell> positions = new ArrayList<>();
	private int hits = 0;

	public Ship(ShipConfig shipConfig, List<Cell> positions) {
		this.shipConfig = shipConfig;
		this.positions = positions;
	}

	public ShipConfig getShipConfig() {
		return this.shipConfig;
	}

	public int getHits() {
		return this.hits;
	}

	public void increaseHits() {
		this.hits++;
	}
	
	public List<Cell> getPositions() {
		return this.positions;
	}

	public void hit() {
		// TODO Auto-generated method stub
		
	}

	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setCells(List<Cell> shipCells) {
		// TODO Auto-generated method stub
		
	}

	public boolean isSunk() {
		// TODO Auto-generated method stub
		return false;
	}
}
