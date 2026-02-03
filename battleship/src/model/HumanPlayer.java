package model;

import java.awt.Point;

public class HumanPlayer extends AbstractPlayer{

	public HumanPlayer(String name, Grid grid) {
		super(name, grid);
	}

	@Override
	public Point chooseMove() {
		// La mossa reale viene fornita dal Controller
        throw new UnsupportedOperationException(
            "Human move must be provided by the controller"
        );
    }

}
