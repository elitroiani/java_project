package model;

import java.awt.Point;

import ai.Reasoner;

public class AIPlayer extends AbstractPlayer {

	private final Reasoner reasoner;
	
	public AIPlayer(String name, Grid grid, Reasoner reasoner) {
		super(name, grid);
		this.reasoner = reasoner;
	}

//	@Override
//	public Point chooseMove() {
//		return this.reasoner.nextMove(); 
//	}

}
