package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.*;
import model.*;
import player.*;

import java.awt.Point;

class ReasonerNoMockTest {

    private Grid grid;
    private GameConfig config;
    private Player aiPlayer;
    private Player enemyPlayer;
    private Reasoner reasoner;
    private GameState simpleState;

    // A simple stub to simulate GameState without complex setup
    // This allows us to control exactly what grid the AI sees.
    class TestGameState extends GameState {
        private final Grid testGrid;

        public TestGameState(Grid grid, Player p1, GameConfig config) {
            // We pass p1 (the AI) as both player 1 and player 2 
        	// to satisfy the superclass constructor
            super(p1, p1, config); 
            this.testGrid = grid;
        }

        @Override
        public Grid getEnemyGrid(Player p) {
            // We ignore the standard logic and return the grid we want to test
            return testGrid;
        }
    }

    @BeforeEach
    void setUp() {
        // 1. Create the configuration first
        config = new GameConfig(); 
        
        // 2. Create the grid
        grid = new Grid(10, 10);
        
        // 3. Create the Player (Make sure AIPlayer is instantiated here!)
        aiPlayer = new AIPlayer("AI", new Grid(10, 10)); 
        
        // 4. Now create the stub by passing it the existing aiPlayer
        simpleState = new TestGameState(grid, aiPlayer, config);

        // 5. Finally initialize the reasoner
        reasoner = new ExpertReasoner(aiPlayer, config);
        
        // If AIPlayer needs the reasoner to work, connect it:
        aiPlayer.setReasoner(reasoner); 
    }
    
    @Test
    void testFirstMoveIsRandomAndValid() {
        // Scenario: The grid is completely empty (NOTFIRED).
        
        Point move = reasoner.chooseMove(simpleState);

        assertNotNull(move, "The reasoner should return a Point object");
        
        // Verify coordinates are within bounds
        assertTrue(move.x >= 0 && move.x < 10, "X coordinate out of bounds");
        assertTrue(move.y >= 0 && move.y < 10, "Y coordinate out of bounds");
        
        // Verify it didn't pick a cell that was somehow blocked (impossible on empty grid, but good check)
        assertEquals(CellState.NOTFIRED, grid.getCellState(move.x, move.y));
    }

    @Test
    void testAvoidsAlreadyHitCells() {
        // Scenario: Fill the top-left corner with Misses.
        // AI should not shoot there.
        
        simulateMissAt(0, 0);
        simulateMissAt(0, 1);
        simulateMissAt(1, 0);
        simulateMissAt(1, 1);

        // Try multiple times to ensure randomness doesn't accidentally pick one
        for (int i = 0; i < 20; i++) {
            Point move = reasoner.chooseMove(simpleState);
            assertFalse(move.x <= 1 && move.y <= 1, 
                "AI picked an already visited cell at " + move);
        }
    }

    @Test
    void testEndGameHunt() {
        // Scenario: Grid is full except for one cell (9,9).
        // The AI must find it.
        
        fillGridExcept(9, 9);
        
        Point move = reasoner.chooseMove(simpleState);
        
        assertEquals(new Point(9, 9), move, "AI must find the last remaining cell");
    }

    // --- Helper Methods to manipulate the Real Grid ---

    private void simulateHitAt(int x, int y) {
        // Bypass the game logic and directly modify the cell
        grid.getCell(x, y).setState(CellState.HIT);
    }

    private void simulateMissAt(int x, int y) {
        grid.getCell(x, y).setState(CellState.MISS);
    }

    private void fillGridExcept(int saveX, int saveY) {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (x == saveX && y == saveY) continue;
                simulateMissAt(x, y);
            }
        }
    }
}