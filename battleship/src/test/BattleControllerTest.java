package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.BattleController;
import model.*;
import player.*;
import view.BattleView;
import java.awt.Point;

class BattleControllerTest {

    private BattleController controller;
    private GameState model;
    private GameConfig config;
    private MockBattleView mockView;

    // Minimalist mock: we only overwrite what is needed to avoid crashing the test
    class MockBattleView extends BattleView {
        public String lastStatus = "";
        public boolean battleStarted = false;

        public MockBattleView() {
            super(10, 10);
            setVisible(false);
        }

        @Override public void setStatus(String s) { this.lastStatus = s; }
        @Override public void switchToPlayMode() { this.battleStarted = true; }
        @Override public void updateCell(boolean e, int x, int y, java.awt.Color c, String s) {}
        @Override public void refreshView() {}
        @Override public void showResults(String w) {}
    }

    @BeforeEach
    void setUp() {
        config = new GameConfig();
        
        Player p1 = new HumanPlayer("TestPlayer", new Grid(10, 10));
        Player p2 = new AIPlayer("CPU", new Grid(10, 10));
        model = new GameState(p1, p2, config);
        
        mockView = new MockBattleView();
        controller = new BattleController(model, mockView, () -> {});
    }

    @Test
    void testInitialStatus() {
        // Verify that the controller asks to place the first ship
        assertNotNull(mockView.lastStatus);
        assertTrue(mockView.lastStatus.contains("Piazza"), "Lo stato iniziale dovrebbe essere il piazzamento.");
    }

    @Test
    void testPlacementAndTransition() {
        // 1. Explicit config reset in test to be safe
        config.getShipTypes().clear();
        config.getShipTypes().add(new ShipConfig("Nave1", 2, 1));
        config.getShipTypes().add(new ShipConfig("Nave2", 2, 1));
        
        // Re-initialize the controller to read the new config
        controller = new BattleController(model, mockView, () -> {});

        System.out.println("Navi attese: " + config.getShipTypes().size());

        // 2. Perform the placings
        simulatePlacement(0, 0); // Places the first
        System.out.println("Dopo 1° piazzamento - Navi nel model: " + model.getHumanPlayer().getGrid().getShips().size());
        
        simulatePlacement(0, 2); // Places the second (on a different line to avoid collisions)
        System.out.println("Dopo 2° piazzamento - Navi nel model: " + model.getHumanPlayer().getGrid().getShips().size());

        // 3. Final check
        assertEquals(2, model.getHumanPlayer().getGrid().getShips().size(), "Il model dovrebbe avere 2 navi.");
        
    }
    
    private void simulatePlacement(int x, int y) {
        try {
            // Look for the "handlePlacementClick" method in BattleController
            java.lang.reflect.Method method = controller.getClass().getDeclaredMethod("handlePlacementClick", int.class, int.class);
            method.setAccessible(true);
            method.invoke(controller, x, y);
        } catch (Exception e) {
            fail("Errore nella simulazione del click: " + e.getMessage());
        }
    }
}