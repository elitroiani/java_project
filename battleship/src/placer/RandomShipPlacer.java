package placer;

import java.util.Random;

import model.GameConfig;
import model.GameState;
import model.Grid;
import model.Ship;
import player.Player;

/**
 * Piazzamento casuale delle navi.
 * Navi grandi prima se l'astratta ordina le ShipConfig.
 */
public class RandomShipPlacer extends AbstractAutomaticShipPlacer {

    private final Random rand = new Random();

    public RandomShipPlacer(GameConfig config) {
        super(config);
    }

    @Override
    protected int getX(GameState gameState, Player player, Ship ship) {
        Grid grid = player.getGrid();
        boolean horizontal = isHorizontal(gameState, player, ship);
        return horizontal
            ? rand.nextInt(grid.getWidth() - ship.getSize() + 1)
            : rand.nextInt(grid.getWidth());
    }

    @Override
    protected int getY(GameState gameState, Player player, Ship ship) {
        Grid grid = player.getGrid();
        boolean horizontal = isHorizontal(gameState, player, ship);
        return horizontal
            ? rand.nextInt(grid.getHeight())
            : rand.nextInt(grid.getHeight() - ship.getSize() + 1);
    }

    @Override
    protected boolean isHorizontal(GameState gameState, Player player, Ship ship) {
        return rand.nextBoolean();
    }
}
