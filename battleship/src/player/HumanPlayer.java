package player;

import java.awt.Point;
import model.GameState;
import model.Grid;

/**
 * Represents a human player.
 * Unlike the AI, the human player doesn't compute a move autonomously 
 * within the chooseMove method; instead, it relies on external input 
 * from the game controller or UI.
 */
public class HumanPlayer extends AbstractPlayer {
    
    /**
     * Constructs a human player with a name and their grid.
     * @param name The player's name.
     * @param grid The grid where their ships are placed.
     */
    public HumanPlayer(String name, Grid grid) {
        super(name, grid);
    }

    /**
     * This method is not used for human players because their moves 
     * are provided asynchronously via UI events handled by the Controller.
     * @param state The current state of the game.
     * @throws UnsupportedOperationException always, to signal that the Controller 
     * must provide the move directly.
     */
    @Override
    public Point chooseMove(GameState state) {
        // The actual move is provided by the Controller/GUI interaction.
        throw new UnsupportedOperationException("Human move must be provided by the controller via user interaction");
    }
}
