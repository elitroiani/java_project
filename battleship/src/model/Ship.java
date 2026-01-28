package model;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;


public class Ship {
	
	//private String name;
	private int size;
	private int hits;
	private List<Point> positions = new ArrayList<>();
	
	public Ship(int size, List<Point> positions) {
		this.size = size;
		this.positions = positions;
	}
	
	
	

}
