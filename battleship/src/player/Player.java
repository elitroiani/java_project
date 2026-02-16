package player;

import java.awt.Point;
import java.util.Optional;

import ai.Reasoner;
import model.GameState;
import model.Grid;

/**
 * Defines the core behavior of a player in the game.
 * By including reasoning capabilities, this interface supports both 
 * autonomous AI moves and potential AI-assisted hints for human players.
 */
public interface Player {

    /**
     * @return The grid owned by this player (where their ships are placed).
     */
    Grid getGrid();

    /**
     * @return The display name of the player.
     */
    String getName();
    
    /**
     * Determines the next target coordinate for a shot.
     * @param state The current state of the game for context.
     * @return The target Point on the opponent's grid, or null if waiting for UI input.
     */
    Point chooseMove(GameState state);
    
    /**
     * Retrieves the reasoning strategy currently assigned to the player.
     * @return The Reasoner instance, or null if none is assigned.
     */
    Optional<Reasoner> getReasoner();

    /**
     * Assigns a reasoning strategy to the player.
     * This can be used to set AI difficulty or to provide hints to a human player.
     * @param reasoner The strategy engine to be used.
     */
    void setReasoner(Reasoner reasoner);
}

