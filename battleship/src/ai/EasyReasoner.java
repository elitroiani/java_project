package ai;

import java.awt.Point;

import model.GameConfig;
import model.GameState;
import player.Player;

/**
 * Basic AI implementation that provides an "Easy" difficulty level.
 * It simply selects moves at random without any specific tactical logic.
 */
public class EasyReasoner extends AbstractReasoner {

    /**
     * Constructs an EasyReasoner by delegating setup to the abstract parent.
     * @param player The AI player using this reasoner.
     * @param config The current game settings.
     */
    public EasyReasoner(Player player, GameConfig config) {
        super(player, config);
    }

    /**
     * Executes a move by picking a random available cell from the grid.
     * This implementation relies on the base randomCellPicker utility.
     * @param state The current state of the game.
     * @return A random valid Point to attack.
     */
    @Override
    public Point chooseMove(GameState state) {
        // Delegates the selection logic to the random picker defined in AbstractReasoner
        return this.randomCellPicker(state);
    }

}
