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
            // Passiamo p1 (l'IA) sia come giocatore 1 che come giocatore 2 
            // per soddisfare il costruttore della superclasse
            super(p1, p1, config); 
            this.testGrid = grid;
        }

        @Override
        public Grid getEnemyGrid(Player p) {
            // Ignoriamo la logica standard e restituiamo la griglia che vogliamo testare
            return testGrid;
        }
    }

    @BeforeEach
    void setUp() {
        // 1. Crea prima la configurazione
        config = new GameConfig(); 
        
        // 2. Crea la griglia
        grid = new Grid(10, 10);
        
        // 3. Crea il Player (Assicurati che AIPlayer sia istanziato qui!)
        aiPlayer = new AIPlayer("AI", new Grid(10, 10)); 
        
        // 4. Ora crea lo stub passandogli l'aiPlayer già esistente
        simpleState = new TestGameState(grid, aiPlayer, config);

        // 5. Infine inizializza il reasoner
        reasoner = new ExpertReasoner(aiPlayer, config);
        
        // Se AIPlayer ha bisogno del reasoner per funzionare, collegalo:
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
    void testTargetingModeAfterHit() {
        // Scenario: We manually simulate a HIT at (5,5) on the grid.
        // The MediumReasoner should detect this and shoot nearby.
        
        // Manually force the grid state
        // Note: Assuming your Grid/Cell classes have setters exposed for testing, 
        // or you simulate a shot. Here I assume a way to set state exists.
        simulateHitAt(5, 5);

        Point move = reasoner.chooseMove(simpleState);

        // Calculate distance to the hit (Manhattan distance should be 1 for orthogonal neighbors)
        int distance = Math.abs(move.x - 5) + Math.abs(move.y - 5);
        
        assertEquals(1, distance, 
            "After a hit at (5,5), the AI should target an immediate neighbor. Move was: " + move);
    }

    @Test
    void testFollowDirectionVertical() {
        // Scenario: Two hits in a column (5,5) and (5,6).
        // The AI should recognize the pattern and shoot at (5,4) or (5,7).
        
        simulateHitAt(5, 5);
        simulateHitAt(5, 6); // DOWN direction implies vertical

        // We need to 'prime' the reasoner state. 
        // Since MediumReasoner is stateful, simply calling chooseMove might pick randomly 
        // if it hasn't tracked the previous moves internally.
        // However, your code re-scans the grid in 'updateCandidatesFromHits'.
        
        Point move = reasoner.chooseMove(simpleState);

        // It must stay on column 5
        assertEquals(5, move.x, "AI should stay on the vertical line (x=5)");
        
        // It should pick 5,4 or 5,7 (neighbors of the cluster)
        boolean validVerticalMove = (move.y == 4 || move.y == 7);
        assertTrue(validVerticalMove, "AI should extend the vertical line. Got: " + move);
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