package player;

import model.Grid;

public abstract class AbstractPlayer implements Player{

    protected final String name;
    protected final Grid grid;
    
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

	// chooseMove rimane astratto
}
