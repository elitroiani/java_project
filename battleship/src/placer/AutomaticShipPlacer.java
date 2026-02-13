package placer;

import java.util.List;
import model.GameState;
import model.Ship;
import player.Player;

/**
 * Common interface for all automated ship placement strategies.
 * It defines how a single ship or an entire fleet should be positioned on a player's grid,
 * allowing for interchangeable algorithms (e.g., Random, Strategic, or Pattern-based).
 */
public interface AutomaticShipPlacer {

    /**
     * Attempts to place a single ship at the specified coordinates and orientation.
     * * @param gameState The current global state of the game.
     * @param player The player who owns the grid where the ship is being placed.
     * @param ship The ship instance to be positioned.
     * @param x The target X coordinate (column).
     * @param y The target Y coordinate (row).
     * @param horizontal True for horizontal placement, false for vertical.
     * @return true if the placement was successful according to grid rules, false otherwise.
     */
    boolean placeShip(GameState gameState, Player player, Ship ship, int x, int y, boolean horizontal);

    /**
     * Automatically positions all ships required for the game onto the player's grid.
     * * This method is typically used by AI players or for "Auto-setup" features.
     * It handles the logic of iterating through the available fleet and resolving collisions.
     * * @param gameState The current global state of the game.
     * @param player The player for whom the fleet is being set up.
     * @return A list of all Ship objects that were successfully placed on the grid.
     */
    List<Ship> placeAllShips(GameState gameState, Player player);
}