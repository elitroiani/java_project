package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.GameConfig;
import model.GameState;
import model.Grid;

public class MediumReasoner extends AbstractReasoner{

	public MediumReasoner(GameConfig config) {
		super(config);
	}
	
	public Point chooseMove(GameState state) {

        Grid grid = state.getEnemyGrid();
        List<Point> candidates = new ArrayList<>();

        // 1️⃣ cerca celle adiacenti a HIT
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {

                if (!grid.isCellHit(x, y))
                    continue;

                candidates.addAll(getAdjacentUntouched(grid, x, y));
            }
        }

        // 2️⃣ se trovate, scegli random tra quelle
        if (!candidates.isEmpty()) {
            return candidates.get(random.nextInt(candidates.size()));
        }

        // 3️⃣ fallback → random puro
        return this.randomCellPicker(state); // usa Easy behavior
    }
	

}
