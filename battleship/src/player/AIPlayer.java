package player;

import java.awt.Point;
import ai.Reasoner;
import model.GameState;
import model.Grid;

/**
 * Represents a computer-controlled player.
 * This class uses a Strategy pattern by delegating its decision-making 
 * logic to a Reasoner implementation.
 */
public class AIPlayer extends AbstractPlayer {
    
    /**
     * Constructs an AI player with a name and a grid.
     * @param name Name of the AI (e.g., "Computer").
     * @param grid The grid where the AI's ships are placed.
     */
    public AIPlayer(String name, Grid grid) {
        super(name, grid);
    }

    /**
     * Delegates the move selection to the assigned Reasoner.
     * @param state The current state of the game.
     * @return The next target Point calculated by the AI.
     */
    @Override
    public Point chooseMove(GameState state) {
        return this.reasoner.chooseMove(state); 
    }
}
