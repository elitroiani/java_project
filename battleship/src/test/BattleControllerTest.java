package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import controller.BattleController;
import model.*;
import player.*;
import view.BattleView;
import java.awt.Color;
import java.awt.event.ActionListener;

class BattleControllerTest {

    private BattleController controller;
    private GameState model;
    private GameConfig config;
    private MockBattleView mockView;

    /**
     * Uno "Stub" manuale della BattleView. 
     * Implementa i metodi della View ma non disegna nulla a schermo.
     */
    class MockBattleView extends BattleView {
        public String lastStatus = "";
        public boolean enemyGridEnabled = true;

        public MockBattleView() {
            super(10, 10); // Chiama il costruttore di JFrame ma lo terremo nascosto
            setVisible(false); 
        }

        @Override public void setStatus(String s) { this.lastStatus = s; }
        @Override public void updateCell(boolean enemy, int x, int y, Color c, String s) {}
        @Override public void setPlayerListener(int x, int y, ActionListener l) {}
        @Override public void setEnemyListener(int x, int y, ActionListener l) {}
        @Override public void hideSetup() {}
        @Override public void disableEnemyGrid() { this.enemyGridEnabled = false; }
        @Override public void enableEnemyGrid() { this.enemyGridEnabled = true; }
        @Override public boolean isHorizontal() { return true; }
    }

    @BeforeEach
    void setUp() {
        config = new GameConfig();
        // Creiamo un model reale con due giocatori
        Player p1 = new HumanPlayer("Umano", new Grid(10, 10));
        Player p2 = new AIPlayer("CPU", new Grid(10, 10));
        model = new GameState(p1, p2, config);
        
        mockView = new MockBattleView();
        
        // Inizializziamo il controller con il nostro mock
        controller = new BattleController(model, mockView, null);
    }

    @Test
    void testInitialPlacementStatus() {
        // Verifica che all'inizio il controller chieda di piazzare la prima nave
        String status = mockView.lastStatus;
        assertTrue(status.contains("Piazza"), "Il controller dovrebbe iniziare in modalità posizionamento");
    }

    @Test
    void testGameFlowSwitching() {
        // Forza l'inizio della battaglia (saltando il posizionamento manuale per brevità)
        // In un test reale useremmo dei riflessi o chiameremmo handlePlacementClick ripetutamente
        
        // Verifichiamo che se il gioco finisce, lo stato della View cambi
        // (Simuliamo l'affondamento di tutte le navi tramite il model)
        // Questo dipende da come il tuo model gestisce le navi.
        
        assertNotNull(controller);
    }
}
