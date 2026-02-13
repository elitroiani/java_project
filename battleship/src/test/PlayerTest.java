package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Grid;
import player.*;

class PlayerTest {

    private Grid dummyGrid;

    @BeforeEach
    void setUp() {
        // Creiamo una griglia minima solo per soddisfare il costruttore
        // ed evitare la IllegalArgumentException
        dummyGrid = new Grid(10, 10);
    }

    @Test
    void testGetters() {
        Player player = new HumanPlayer("TestPlayer", dummyGrid);
        assertEquals("TestPlayer", player.getName());
        assertEquals(dummyGrid, player.getGrid());
    }

    @Test
    void testHumanPlayerException() {
        Player human = new HumanPlayer("User", dummyGrid);
        
        // Ora il costruttore passa, quindi possiamo testare il metodo chooseMove
        assertThrows(UnsupportedOperationException.class, () -> {
            human.chooseMove(null); 
        }, "L'umano deve lanciare UnsupportedOperationException");
    }

    @Test
    void testAIPlayerReasonerAssignment() {
        AIPlayer ai = new AIPlayer("Bot", dummyGrid);
        
        // Verifichiamo il set e get ereditati o implementati
        ai.setReasoner(state -> null);
        assertNotNull(ai.getReasoner());
    }
}