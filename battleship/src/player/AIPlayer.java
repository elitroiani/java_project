package player;

import java.awt.Point;

import ai.Reasoner;
import model.GameState;
import model.Grid;

public class AIPlayer extends AbstractPlayer {

	private Reasoner reasoner;
	
	public AIPlayer(String name, Grid grid) {
		super(name, grid);
	}

	@Override
	public Point chooseMove(GameState state) {
		return this.reasoner.chooseMove(state); 
	}
	
	public void setReasoner(Reasoner reasoner ) {
		this.reasoner = reasoner;
	}
	
	public Reasoner getReasoner() {
		return this.reasoner;
	}
}
