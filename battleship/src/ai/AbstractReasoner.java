package ai;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import model.GameConfig;
import model.GameState;
import model.Grid;

public abstract class AbstractReasoner implements Reasoner{

    protected final Random random = new Random();
    protected final GameConfig config;

    protected AbstractReasoner(GameConfig config) {
        this.config = config;
    }

    protected List<Point> getUntouchedCells(Grid grid) {
		return null;
    }
	
}
