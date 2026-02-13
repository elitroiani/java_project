package placer;

import java.util.ArrayList;
import java.util.List;

import model.GameConfig;
import model.GameState;
import model.Ship;
import model.ShipConfig;
import player.Player;

/**
 * Base implementation for all automatic ship positioning strategies.
 * It uses the Template Method pattern to handle the fleet iteration logic, 
 * while delegating specific coordinate selection to subclasses.
 */
public abstract class AbstractAutomaticShipPlacer implements AutomaticShipPlacer {

    protected final GameConfig config;

    /**
     * Initializes the placer with game settings.
     * @param config Configuration containing ship types and counts.
     */
    public AbstractAutomaticShipPlacer(GameConfig config) {
        this.config = config;
    }

    /**
     * Attempts to place a single ship by delegating to the player's grid.
     * @return true if the grid accepts the placement, false otherwise.
     */
    @Override
    public boolean placeShip(GameState gameState, Player player, Ship ship, int x, int y, boolean horizontal) {
        return player.getGrid().placeShip(ship, x, y, horizontal);
    }

    /**
     * Automatically positions the entire fleet defined in the GameConfig.
     * It iterates through each ship type and attempts placement until successful 
     * or until the retry limit is reached.
     * * @param gameState The current state of the game.
     * @param player The player whose grid will be populated.
     * @return A list of successfully placed Ship objects.
     * @throws IllegalStateException if a ship cannot be placed after 100 attempts.
     */
    @Override
    public List<Ship> placeAllShips(GameState gameState, Player player) {
        List<Ship> placedShips = new ArrayList<>();

        for (ShipConfig sc : config.getShipTypes()) {
            for (int i = 0; i < sc.getCount(); i++) {
                boolean placed = false;
                int attempts = 0;

                // Create the ship instance based on the current configuration
                Ship ship = new Ship(sc); 

                // Retry loop to handle collisions or out-of-bounds placements
                while (!placed && attempts < 100) {
                    int x = getX(gameState, player, ship);
                    int y = getY(gameState, player, ship);
                    boolean horizontal = isHorizontal(gameState, player, ship);

                    placed = placeShip(gameState, player, ship, x, y, horizontal);
                    attempts++;
                }

                // If placement fails consistently, the grid might be too small or congested
                if (!placed) {
                    throw new IllegalStateException("Failed to place ship: " + sc.getName() + " after 100 attempts.");
                }

                placedShips.add(ship); 
            }
        }

        return placedShips;
    }

    // --- Abstract methods to be implemented by concrete strategies (e.g., Random, Clustered) ---

    /** @return The chosen X coordinate for the ship. */
    protected abstract int getX(GameState gameState, Player player, Ship ship);

    /** @return The chosen Y coordinate for the ship. */
    protected abstract int getY(GameState gameState, Player player, Ship ship);

    /** @return True for horizontal orientation, false for vertical. */
    protected abstract boolean isHorizontal(GameState gameState, Player player, Ship ship);
}
