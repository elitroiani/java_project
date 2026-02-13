package placer;

import java.util.Random;
import model.GameConfig;
import model.GameState;
import model.Grid;
import model.Ship;
import player.Player;

/**
 * Advanced ship positioning strategy.
 * This placer applies heuristics to make the fleet harder to hit:
 * - Large ships prefer horizontal orientation.
 * - Large ships avoid the extreme edges of the grid to reduce predictability.
 */
public class HardShipPlacer extends AbstractAutomaticShipPlacer {

    private final Random rand = new Random();

    /**
     * Initializes the strategic placer.
     * @param config The game configuration.
     */
    public HardShipPlacer(GameConfig config) {
        super(config);
    }

    /**
     * Bias orientation based on ship size.
     * Large ships (size > 3) are always horizontal, while smaller ones are random.
     */
    @Override
    protected boolean isHorizontal(GameState gameState, Player player, Ship ship) {
        // Larger ships tend to be horizontal, smaller ones are random
        return ship.getSize() > 3 || rand.nextBoolean();
    }

    /**
     * Calculates the X coordinate with a bias against the edges for horizontal ships.
     */
    @Override
    protected int getX(GameState gameState, Player player, Ship ship) {
        Grid grid = player.getGrid();
        boolean horizontal = isHorizontal(gameState, player, ship);

        if (horizontal) {
            // Avoid the first and last column for horizontal ships if possible
            int maxX = grid.getWidth() - ship.getSize() - 1;
            return 1 + rand.nextInt(Math.max(1, maxX));
        } else {
            // For vertical ships, any column is equally likely
            return rand.nextInt(grid.getWidth());
        }
    }

    /**
     * Calculates the Y coordinate with a bias against the edges for vertical ships.
     */
    @Override
    protected int getY(GameState gameState, Player player, Ship ship) {
        Grid grid = player.getGrid();
        boolean horizontal = isHorizontal(gameState, player, ship);

        if (!horizontal) {
            // Avoid the first and last row for vertical ships if possible
            int maxY = grid.getHeight() - ship.getSize() - 1;
            return 1 + rand.nextInt(Math.max(1, maxY));
        } else {
            // For horizontal ships, any row is equally likely
            return rand.nextInt(grid.getHeight());
        }
    }
}