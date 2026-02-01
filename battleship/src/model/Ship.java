package model;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;


public class Ship { //IMPLEMENTS SHIPPLACER
	
	private final ShipConfig shipConfig;
	private List<Point> positions = new ArrayList<>();
	private Integer hits;
	
	public Ship(ShipConfig shipConfig, List<Point> positions) {
		this.shipConfig = shipConfig;
		this.positions = positions;
	}


	public ShipConfig getShipConfig() {
		return this.shipConfig;
	}

	public int getHits() {
		return this.hits;
	}

	public List<Point> getPositions() {
		return this.positions;
	}	 

}
