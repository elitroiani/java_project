package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import placer.RandomShipPlacer;
import player.*;
import java.util.List;

class ShipPlacerTest {

    private Grid grid;
    private GameConfig config;
    private Player player;

    @BeforeEach
    void setUp() {
        // Initialize a standard 10x10 grid
        grid = new Grid(10, 10);
        
        // Assume config is pre-loaded with the standard 5-ship fleet
        // (Carrier, Battleship, Destroyer, Submarine, Patrol Boat)
        config = new GameConfig(); 
        player = new HumanPlayer("Tester", grid);
    }

    @Test
    void testRandomPlacementSuccess() {
        RandomShipPlacer placer = new RandomShipPlacer(config);
        
        // Execute the automatic placement of the entire fleet
        List<Ship> placedShips = placer.placeAllShips(null, player);
        
        // Verification 1: The list must contain exactly 5 ships as per game rules
        assertEquals(5, placedShips.size(), "The placer should always return exactly 5 ships");
        
        // Verification 2: The grid's internal list must also contain these 5 ships
        int shipsInGrid = player.getGrid().getShips().size();
        assertEquals(5, shipsInGrid, "The grid must have 5 ships registered after placement");
    }
}
