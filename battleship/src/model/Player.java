package model;

import java.awt.Point;

public interface Player {

	Grid getGrid();
	String getName();
	
	/**
     * Restituisce la prossima mossa.
     * Per HumanPlayer la mossa è fornita dal Controller.
     * Per AIPlayer la mossa è generata dall'IA.
     */
	Point chooseMove();  // ritorna la prossima mossa
}
