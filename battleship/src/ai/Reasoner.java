package ai;

import model.GameState;
import java.awt.Point;

/**
 * Interface representing the decision-making logic for the AI.
 * Each implementation defines a specific strategy for choosing the next move.
 */
public interface Reasoner {
    
    /**
     * Determines the next coordinates to attack based on the current game state.
     * * @param state The current state of the game, including grid information and hit history.
     * @return A Point object containing the X and Y coordinates for the next shot.
     */
    Point chooseMove(GameState state);
}
