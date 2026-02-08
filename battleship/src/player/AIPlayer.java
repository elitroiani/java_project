package player;

import java.awt.Point;

import ai.Reasoner;
import model.GameState;
import model.Grid;

public class AIPlayer extends AbstractPlayer {

	private final Reasoner reasoner;
	
	public AIPlayer(String name, Grid grid, Reasoner reasoner) {
		super(name, grid);
		this.reasoner = reasoner;
	}


	@Override
	public Point chooseMove(GameState state) {
		return this.reasoner.chooseMove(state); 
	}

}
