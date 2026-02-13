package player;

import ai.Reasoner;
import model.Grid;

/**
 * Base implementation of the Player interface.
 * Provides shared functionality for all players, such as name management, 
 * grid ownership, and AI component storage.
 */
public abstract class AbstractPlayer implements Player {

    protected final String name;
    protected final Grid grid;
    protected Reasoner reasoner;
    
    /**
     * Initializes a player with a name and their respective battlefield.
     * @param name The display name of the player.
     * @param grid The grid where this player's ships are located.
     * @throws IllegalArgumentException if the name is blank or the grid is null.
     */
    public AbstractPlayer(String name, Grid grid) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }
        this.name = name;
        this.grid = grid;
    }
    
    @Override
    public Grid getGrid() {
        return this.grid;
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * Assigns a brain (logic engine) to this player.
     * @param reasoner The strategy implementation to be used.
     */
    public void setReasoner(Reasoner reasoner) {
        this.reasoner = reasoner;
    }

    @Override
    public Reasoner getReasoner() {
        return this.reasoner;
    }

    // chooseMove remains abstract, as it must be implemented by specific player types.
}
