package ai;

import java.awt.Point;

import model.GameConfig;
import model.GameState;

public class HardReasoner extends AbstractReasoner{
	
	private int[][] heatmap;

	public HardReasoner(GameConfig config) {
		super(config);
		this.heatmap = new int[config.getWidth()][config.getHeight()];
	}

	@Override
	public Point chooseMove(GameState state) {
		// TODO Auto-generated method stub
		return null;
	}

}
