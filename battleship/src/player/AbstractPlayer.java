package player;

import model.Grid;

public abstract class AbstractPlayer implements Player{

    protected final String name;
    protected final Grid grid;
    
    public AbstractPlayer(String name, Grid grid) {
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
