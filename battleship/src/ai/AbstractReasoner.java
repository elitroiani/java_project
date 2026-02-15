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

/**
 * Abstract base class for AI reasoning logic.
 * Provides shared utilities for move selection and grid analysis.
 */
public abstract class AbstractReasoner implements Reasoner {

    /** Random number generator for stochastic move selection */
    protected final Random random = new Random();
    
    /** The player associated with this reasoner */
    protected final Player player;
    
    /** Global game configuration containing fleet and grid rules */
    protected final GameConfig config;

    /**
     * Initializes the reasoner with the acting player and game configuration.
     * @param player The AI player.
     * @param config The current game configuration.
     */
    public AbstractReasoner(Player player, GameConfig config) {
        this.player = player;
        this.config = config;
    }
    
    /**
     * Abstract method to be implemented by specific AI strategies.
     * @param state The current snapshot of the game.
     * @return The chosen coordinate for the next move.
     */
    public abstract Point chooseMove(GameState state);
    
    /**
     * Retrieves a list of coordinates for all cells that have not been attacked yet.
     * @param grid The opponent's grid to analyze.
     * @return A list of Point objects representing untouched coordinates.
     */
    protected List<Point> getUntouchedCells(Grid grid) {
        return grid.getSmartUntouchedCells().stream()                        
                                       .map(s -> s.getCoordinates())
                                       .toList(); 
    }
    
    /**
     * Selects a random coordinate from the available untouched cells.
     * @param state The current game state.
     * @return A random Point that is valid for a move.
     * @throws IllegalStateException if no untouched cells remain.
     */
    protected Point randomCellPicker(GameState state) {
        Grid enemyGrid = state.getEnemyGrid(this.player);

        List<Point> available = getUntouchedCells(enemyGrid);

        if (available.isEmpty()) {
            throw new IllegalStateException("No valid moves available");
        }
        
        // Return a completely random choice from available cells
        return available.get(random.nextInt(available.size()));
    }
    
    /**
     * Identifies adjacent cells (North, South, East, West) that have not been targeted.
     * @param grid The grid to check.
     * @param x The X coordinate of the reference cell.
     * @param y The Y coordinate of the reference cell.
     * @return A list of valid adjacent Points in NOTFIRED state.
     */
    protected List<Point> getAdjacentUntouched(Grid grid, int x, int y) {
        List<Point> result = new ArrayList<>();
        // Define orthogonal movements only (up, down, left, right)
        int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1}}; 

        for (int[] d : deltas) {
            int nx = x + d[0];
            int ny = y + d[1];

            // Verify coordinates are within bounds and the cell hasn't been attacked
            if (grid.isValidCoordinate(nx, ny) &&
                grid.getCellState(nx, ny) == CellState.NOTFIRED) {
                result.add(new Point(nx, ny));
            }
        }

        return result;
    }

}
