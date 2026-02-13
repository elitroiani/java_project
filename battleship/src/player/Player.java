package player;

import java.awt.Point;

import ai.Reasoner;
import model.GameState;
import model.Grid;

public interface Player {

	Grid getGrid();
	String getName();
	
	/**
     * Restituisce la prossima mossa.
     * Per HumanPlayer la mossa è fornita dal Controller.
     * Per AIPlayer la mossa è generata dall'IA.
     */
	Point chooseMove(GameState state);  // ritorna la prossima mossa
	
	public Reasoner getReasoner();
}
