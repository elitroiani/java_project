package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.CellState;
import model.GameConfig;
import model.GameState;
import model.Grid;
import model.Ship;
import player.Player;

public class ExpertReasoner extends AbstractReasoner {

    private final double[][] probabilityGrid;
    private final int width;
    private final int height;
    private final Random random = new Random();

    public ExpertReasoner(Player player, GameConfig config, GameState state) {
        super(player, config, state);
        this.width = config.getWidth();
        this.height = config.getHeight();
        this.probabilityGrid = new double[width][height];
    }

    @Override
    public Point chooseMove() {
        Grid grid = state.getEnemyGrid(player);
        updateProbability(grid);

        double max = Double.NEGATIVE_INFINITY;
        List<Point> candidates = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (grid.getCellState(x, y) != CellState.NOTFIRED)
                    continue;

                double value = probabilityGrid[x][y];

                if (value > max) {
                    max = value;
                    candidates.clear();
                    candidates.add(new Point(x, y));
                } else if (value == max) {
                    candidates.add(new Point(x, y));
                }
            }
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private void updateProbability(Grid grid) {

        // reset heatmap
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                probabilityGrid[x][y] = 0;

        // navi nemiche ancora vive
        List<Ship> remainingShips = state.enemyShipsRemaining(player);

        for (Ship ship : remainingShips) {
            int size = ship.getSize();

            // ORIZZONTALE
            for (int y = 0; y < height; y++) {
                for (int x = 0; x <= width - size; x++) {

                    if (canPlaceHorizontal(grid, x, y, size)) {
                        for (int i = 0; i < size; i++)
                            probabilityGrid[x + i][y]++;
                    }
                }
            }

            // VERTICALE
            for (int x = 0; x < width; x++) {
                for (int y = 0; y <= height - size; y++) {

                    if (canPlaceVertical(grid, x, y, size)) {
                        for (int i = 0; i < size; i++)
                            probabilityGrid[x][y + i]++;
                    }
                }
            }
        }
    }

    private boolean canPlaceHorizontal(Grid grid, int x, int y, int size) {
        for (int i = 0; i < size; i++) {
            CellState state = grid.getCellState(x + i, y);
            if (state == CellState.MISS)
                return false;
        }
        return true;
    }

    private boolean canPlaceVertical(Grid grid, int x, int y, int size) {
        for (int i = 0; i < size; i++) {
            CellState state = grid.getCellState(x, y + i);
            if (state == CellState.MISS)
                return false;
        }
        return true;
    }
}
