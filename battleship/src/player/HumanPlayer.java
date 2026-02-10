package player;

import java.awt.Point;

import model.GameState;
import model.Grid;

public class HumanPlayer extends AbstractPlayer{
	
	public HumanPlayer(String name, Grid grid) {
		super(name, grid);
	}

	@Override
	public Point chooseMove(GameState state) {
		// La mossa reale viene fornita dal Controller
        throw new UnsupportedOperationException("Human move must be provided by the controller");
	}

}
