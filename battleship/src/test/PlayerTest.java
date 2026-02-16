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
        // We create a minimal grid just to satisfy the constructor 
    	// and avoid the IllegalArgumentException
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
        
        // Now the constructor passes, so we can test the chooseMove method
        assertThrows(UnsupportedOperationException.class, () -> {
            human.chooseMove(null); 
        }, "L'umano deve lanciare UnsupportedOperationException");
    }

    @Test
    void testAIPlayerReasonerAssignment() {
        AIPlayer ai = new AIPlayer("Bot", dummyGrid);
        
        // We verify the inherited or implemented set and get
        ai.setReasoner(state -> null);
        assertNotNull(ai.getReasoner());
    }
}