package placer;

import java.util.Random;
import model.GameConfig;
import model.GameState;
import model.Grid;
import model.Ship;
import player.Player;

/**
 * Basic implementation for random ship placement.
 * It selects coordinates and orientation purely by chance, 
 * ensuring they fall within the legal boundaries of the grid.
 */
public class RandomShipPlacer extends AbstractAutomaticShipPlacer {

    private final Random rand = new Random();

    /**
     * Constructs a random placer with the given game configuration.
     * @param config The game configuration defining ships to be placed.
     */
    public RandomShipPlacer(GameConfig config) {
        super(config);
    }

    /**
     * Calculates a random X coordinate.
     * If the ship is horizontal, it limits the range to ensure the ship 
     * doesn't protrude past the right edge.
     */
    @Override
    protected int getX(GameState gameState, Player player, Ship ship) {
        Grid grid = player.getGrid();
        boolean horizontal = isHorizontal(gameState, player, ship);
        
        // If horizontal, the starting X plus ship size must not exceed grid width
        return horizontal
            ? rand.nextInt(grid.getWidth() - ship.getSize() + 1)
            : rand.nextInt(grid.getWidth());
    }

    /**
     * Calculates a random Y coordinate.
     * If the ship is vertical, it limits the range to ensure the ship 
     * doesn't protrude past the bottom edge.
     */
    @Override
    protected int getY(GameState gameState, Player player, Ship ship) {
        Grid grid = player.getGrid();
        boolean horizontal = isHorizontal(gameState, player, ship);
        
        // If vertical, the starting Y plus ship size must not exceed grid height
        return horizontal
            ? rand.nextInt(grid.getHeight())
            : rand.nextInt(grid.getHeight() - ship.getSize() + 1);
    }

    /**
     * Randomly decides the orientation of the ship.
     * @return true for horizontal, false for vertical.
     */
    @Override
    protected boolean isHorizontal(GameState gameState, Player player, Ship ship) {
        return rand.nextBoolean();
    }
}
