package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import player.HumanPlayer;

import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class BattleshipModelTest {

    private Grid grid;
    private ShipConfig smallShipConfig;
    private ShipConfig largeShipConfig;

    @BeforeEach
    void setUp() {
        grid = new Grid(10, 10);
        smallShipConfig = new ShipConfig("Patrol Boat", 2, 1);
        largeShipConfig = new ShipConfig("Carrier", 5, 1);
    }

    @Test
    void testShipPlacementSuccess() {
        Ship ship = new Ship(smallShipConfig);
        // Place horizontally at (0,0)
        boolean placed = grid.placeShip(ship, 0, 0, true);
        
        assertTrue(placed, "Ship should be placed successfully");
        assertEquals(CellState.NOTFIRED, grid.getCell(0, 0).getState());
        assertTrue(grid.getCell(0, 0).hasShip());
        assertTrue(grid.getCell(1, 0).hasShip());
    }

    @Test
    void testBoundaryViolation() {
        Ship ship = new Ship(largeShipConfig);
        // Try to place a size 5 ship at X=7 (will go out of bounds at X=11)
        boolean placed = grid.placeShip(ship, 7, 0, true);
        
        assertFalse(placed, "Ship should not be placed out of bounds");
    }

    @Test
    void testProximityRuleViolation() {
        Ship ship1 = new Ship(smallShipConfig);
        grid.placeShip(ship1, 2, 2, true); // Occupies (2,2) and (3,2)

        Ship ship2 = new Ship(smallShipConfig);
        // Try to place another ship in (3,3) - touching diagonally
        boolean placed = grid.placeShip(ship2, 3, 3, true);

        assertFalse(placed, "Should fail: ships cannot touch (3x3 rule)");
    }

    @Test
    void testSinkingLogic() {
        Ship ship = new Ship(smallShipConfig);
        grid.placeShip(ship, 0, 0, true);

        // First hit
        MoveResult res1 = grid.fireAt(0, 0);
        assertEquals(MoveResult.HIT, res1);
        assertFalse(ship.isSunk());

        // Second hit (sinking shot)
        MoveResult res2 = grid.fireAt(1, 0);
        assertEquals(MoveResult.SUNK, res2);
        assertTrue(ship.isSunk());
    }

    @Test
    void testFireAtSameCellTwice() {
        grid.fireAt(5, 5); // First shot (MISS)
        MoveResult res = grid.fireAt(5, 5); // Second shot
        
        assertEquals(MoveResult.ALREADY_FIRED, res, "Should return ALREADY_FIRED for repeat shots");
    }

    @Test
    void testAllShipsSunk() {
    	GameConfig config = new GameConfig();
    	HumanPlayer p = new HumanPlayer("human", grid);
    	GameState state = new GameState(p, null, config);
        Ship ship = new Ship(smallShipConfig);
        grid.placeShip(ship, 0, 0, true);

        grid.fireAt(0, 0);
        grid.fireAt(1, 0);

        assertTrue(grid.allShipsSunk(), "Grid should report all ships sunk");
        assertTrue(state.isGameOver(), "Grid Shoul report gameOver");
    }
}