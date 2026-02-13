package placer;

import java.util.List;
import model.GameConfig;
import model.GameState;
import model.Ship;
import model.ShipConfig;
import player.Player;

/**
 * Manages the manual placement of ships via user interaction.
 * Acts as a bridge between the Controller's click events and the Player's Grid.
 */
public class ManualShipPlacer {

    private final GameConfig config;

    public ManualShipPlacer(GameConfig config) {
        this.config = config;
    }

    /**
     * Attempts to place a single ship on the player's grid.
     * * @param player The player whose grid will be modified.
     * @param model 
     * @param ship The ship instance to be placed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param horizontal orientation.
     * @return true if the grid accepted the placement, false if invalid (overlap or out of bounds).
     */
    public boolean placeShip(GameState model, Player player, Ship ship, int x, int y, boolean horizontal) {
        // La griglia deve contenere la logica di validazione (canPlaceShip)
        // all'interno del metodo placeShip stesso.
        return player.getGrid().placeShip(ship, x, y, horizontal);
    }

    /**
     * @return The list of ship configurations defined for the current game mode.
     */
    public List<ShipConfig> getShipsToPlace() {
        return config.getShipTypes();
    }
}