package ai;

import java.awt.Point;
import java.util.List;

import model.GameConfig;
import model.GameState;
import model.Grid;
import player.Player;

public class EasyReasoner extends AbstractReasoner{

	public EasyReasoner(Player player, GameConfig config) {
		super(player, config);
	}

	@Override
	public Point chooseMove(GameState state) {
        return this.randomCellPicker(state);// TODO Auto-generated method stub
	}

}
