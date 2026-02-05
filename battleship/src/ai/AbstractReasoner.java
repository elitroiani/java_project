package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.CellState;
import model.GameConfig;
import model.GameState;
import model.Grid;
import player.Player;

public abstract class AbstractReasoner implements Reasoner{

    protected final Random random = new Random();
    protected final Player player;
    protected final GameConfig config;
    protected final GameState state;

    public AbstractReasoner(Player player, GameConfig config, GameState state) {
    	this.player = player;
        this.config = config;
        this.state = state;
    }
    
    public abstract Point chooseMove();
    
    //grid.metodo per avere le celle non toccate;
    protected List<Point> getUntouchedCells(Grid grid) {
		return grid.getUntouchedCells().stream()						
									   .map(s -> s.getCoordinates())
									   .toList(); 
    }
    
    protected Point randomCellPicker(GameState state) {
    	Grid enemyGrid = state.getEnemyGrid(this.player);

    	List<Point> available = getUntouchedCells(enemyGrid);

    	if (available.isEmpty()) {
    		throw new IllegalStateException("No valid moves available");
    	}
    	// scelta completamente casuale
    	return available.get(random.nextInt(available.size()));
    }
    
    protected List<Point> getAdjacentUntouched(Grid grid, int x, int y) {
        List<Point> result = new ArrayList<>();
        int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1}}; 						// solo ortogonali

        for (int[] d : deltas) {
            int nx = x + d[0];
            int ny = y + d[1];

            if (grid.isValidCoordinate(nx, ny) &&
                grid.getCellState(nx, ny) == CellState.NOTFIRED) {
                result.add(new Point(nx, ny));
            }
        }

        return result;
    }

}
