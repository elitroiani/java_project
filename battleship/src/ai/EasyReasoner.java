package ai;

import java.awt.Point;
import java.util.List;

import model.GameConfig;
import model.GameState;
import model.Grid;

public class EasyReasoner extends AbstractReasoner{

	public EasyReasoner(GameConfig config) {
		super(config);
	}

	@Override
	public Point chooseMove(GameState state) {
        return this.randomCellPicker(state);// TODO Auto-generated method stub
	}

}
