package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.GameConfig;
import model.GameState;
import model.Grid;

public abstract class AbstractReasoner implements Reasoner{

    protected final Random random = new Random();
    protected final GameConfig config;

    public AbstractReasoner(GameConfig config) {
        this.config = config;
    }

    protected List<Point> getUntouchedCells(Grid grid) {
		return null; //grid.metodo per avere le celle non toccate;
    }
    
    protected Point randomCellPicker(GameState state) {
    	Grid enemyGrid = state.getEnemyGrid(state);

    	List<Point> available = getUntouchedCells(enemyGrid);

    	if (available.isEmpty()) {
    		throw new IllegalStateException("No valid moves available");
    	}
    	// scelta completamente casuale
    	return available.get(random.nextInt(available.size()));
    }
    
    protected List<Point> getAdjacentUntouched(Grid grid, int x, int y) {
	    List<Point> result = new ArrayList<>();

	    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

	    for (int[] d : dirs) {
	        int nx = x + d[0];
	        int ny = y + d[1];

	        if (grid.isInside(nx, ny) && grid.isCellUntouched(nx, ny)) {
	            result.add(new Point(nx, ny));
	        }
	    }
	    return result;
    }
}
