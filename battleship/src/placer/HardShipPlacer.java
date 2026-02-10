package placer;

import java.util.Random;

import model.GameConfig;
import model.GameState;
import model.Grid;
import model.Ship;
import player.Player;

/**
 * Piazzamento strategico delle navi.
 * - Navi grandi preferiscono orizzontale
 * - Evita bordi estremi per navi grandi
 */
public class HardShipPlacer extends AbstractAutomaticShipPlacer {

    private final Random rand = new Random();

    public HardShipPlacer(GameConfig config) {
        super(config);
    }

    @Override
    protected boolean isHorizontal(GameState gameState, Player player, Ship ship) {
        // Navi grandi tendono ad orizzontale, le piccole random
        return ship.getSize() > 3 || rand.nextBoolean();
    }

    @Override
    protected int getX(GameState gameState, Player player, Ship ship) {
        Grid grid = player.getGrid();
        boolean horizontal = isHorizontal(gameState, player, ship);

        if (horizontal) {
            int maxX = grid.getWidth() - ship.getSize() - 1;
            return 1 + rand.nextInt(Math.max(1, maxX));
        } else {
            return rand.nextInt(grid.getWidth());
        }
    }

    @Override
    protected int getY(GameState gameState, Player player, Ship ship) {
        Grid grid = player.getGrid();
        boolean horizontal = isHorizontal(gameState, player, ship);

        if (!horizontal) {
            int maxY = grid.getHeight() - ship.getSize() - 1;
            return 1 + rand.nextInt(Math.max(1, maxY));
        } else {
            return rand.nextInt(grid.getHeight());
        }
    }
}