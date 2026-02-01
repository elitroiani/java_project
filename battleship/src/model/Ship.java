package model;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;


public class Ship { //IMPLEMENTS SHIPPLACER
	
	private final ShipConfig shipConfig;
	private List<Cell> positions = new ArrayList<>();
	//private Integer hits;
	
	public Ship(ShipConfig shipConfig, List<Cell> positions) {
		this.shipConfig = shipConfig;
		this.positions = positions;
	}


	public ShipConfig getShipConfig() {
		return this.shipConfig;
	}

	/*
	 * public int getHits() { return this.hits; }
	 */

	public List<Cell> getPositions() {
		return this.positions;
	}	 
}
