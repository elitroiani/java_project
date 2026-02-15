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

    // Mock minimalista: sovrascriviamo solo quello che serve per non far crashare il test
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
        // Verifica che il controller chieda di piazzare la prima nave
        assertNotNull(mockView.lastStatus);
        assertTrue(mockView.lastStatus.contains("Piazza"), "Lo stato iniziale dovrebbe essere il piazzamento.");
    }

    @Test
    void testPlacementAndTransition() {
        // 1. Reset esplicito della config nel test per essere sicuri
        config.getShipTypes().clear();
        config.getShipTypes().add(new ShipConfig("Nave1", 2, 1));
        config.getShipTypes().add(new ShipConfig("Nave2", 2, 1));
        
        // Re-inizializziamo il controller per leggere la nuova config
        controller = new BattleController(model, mockView, () -> {});

        System.out.println("Navi attese: " + config.getShipTypes().size());

        // 2. Eseguiamo i piazzamenti
        simulatePlacement(0, 0); // Piazza la prima
        System.out.println("Dopo 1° piazzamento - Navi nel model: " + model.getHumanPlayer().getGrid().getShips().size());
        
        simulatePlacement(0, 1); // Piazza la seconda (su riga diversa per evitare collisioni)
        System.out.println("Dopo 2° piazzamento - Navi nel model: " + model.getHumanPlayer().getGrid().getShips().size());

        // 3. Verifica finale
        assertEquals(2, model.getHumanPlayer().getGrid().getShips().size(), "Il model dovrebbe avere 2 navi.");
        
        // Se questo fallisce, guarda il valore di battleStarted
        assertTrue(mockView.battleStarted, "Il controller non ha chiamato switchToPlayMode! Ultimo stato: " + mockView.lastStatus);
    }
}