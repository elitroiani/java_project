package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.CellState;
import model.GameConfig;
import model.GameState;
import model.Grid;
import player.Player;

public class MediumReasoner extends AbstractReasoner {

    private List<Point> candidates = new ArrayList<>();
    private Point lastHit = null;
    private Direction currentDirection = null;

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    public MediumReasoner(Player player, GameConfig config, GameState state) {
        super(player, config, state);
    }

    @Override
    public Point chooseMove() {
        Grid grid = state.getEnemyGrid(player);

        // Pulisco candidati invalidi
        candidates.removeIf(p -> grid.getCellState(p.x, p.y) != CellState.NOTFIRED);

        // Aggiorno candidati da HIT
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (grid.getCellState(x, y) == CellState.HIT) {
                    candidates.addAll(getAdjacentUntouched(grid, x, y));
                    lastHit = new Point(x, y);
                }
            }
        }

        // Direzione temporanea
        if (currentDirection != null && lastHit != null) {
            Point next = nextInDirection(grid, lastHit, currentDirection);
            if (next != null) {
                candidates.remove(next);
                return next;
            } else currentDirection = null;
        }

        // Scegli tra candidati
        if (!candidates.isEmpty()) {
            Point next = candidates.remove(random.nextInt(candidates.size()));
            if (lastHit != null) updateDirection(lastHit, next);
            return next;
        }

        // Fallback Easy
        return randomCellPicker(state);
    }

    private Point nextInDirection(Grid grid, Point from, Direction dir) {
        int nx = from.x, ny = from.y;

        switch (dir) {
            case UP -> ny--;
            case DOWN -> ny++;
            case LEFT -> nx--;
            case RIGHT -> nx++;
        }

        if (grid.isValidCoordinate(nx, ny) && grid.getCellState(nx, ny) == CellState.NOTFIRED)
            return new Point(nx, ny);

        return null;
    }

    private void updateDirection(Point last, Point next) {
        if (last.x == next.x) {
            currentDirection = (next.y > last.y) ? Direction.DOWN : Direction.UP;
        } else if (last.y == next.y) {
            currentDirection = (next.x > last.x) ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentDirection = null; // diagonale impossibile
        }
    }
}
