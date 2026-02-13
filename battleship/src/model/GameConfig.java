package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Global game configuration settings, including grid dimensions and ship fleet composition.
 */
public class GameConfig {

    // Default grid and gameplay constants
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final int NUMBER_OF_SHIPS = 5;

    private final List<ShipConfig> shipTypes;

    /**
     * Initializes the standard configuration with a classic battleship fleet.
     */
    public GameConfig() {
        this.shipTypes = new ArrayList<>();

        // Standard fleet composition: Name, Size, and Quantity
        shipTypes.add(new ShipConfig("Carrier", 5, 1));
        shipTypes.add(new ShipConfig("Battleship", 4, 1));
        shipTypes.add(new ShipConfig("Cruiser", 3, 1));
        shipTypes.add(new ShipConfig("Submarine", 3, 1));
        shipTypes.add(new ShipConfig("Destroyer", 2, 1));
    }

    // --- GETTERS ---
    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
    
    public int getGridSize() {
        return WIDTH;
    }
    
    public int getShips() {
        return NUMBER_OF_SHIPS;
    }

    /**
     * Returns a copy of the ship configurations to ensure the internal list 
     * remains immutable from outside the class.
     * @return A list of ship types and their properties.
     */
    public List<ShipConfig> getShipTypes() {
        return new ArrayList<>(shipTypes); 
    }
}