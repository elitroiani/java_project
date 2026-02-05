package ai;

import java.awt.Point;
import java.util.List;

import model.GameConfig;
import model.GameState;
import model.Grid;
import player.Player;

public class EasyReasoner extends AbstractReasoner{

	public EasyReasoner(Player player, GameConfig config, GameState state) {
		super(player, config, state);
	}

	@Override
	public Point chooseMove() {
        return this.randomCellPicker(state);// TODO Auto-generated method stub
	}

}
